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
package org.eclipse.sudoku.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.sudoku.core.models.SudokuGame;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class SudokuCoreActivator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.sudoku.core";

	private static final String STORED_BOARD = "stored_board";
	
	// The shared instance
	private static SudokuCoreActivator plugin;

	public SudokuGame game;
	
	/**
	 * The constructor
	 */
	public SudokuCoreActivator() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		game = new SudokuGame(getPluginPreferences().getString(STORED_BOARD));
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);		

		getPluginPreferences().setValue(STORED_BOARD, game.getBoardStateStorage());
		savePluginPreferences();
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static SudokuCoreActivator getDefault() {
		return plugin;
	}
	public static void logError(Exception e) {
		logError(new Status(IStatus.ERROR, PLUGIN_ID, 0, e.getMessage(), e));
	}

	public static void logError(IStatus status) {
		getDefault().getLog().log(status);
	}

	public static void logError(String message) {
		logError(new Status(IStatus.ERROR, PLUGIN_ID, 0, message, null));
	}

	public static SudokuGame getGame() {
		return getDefault().game;
	}
}
