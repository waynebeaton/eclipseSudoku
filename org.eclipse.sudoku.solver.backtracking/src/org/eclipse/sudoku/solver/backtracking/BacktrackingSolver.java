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
package org.eclipse.sudoku.solver.backtracking;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.sudoku.core.exceptions.CannotSolveSudokuBoardException;
import org.eclipse.sudoku.core.models.Cell;
import org.eclipse.sudoku.core.models.SudokuBoard;
import org.eclipse.sudoku.core.solvers.SudokuBoardSolver;

public class BacktrackingSolver implements SudokuBoardSolver {
	public void solve(SudokuBoard board, IProgressMonitor monitor) throws CannotSolveSudokuBoardException {
		try {
			doSolve(board, monitor);
		} catch (CannotFindSolutionException e) {
			throw new CannotSolveSudokuBoardException();
		}
	}

	protected void doSolve(SudokuBoard board, IProgressMonitor monitor) throws CannotFindSolutionException {
		if (monitor.isCanceled()) return;
		monitor.worked(1);
		// If there are no more cells, we're done.
		Cell cell = findEmptyCellWithLeastPossibleValues(board);
		if (cell == null) return;
		
		// Get the list of possible values.
		List<Integer> values = cell.getPossibleValues();
		
		// If there are no possible values, we've hit a roadblock;
		// throw a failure exception.
		if (values.isEmpty()) throw new CannotFindSolutionException();

		// Randomize the values.
		Collections.shuffle(values);
		// Iterate through the possible values and try each of them
		// until one works.
		for (Integer value : values) {
			cell.setValue(value);
			try {
				// Try to generate the rest of the board
				doSolve(board, monitor);
				return; // it worked!
			} catch (CannotFindSolutionException e) {
				// It failed! Try the next value
			}
		}
		// We failed to find a solution. Reset the value of the
		// cell and throw an exception to indicate the failure.
		cell.setValue(0);
		throw new CannotFindSolutionException();
	}

	private Cell findEmptyCellWithLeastPossibleValues(SudokuBoard board) {
		int minimum = 10;
		Cell cell = null;
		for (Cell candidate : board.getCells()) {
			if (!candidate.isEmpty()) continue;
			int count = candidate.getPossibleValues().size();
			if (count < minimum) {
				minimum = count;
				cell = candidate;
			}
		}
		return cell;
	}
}

@SuppressWarnings("serial")
class CannotFindSolutionException extends Exception {
	
}
