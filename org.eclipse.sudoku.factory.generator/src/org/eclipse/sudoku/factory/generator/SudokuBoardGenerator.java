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
package org.eclipse.sudoku.factory.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.sudoku.core.exceptions.CannotSolveSudokuBoardException;
import org.eclipse.sudoku.core.models.Cell;
import org.eclipse.sudoku.core.models.Row;
import org.eclipse.sudoku.core.models.SudokuBoard;
import org.eclipse.sudoku.solver.backtracking.BacktrackingSolver;

public class SudokuBoardGenerator {

	public SudokuBoard generate() {
		return generate(3);
	}
	
	/**
	 * This method creates a new Sudoku board at the specified
	 * level of difficulty.
	 * @param difficulty Smaller values indicate a higher level
	 * of difficulty.
	 * @return an instance of {@link SudokuBoard}.
	 */
	public SudokuBoard generate(int difficulty) {
		difficulty = Math.min(difficulty, 9);
		
		// Create a new board and solve it.
		SudokuBoard board = new SudokuBoard();
		
		try {
			new BacktrackingSolver().solve(board, new NullProgressMonitor());
		} catch (CannotSolveSudokuBoardException e) {
			// Shouldn't happen.
		}

		makeGivenCells(difficulty, board);
		
		// Clear the board. This will revert all non-static
		// cells to the empty state.
		board.clear();
		
		return board;
	}

	private void makeGivenCells(int difficulty, SudokuBoard board) {
//		int totalStaticCells = difficulty * 9;
//		int[] staticCellsInRows = new int[9];
//		if (difficulty != 9) {
//			for (int currentRowNumber =  0; currentRowNumber < 8; currentRowNumber++) {
//				int lowerBound = ((int) Math.ceil(Math.max((double)1, ((double)totalStaticCells / (double)(9 - currentRowNumber))))) - 1;
//				int upperBound = (int) Math.ceil(Math.min(9, (totalStaticCells/2)));
//				if (lowerBound != upperBound) {
//					staticCellsInRows[currentRowNumber] = new Random().nextInt(upperBound - lowerBound) + lowerBound;				
//				}
//				else staticCellsInRows[currentRowNumber] = upperBound;
//				totalStaticCells -= staticCellsInRows[currentRowNumber];
//			}
//			staticCellsInRows[8] = totalStaticCells;
//		}
//		else {
//			for (int currentRowNumber = 0; currentRowNumber < 9; currentRowNumber++) {
//				staticCellsInRows[currentRowNumber] = 9;
//			}
//		}
		
		// Get the rows from the board (make a copy).
		List<Row> rows = new ArrayList<Row>();
		rows.addAll(board.getRows());
		
		// Randomize the rows.
		Collections.shuffle(rows);
		for (int index = 0; index < 9; index++) {
			Row row = rows.get(index);
			// Get the cells for the row. Make the nth
			// cell static.
			List<Cell> cells = row.getCells();
			cells.remove(index).makeGiven();

			// Shuffle the cells
			// Mark additional (difficulty-1) cells as static
			Collections.shuffle(cells);
			for (int count = 1; count < difficulty; count++) {
				cells.remove(0).makeGiven();
			}
		}
	}
}
