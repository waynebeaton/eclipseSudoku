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
package org.eclipse.sudoku.core.models;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.sudoku.core.listeners.SudokuBoardStateListener;

public class SudokuBoard {

	Cell[][] board = new Cell[9][9];
	List<Cell> cells = new ArrayList<Cell>();
	List<Row> rows = new ArrayList<Row>();
	List<Column> columns = new ArrayList<Column>();
	List<Box> boxes = new ArrayList<Box>();
	private ListenerList boardStateListenerList = new ListenerList();

	public SudokuBoard(int[][] board) {
		initialize();
		for (int column = 0; column < 9; column++) {
			for (int row = 0; row < 9; row++) {
				int value = board[row][column];
				Cell cell = null;
				if (value == 0)
					cell = Cell.createChangeable(row, column, this);
				else
					cell = Cell.createGiven(row, column, this, value);
				this.board[row][column] = cell;
				cells.add(cell);
			}
		}
	}

	public SudokuBoard() {
		initialize();
		for (int column = 0; column < 9; column++) {
			for (int row = 0; row < 9; row++) {
				Cell cell = Cell.createChangeable(row, column, this);
				this.board[row][column] = cell;
				cells.add(cell);
			}
		}
	}

	private void initialize() {
		for(int index=0;index<9;index++) {
			rows.add(new Row(index, this));
			columns.add(new Column(index, this));
		}
		for(int x=0;x<3;x++) {
			for(int y=0;y<3;y++) {
				boxes.add(new Box(x * 3, y * 3, x * 3 + 2, y * 3 + 2, this));
			}
		}
	}

	public boolean isValid() {
		if (getInvalidRows().size() > 0) return false;
		if (getInvalidColumns().size() > 0) return false;
		if (getInvalidBoxes().size() > 0) return false;
		
		return true;
	}

	public void setValue(int row, int column, int value) {
		getCell(row, column).setValue(value);
	}

	public List<Box> getInvalidBoxes() {
		List<Box> invalidBoxes = new ArrayList<Box>();
		for (Box box : boxes) {
			if (!box.isValid()) invalidBoxes.add(box);
		}
		return invalidBoxes;
	}
	
	public List<Row> getInvalidRows() {
		List<Row> invalidRows = new ArrayList<Row>();
		for (Row row : rows) {
			if (!row.isValid()) invalidRows.add(row);
		}
		return invalidRows;
	}

	public List<Column> getInvalidColumns() {
		List<Column> invalidColumns = new ArrayList<Column>();
		for (Column column : columns) {
			if (!column.isValid()) invalidColumns.add(column);
		}
		return invalidColumns;
	}

	public int getValue(int row, int column) {
		return getCell(row, column).getValue();
	}

	public boolean isComplete() {
		for (Cell cell : cells) {
			if (cell.isEmpty()) return false;
		}
		return true;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof SudokuBoard) return equals((SudokuBoard)object);
		return false;
	}
	
	protected boolean equals(SudokuBoard other) {
		for(int row=0;row<9;row++) {
			for (int column=0;column<9;column++) {
				if (getValue(row, column) != other.getValue(row, column)) return false;
			}
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		// TODO Make a better hashCode method.
		return 42;
	}

	public Cell getCell(int row, int column) {
		return board[row][column];
	}

	public Box getBoxContainingCell(int row, int column) {
		int x = row / 3;
		int y = column / 3;
		return getBox(x,y);
	}
	
	private Box getBox(int x, int y) {
		return boxes.get(x * 3 + y);
	}

	public Column getColumn(int column) {
		return columns.get(column);
	}

	public Row getRow(int row) {
		return rows.get(row);
	}

	public Cell getCell(int index) {
		return cells.get(index);
	}

	public List<Row> getRows() {
		return rows;
	}

	public void clear() {
		for (Cell cell : cells) {
			cell.clear();
		}
		notifyBoardCleared();
	}

	public List<Cell> getNonEmptyCells() {
		List<Cell> cells = new ArrayList<Cell>();
		for (Cell cell : this.cells) {
			if (!cell.isEmpty()) cells.add(cell);
		}
		return cells;
	}

	public List<Cell> getCells() {
		return cells;
	}

	public List<Box> getBoxes() {
		return boxes;
	}
	
	public void removeSudokuBoardStateListener(SudokuBoardStateListener listener) {
		boardStateListenerList.add(listener);
	}

	public void addSudokuBoardStateListener(SudokuBoardStateListener listener) {
		boardStateListenerList.add(listener);
	}

	protected void notifyCellChanged(Cell cell, int newValue, int oldValue) {
		for (Object object : boardStateListenerList.getListeners()) {
			SudokuBoardStateListener listener = (SudokuBoardStateListener)object;
			listener.cellChanged(this, cell, newValue, oldValue);
		}
	}

	private void notifyBoardCleared() {
		for (Object object : boardStateListenerList.getListeners()) {
			SudokuBoardStateListener listener = (SudokuBoardStateListener)object;
			listener.boardCleared(this);
		}
	}

	protected void notifyCellChanged(Cell cell) {
		for (Object object : boardStateListenerList.getListeners()) {
			SudokuBoardStateListener listener = (SudokuBoardStateListener)object;
			listener.cellChanged(this, cell);
		}		
	}
}
