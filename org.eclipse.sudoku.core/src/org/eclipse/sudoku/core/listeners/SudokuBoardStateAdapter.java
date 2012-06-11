package org.eclipse.sudoku.core.listeners;

import org.eclipse.sudoku.core.models.Cell;
import org.eclipse.sudoku.core.models.SudokuBoard;

public abstract class SudokuBoardStateAdapter implements SudokuBoardStateListener {

	public void boardCleared(SudokuBoard board) {
		boardChanged(board);
	}

	public void cellChanged(SudokuBoard board, Cell cell, int newValue, int oldValue) {
		boardChanged(board);
	}

	public void cellChanged(SudokuBoard board, Cell cell) {
		boardChanged(board);
	}

	public void boardChanged(SudokuBoard board) {
		
	}

}
