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
package org.eclipse.sudoku.core.listeners;

import java.io.Serializable;

import org.eclipse.sudoku.core.models.Cell;
import org.eclipse.sudoku.core.models.SudokuBoard;

public interface SudokuBoardStateListener extends Serializable {
	/**
	 * Thid method is called when the value of a cell changes. When this method
	 * is called, the state has already changed.
	 * @param board 
	 *           the {@link SudokuBoard} that owns the changed cell.
	 * @param cell
	 *            the {@link Cell} affected by the change.
	 * @param newValue
	 *            the old value of the cell.
	 * @param oldValue
	 *            the new value of the cell.
	 */
	public void cellChanged(SudokuBoard board, Cell cell, int newValue, int oldValue);

	/**
	 * This method is called when the board is cleared
	 * 
	 * @param board
	 *            the {@link SudokuBoard} that was cleared.
	 */
	public void boardCleared(SudokuBoard board);

	/**
	 * This method is called if a property (other than the value) of the cell
	 * changes. When this method is called, the state has already changed.
	 * @param board 
	 *            the {@link SudokuBoard} that owns the changed cell.
	 * @param cell
	 *            the {@link Cell} that changed.
	 */
	public void cellChanged(SudokuBoard board, Cell cell);

}
