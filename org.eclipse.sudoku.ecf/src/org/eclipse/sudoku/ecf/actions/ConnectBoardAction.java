package org.eclipse.sudoku.ecf.actions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerListener;
import org.eclipse.ecf.core.events.IContainerConnectedEvent;
import org.eclipse.ecf.core.events.IContainerEvent;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.datashare.IChannel;
import org.eclipse.ecf.datashare.IChannelContainerAdapter;
import org.eclipse.ecf.datashare.IChannelListener;
import org.eclipse.ecf.datashare.events.IChannelEvent;
import org.eclipse.ecf.datashare.events.IChannelMessageEvent;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.sudoku.core.SudokuCoreActivator;
import org.eclipse.sudoku.core.listeners.SudokuBoardChangeListener;
import org.eclipse.sudoku.core.listeners.SudokuBoardStateListener;
import org.eclipse.sudoku.core.models.Cell;
import org.eclipse.sudoku.core.models.SudokuBoard;
import org.eclipse.sudoku.core.models.SudokuGame;
import org.eclipse.sudoku.ecf.Activator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class ConnectBoardAction implements IWorkbenchWindowActionDelegate  {

	public static ConnectBoardAction INSTANCE = new ConnectBoardAction();
	private SudokuGame game = SudokuCoreActivator.getGame();

	private static final String CONTAINER_TYPE = "ecf.generic.client";
	private static final String TARGET_SERVER = "ecftcp://ecf.eclipse.org:3282/server";
	private static final String CHANNEL_ID = "soduko";

	private IContainer container = null;
	private IChannel channel = null;

//	public ConnectBoardAction() {
//		setId("sudoku.connect");
//		setText("&Connect");
//		setToolTipText("Multiplayer Soduko");
//		URL url = FileLocator.find(SudokuUiActivator.getDefault().getBundle(), new Path("icons/person.gif"), null);
//		setImageDescriptor(ImageDescriptor.createFromURL(url));
//	}

	public void run(IAction action) {
		Job connectJob = new ClientConnectJob(action, "Connecting...");
		connectJob.schedule();
	}

	class ClientConnectJob extends Job {
		private final IAction action;
		public ClientConnectJob(IAction action, String name) {
			super(name);
			this.action = action;
		}
		public IStatus run(IProgressMonitor pm) {
			try {

				// create a datashare container
				container = ContainerFactory.getDefault().createContainer(CONTAINER_TYPE);

				// Get IChannelContainer adapter
				IChannelContainerAdapter channelContainer = (IChannelContainerAdapter) container
				.getAdapter(IChannelContainerAdapter.class);

				final ID channelID = IDFactory.getDefault().createID(
						channelContainer.getChannelNamespace(), CHANNEL_ID);
				// Setup listener so then when channelmessageevents are received that
				// they present in UI
				final IChannelListener channelListener = new IChannelListener() {
					public void handleChannelEvent(final IChannelEvent event) {
						// handles new boards and cell change messages
						if (event instanceof IChannelMessageEvent) {
							IChannelMessageEvent msg = (IChannelMessageEvent) event;
							ByteArrayInputStream byteInput = new ByteArrayInputStream(msg.getData());
							ObjectInputStream objectInput;
							try {
								objectInput = new ObjectInputStream(byteInput);
								final Object object = objectInput.readObject();
								if(object instanceof String) {
									String cell = (String) object;
									String[] splitString = cell.split("-");
									
									game.getBoard().setValue(
											Integer.valueOf(splitString[0]),
											Integer.valueOf(splitString[1]), 
											Integer.valueOf(splitString[2]));

								}
								if(object instanceof StringBuilder) {
									Display.getDefault().asyncExec(new Runnable() {
									
										public void run() {
											StringBuilder board = (StringBuilder) object;
											game.createNewBoard(board);
										}
									
									});
								}

							} catch (IOException e) {
								e.printStackTrace();
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							}
						}
					}
				};

				// Create new channel
				channel = channelContainer.createChannel(channelID, channelListener, new HashMap());
				
				// hack to sync boards...
				container.addListener(new IContainerListener() {

					public void handleEvent(IContainerEvent evt) {
						if(evt instanceof IContainerConnectedEvent) {
							
							if(container.getConnectedID() == null)
								return;
							
							Display.getDefault().asyncExec(new Runnable() {

								public void run() {
									ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
									ObjectOutputStream objectOut;
									try {
										objectOut = new ObjectOutputStream(byteOut);
										StringBuilder builder = new StringBuilder();
										for (int row = 0; row < 9; row++) {
											for (int column = 0; column < 9; column++) {
												Cell cell = game.getBoard().getCell(row, column);
												if (cell.isGiven()) builder.append('*');
												builder.append(cell.getValue());
											}
										}
										objectOut.writeObject(builder);
										channel.sendMessage(byteOut.toByteArray());
									} catch (IOException e) {
										e.printStackTrace();
									} catch (ECFException e) {
										e.printStackTrace();
									}
								}

							});

						}
					}

				});
					
				final SudokuBoardStateListener listener = new MySudokuBoardStateListener();
				
				// Set the game to use the given channel for sending
				game.getBoard().addSudokuBoardStateListener(listener);
				
				game.addSudokuBoardChangeListener(new SudokuBoardChangeListener() {
					
					public void boardChanged(SudokuBoard newBoard, SudokuBoard oldBoard) {
						oldBoard.removeSudokuBoardStateListener(listener);
						newBoard.addSudokuBoardStateListener(listener);
					}
				
				});

				container.connect(IDFactory.getDefault().createID(
						container.getConnectNamespace(), TARGET_SERVER), null);

				
				action.setEnabled(false);
				return new Status(
						IStatus.OK, 
						Activator.getDefault().getBundle().getSymbolicName(), 
						0, 
						"Connected",
						null);
			} catch (Exception e) {
				return new Status(IStatus.ERROR, Activator.getDefault()
						.getBundle().getSymbolicName(), 0,
						"Could not connect\n\n" + e.getMessage()
						+ "\nSee stack trace in Error Log", e);
			}
		}
	}
	
	class MySudokuBoardStateListener implements SudokuBoardStateListener {
		private static final long serialVersionUID = 8569835801531057746L;
		
		public void cellChanged(SudokuBoard board, Cell cell, int newValue, int oldValue) {
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			ObjectOutputStream objectOut;
			try {
				String cellString = cell.row + "-" + cell.column + "-" + newValue;
				objectOut = new ObjectOutputStream(byteOut);
				objectOut.writeObject(cellString);
				channel.sendMessage(byteOut.toByteArray());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ECFException e) {
				e.printStackTrace();
			}
		}

		public void cellChanged(SudokuBoard board, Cell cell) {}
		public void boardChanged(SudokuBoard board) {}

		/* (non-Javadoc)
		 * @see org.eclipse.sudoku.core.listeners.SudokuBoardStateListener#boardCleared(org.eclipse.sudoku.core.models.SudokuBoard)
		 */
		public void boardCleared(SudokuBoard board) {
			// TODO Auto-generated method stub
			
		}

	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub
		
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}

}
