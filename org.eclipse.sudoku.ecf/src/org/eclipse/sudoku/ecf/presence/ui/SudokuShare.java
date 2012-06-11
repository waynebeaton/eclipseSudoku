/****************************************************************************
 * Copyright (c) 2004 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/

package org.eclipse.sudoku.ecf.presence.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.core.util.ICallable;
import org.eclipse.ecf.datashare.AbstractShare;
import org.eclipse.ecf.datashare.IChannelContainerAdapter;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sudoku.core.SudokuCoreActivator;
import org.eclipse.sudoku.core.listeners.SudokuBoardChangeListener;
import org.eclipse.sudoku.core.listeners.SudokuBoardStateListener;
import org.eclipse.sudoku.core.models.Cell;
import org.eclipse.sudoku.core.models.SudokuBoard;
import org.eclipse.sudoku.core.models.SudokuGame;
import org.eclipse.sudoku.ecf.Activator;
import org.eclipse.sudoku.ui.views.SudokuView;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * 
 */
public class SudokuShare extends AbstractShare {

	private static final int BOARD_ROWS = 9;
	private static final int BOARD_COLS = 9;
	
	protected static final String SUDOKU_VIEW_ID = "org.eclipse.sudoku.ui.views.SudokuView";

	private static final int OPEN_MESSAGE = 0;
	private static final int SYNC_MESSAGE = 1;
	private static final int CELL_MESSAGE = 2;

	protected SudokuGame game;
	protected Cell remotelyChangedCell;

	protected ID local;
	protected ID other;

	SudokuBoardStateListener boardStateListener = new ShareSudokuBoardStateListener();
	SudokuBoardChangeListener boardChangeListener = new SudokuBoardChangeListener() {
		public void boardChanged(SudokuBoard newBoard, SudokuBoard oldBoard) {
			oldBoard.removeSudokuBoardStateListener(boardStateListener);
			newBoard.addSudokuBoardStateListener(boardStateListener);
		}
	};

	public SudokuShare(IChannelContainerAdapter adapter, ID localID)
			throws ECFException {
		super(adapter);
		this.local = localID;
		Assert.isNotNull(this.local);
		this.game = SudokuCoreActivator.getGame();
		Assert.isNotNull(game);
		// Set the game to use the given channel for sending
		game.getBoard().addSudokuBoardStateListener(boardStateListener);
		game.addSudokuBoardChangeListener(boardChangeListener);
	}

	private void logError(String exceptionString, Throwable e) {
		Activator.getDefault().getLog().log(
				new Status(IStatus.ERROR, Activator.PLUGIN_ID, IStatus.ERROR,
						exceptionString, e));
	}

	protected void startPlaying(ID targetID) {
		this.other = targetID;
		try {
			sendMessage(this.other, serialize(OPEN_MESSAGE, local));
			sendSyncGameState();
			openLocalView();
		} catch (Exception e) {
			logError("sendOpenSudoku", e);
		}
	}

	private void sendSyncGameState() {
		try {
			sendMessage(this.other, serialize(SYNC_MESSAGE, writeGameState()));
		} catch (Exception e) {
			logError("sendSynchGameState", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ecf.datashare.AbstractShare#handleMessage(org.eclipse.ecf.core.identity.ID,
	 *      byte[])
	 */
	protected void handleMessage(ID fromContainerID, byte[] data) {
		try {
			Object[] odata = deserialize(data);
			int msg = ((Integer) odata[0]).intValue();
			switch (msg) {
			case OPEN_MESSAGE:
				handleOpenMessage((ID) odata[1]);
				break;
			case SYNC_MESSAGE:
				handleSyncMessage((StringBuilder) odata[1]);
				break;
			case CELL_MESSAGE:
				handleCellMessage((String) odata[1]);
				break;
			default:
				throw new UnsupportedOperationException(NLS.bind(
						"msg {0} not recognized", String.valueOf(msg)));
			}
		} catch (Exception e) {
			logError("deserialization exception", e);
		}
	}

	/**
	 * @param param
	 */
	private void handleCellMessage(final String param) {
		asyncExec(new ICallable() {
			public Object call() throws Throwable {
				StringTokenizer st = new StringTokenizer(param,"-");
				int row = Integer.valueOf(st.nextToken());
				int col = Integer.valueOf(st.nextToken());
				int value = Integer.valueOf(st.nextToken());
				remotelyChangedCell = game.getBoard().getCell(row, col);
				game.getBoard().setValue(row, col, value);
				return null;
			}
		});
	}

	/**
	 * @param param
	 */
	private void handleSyncMessage(final StringBuilder board) {
		asyncExec(new ICallable() {
			public Object call() throws Throwable {
				game.createNewBoard(board);
				return null;
			}
		});
	}

	private StringBuilder writeGameState() {
		StringBuilder builder = new StringBuilder();
		SudokuBoard board = game.getBoard();
		for (int row = 0; row < BOARD_ROWS; row++) {
			for (int column = 0; column < BOARD_COLS; column++) {
				Cell cell = board.getCell(row, column);
				if (cell.isGiven())
					builder.append('*');
				builder.append(cell.getValue());
			}
		}
		return builder;
	}

	private void asyncExec(final ICallable callable) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					callable.call();
				} catch (Throwable e) {
					logError("asyncExec", e);
				}
			}
		});
	}

	private void openLocalView() {
		asyncExec(new ICallable() {
			public Object call() throws Exception {
				SudokuView view = (SudokuView) PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage().showView(
								SUDOKU_VIEW_ID);
				// XXX want to set view title here
				return view;
			}
		});
	}

	/**
	 * @param param
	 */
	private void handleOpenMessage(final ID otherID) {
		this.other = otherID;
		openLocalView();
	}

	protected byte[] serialize(int msg, Object o) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(new Object[] { new Integer(msg), o });
		return bos.toByteArray();
	}

	protected Object[] deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bins = new ByteArrayInputStream(bytes);
		ObjectInputStream oins = new ObjectInputStream(bins);
		return (Object[]) oins.readObject();
	}

	public synchronized void dispose() {
		if (game != null) {
			game.getBoard().removeSudokuBoardStateListener(boardStateListener);
			game.removeSudokuBoardChangeListener(boardChangeListener);
			game = null;
			remotelyChangedCell = null;
			local = null;
		}
		super.dispose();
	}

	class ShareSudokuBoardStateListener implements SudokuBoardStateListener {

		private static final long serialVersionUID = -8298618268317224467L;

		public void cellChanged(SudokuBoard board, Cell cell, int newValue,
				int oldValue) {
			try {
				if (remotelyChangedCell == null)
					sendMessage(SudokuShare.this.other, serialize(CELL_MESSAGE,
							cell.row + "-" + cell.column + "-" + newValue));
				else
					remotelyChangedCell = null;
			} catch (Exception e) {
				logError("cellChanged", e);
			}
		}

		public void cellChanged(SudokuBoard board, Cell cell) {
		}

		public void boardChanged(SudokuBoard board) {
		}

		public void boardCleared(SudokuBoard board) {
		}
	}
}
