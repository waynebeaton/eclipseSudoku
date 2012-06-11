/*******************************************************************************
 * Copyright (c) 2006 The Eclipse Foundation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Wayne Beaton (The Eclipse Foundation) - initial API and implementation
 *******************************************************************************/
package org.eclipse.sudoku.ui.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.sudoku.core.SudokuCoreActivator;
import org.eclipse.sudoku.core.exceptions.CannotSolveSudokuBoardException;
import org.eclipse.sudoku.core.exceptions.SudokuBoardSolverUnavailableException;
import org.eclipse.sudoku.core.internal.Solver;
import org.eclipse.sudoku.core.listeners.SudokuBoardChangeListener;
import org.eclipse.sudoku.core.listeners.SudokuBoardStateAdapter;
import org.eclipse.sudoku.core.listeners.SudokuBoardStateListener;
import org.eclipse.sudoku.core.models.SudokuBoard;
import org.eclipse.sudoku.core.models.SudokuGame;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate2;

@SuppressWarnings("serial")
public class SolveBoardAction implements IWorkbenchWindowPulldownDelegate2 {
	private Menu menu;

	private Menu dropDown;

	private Solver lastSolver;

	private IWorkbenchWindow window;
	
	/**
	 * This method returns the menu that will be connected into the menu bar.
	 */
	public Menu getMenu(Menu parent) {
		if (menu == null)
			menu = initMenu(new Menu(parent));
		return menu;
	}

	/**
	 * This method returns the menu that will be connected into the toolbar
	 * button.
	 */
	public Menu getMenu(Control parent) {
		if (dropDown == null)
			dropDown = initMenu(new Menu(parent));
		return dropDown;
	}
	/**
	 * This method initializes the provided menu. All this method presently does
	 * is install a new menu listener that is invoked whenever the menu is
	 * opened. When the menu is opened, its contents are rebuilt. This allows us
	 * to dynamically discover any new puzzle solvers that may have been
	 * installed. It's not an expensive operation, so even if this never
	 * happens, it's not that big a deal...
	 * 
	 * @param menu
	 *            the menu to initialize.
	 * @return the menu as a convenience.
	 */
	private Menu initMenu(Menu menu) {
		/*
		 * Rebuild the menu every time it's opened. (this allows us to
		 * dynamically discover any new or removed board solvers).
		 */
		menu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e) {
				Menu menu = (Menu) e.widget;
				clearMenu(menu);
				fillMenu(menu);
			}
		});
		return menu;
	}

	/**
	 * This method clears the provided menu by removing all of its items.
	 * 
	 * @param menu
	 *            the menu to clear.
	 */
	private void clearMenu(Menu menu) {
		for (MenuItem item : menu.getItems()) {
			item.dispose();
		}
	}

	/**
	 * This method fills the provided menu with an entry for each available
	 * puzzle solver.
	 * 
	 * @param menu
	 *            the menu to fill.
	 */
	private void fillMenu(Menu menu) {
		for (final Solver solver : SudokuCoreActivator.getGame().getSolvers()) {
			IAction action = new Action() {
				@Override
				public void run() {
					solve(solver);
				}

				@Override
				public String getText() {
					return solver.getName();
				}
			};
			ActionContributionItem item = new ActionContributionItem(action);
			item.fill(menu, -1);
		}
	}

	/**
	 * When the receiver is disposed, the menus and their items are also
	 * disposed.
	 */
	public void dispose() {
		if (menu != null) {
			clearMenu(menu);
			menu.dispose();
			menu = null;
		}
		if (dropDown != null) {
			clearMenu(dropDown);
			dropDown.dispose();
			dropDown.dispose();
		}
	}
	public void init(IWorkbenchWindow window) {
		this.window = window;
		
	}

	public void run(IAction action) {
		if (lastSolver == null) {
			message("No current solver", "Select a solver from the drop down list.");
			return;
		}
		solve(lastSolver);
	}

	public void selectionChanged(IAction action, ISelection selection) {
		
	}

	protected void solve(final Solver solver) {
		
		lastSolver = solver;
		new Job("Solve puzzle") {
			protected IStatus run(final IProgressMonitor monitor) {				
				SudokuGame game = SudokuCoreActivator.getGame();
				SudokuBoard board = game.getBoard();
								
				SudokuBoardChangeListener boardChangeListener = getBoardChangeListener(monitor);
				SudokuBoardStateListener boardStateListener = getBoardStateListener(monitor);
				
				game.addSudokuBoardChangeListener(boardChangeListener );
				board.addSudokuBoardStateListener(boardStateListener);
				try {
					doJob(solver, monitor, board);
				} finally {
					monitor.done();
					game.removeSudokuBoardChangeListener(boardChangeListener);
					board.removeSudokuBoardStateListener(boardStateListener);
				}
				
				if (monitor.isCanceled()) return Status.CANCEL_STATUS;
				return Status.OK_STATUS;
			}

			private void doJob(final Solver solver, final IProgressMonitor monitor, SudokuBoard board) {
				try {
					monitor.beginTask("Solve", IProgressMonitor.UNKNOWN);
					solver.solve(board, monitor);
				} catch (SudokuBoardSolverUnavailableException e) {
					// TODO Need a better message.
					message(solver.getName(), "The solver is not available.");
				} catch (CannotSolveSudokuBoardException e) {
					// TODO Need a better message.
					message(solver.getName(), "The solver cannot solve this board.");
				}
			}
			
			/**
			 * This listener is added to the SudokuBoard before the solution
			 * is computed. It slows down the screen renderings so that the
			 * the visuals are a little cooler...
			 */
			private SudokuBoardStateListener getBoardStateListener(final IProgressMonitor monitor) {
				return new SudokuBoardStateAdapter() {
					public void boardChanged(SudokuBoard board) {
						try {
							if (!board.isValid()) {
								monitor.setCanceled(true);
							}
							Thread.sleep(200);
						} catch (InterruptedException e) {
						}
					}
				};
			}

			private SudokuBoardChangeListener getBoardChangeListener(final IProgressMonitor monitor) {
				return new SudokuBoardChangeListener() {
					public void boardChanged(SudokuBoard newBoard, SudokuBoard oldBoard) {
						monitor.setCanceled(true);
					}
				};
			};
		}.schedule();

	}

	protected void message(final String title, final String message) {
		window.getShell().getDisplay().syncExec(new Runnable() {
			public void run() {
				MessageDialog.openInformation(window.getShell(), title, message);
			}
		});
	}
}
