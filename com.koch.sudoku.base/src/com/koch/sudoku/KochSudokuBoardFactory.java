/*******************************************************************************
 * Copyright (c) 2006 George Koch.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    George Koch - initial API and implementation
 *******************************************************************************/
package com.koch.sudoku;

import org.eclipse.sudoku.core.models.Cell;
import org.eclipse.sudoku.core.models.SudokuBoard;
import org.eclipse.sudoku.core.factories.SudokuBoardFactory;

import com.koch.sudoku.base.Board;
import com.koch.sudoku.base.Generator;

public class KochSudokuBoardFactory implements SudokuBoardFactory {

	public SudokuBoard createNewBoard() {
		final Board board = new Generator().generate(false);
		final SudokuBoard sudokuBoard = new SudokuBoard();
		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 9; y++) {
				final com.koch.sudoku.base.Cell sourceCell = board.getCell(x, y);
				final Cell targetCell = sudokuBoard.getCell(x, y);
				final int value = sourceCell.getValue();
				targetCell.setValue(value);
				if (value != 0) {
					targetCell.makeGiven();
				}
			}
		}
		return sudokuBoard;
	}
}
