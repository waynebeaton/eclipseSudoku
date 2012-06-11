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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.sudoku.core.SudokuCoreActivator;
import org.eclipse.sudoku.core.exceptions.CannotCreateSudokuBoardException;
import org.eclipse.sudoku.core.exceptions.SudokuBoardFactoryUnavailableException;
import org.eclipse.sudoku.core.internal.Factory;
import org.eclipse.sudoku.ui.SudokuUiActivator;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate2;

public class NewBoardAction implements IWorkbenchWindowPulldownDelegate2 {
	private Menu menu;
	private Menu dropDown;
	private IWorkbenchWindow window;
	private Factory lastFactory;
	private IAction action;

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	/**
	 * This method is invoked when the button is pressed (i.e. the user has not
	 * selected from the drop down). In this event, generate a new puzzle by
	 * randomly selecting a generator from those available.
	 */
	public void run(IAction action) {
		this.action = action;
		createNewBoard();
	}

	/**
	 * The workbench selection has changed, and I don't care.
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.action = action;
	}

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
	 * to dynamically discover any new puzzle generators that may have been
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
		 * dynamically discover any new or removed board generators).
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
	 * puzzle generator.
	 * 
	 * @param menu
	 *            the menu to fill.
	 */
	private void fillMenu(Menu menu) {
		for (final Factory factory : SudokuCoreActivator.getGame().getFactories()) {
			IAction action = new Action() {
				@Override
				public void run() {
					createNewBoard(factory);
				}

				@Override
				public String getText() {
					return factory.getName();
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

	/**
	 * This method creates a new puzzle using the provided factory.
	 * 
	 * @param factory
	 *            the factory to use to generate the puzzle.
	 */
	private void createNewBoard(final Factory factory) {
		lastFactory = factory;
		action.setToolTipText("Generate a puzzle using " + lastFactory.getName());
		
		runWithProgress(new Runnable() {
			public void run() {
				try {
					SudokuCoreActivator.getGame().createNewBoard(factory);
				} catch (SudokuBoardFactoryUnavailableException e) {
					message(factory.getName(), "The puzzle generator is not available.");
				} catch (CannotCreateSudokuBoardException e) {
					message(factory.getName(), "A puzzle could not be generated.");
				}
			}
		});
	}

	/**
	 * This method creates a new puzzle using a randomly selected factory.
	 * 
	 * @param factory
	 *            the factory to use to generate the puzzle.
	 */
	private void createNewBoard() {
		if (lastFactory == null) lastFactory = getDefaultFactory();
		if (lastFactory == null) {
			message("No current generator", "Select a generator from the drop down list.");
			return;
		}
		createNewBoard(lastFactory);
	}

	private Factory getDefaultFactory() {
		for (Factory factory : SudokuCoreActivator.getGame().getFactories()) {
			return factory;
		}
		return null;
	}

	/**
	 * This method provides feedback to the user while it runs the provided
	 * {@link Runnable}.
	 * 
	 * @param runnable
	 *            the runnable to run.
	 */
	private void runWithProgress(final Runnable runnable) {
		ProgressMonitorDialog progress = new ProgressMonitorDialog(window
				.getShell());
		try {
			progress.run(false, false, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask("Generating Sudoku board",
							IProgressMonitor.UNKNOWN);
					runnable.run();
					monitor.done();
				}
			});
		} catch (Exception e) {
			SudokuUiActivator.logError(e);
		}
	}

	protected void message(final String title, final String message) {
		window.getShell().getDisplay().syncExec(new Runnable() {
			public void run() {
				MessageDialog.openInformation(window.getShell(), title, message);
			}
		});
	}
}
