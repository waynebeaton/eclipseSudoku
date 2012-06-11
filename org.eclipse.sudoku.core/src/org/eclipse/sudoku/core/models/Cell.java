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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Cell implements Serializable {

	private static final long serialVersionUID = 7719346602447818253L;
	private int value;
	public final int row;
	public final int column;
	private final SudokuBoard board;
	private CellStrategy strategy;
	private boolean marked = false;

	/**
	 * Define a strategy for changeable cells. This object defines how
	 * a cell instance will repond to requests to change its state.
	 * Changeable cells simply update their value when asked.
	 */
	private static final CellStrategy changeableStrategy = new CellStrategy() {
		private static final long serialVersionUID = -4803892363760279625L;

		public void setValue(Cell cell, int value) {
			cell.value = value;
		}

		@Override
		public void clear(Cell cell) {
			int oldValue = cell.value;
			cell.value = 0;
			cell.board.notifyCellChanged(cell, 0, oldValue);
		}
	};

	/**
	 * Define a strategy for given cells. This object defines how
	 * a cell instance will repond to requests to change its state.
	 * Given cells cannot change their value and simply ignore requests
	 * to change.
	 */
	private static final CellStrategy givenStrategy = new CellStrategy() {
		private static final long serialVersionUID = 1299406765112151675L;

		public void setValue(Cell cell, int value) {
			// Do nothing.
		}

		@Override
		public void clear(Cell cell) {
			// Do nothing.
		}
	};
	
	protected Cell(int row, int column, SudokuBoard board, int value, CellStrategy strategy) {
		this.row = row;
		this.column = column;
		this.board = board;
		this.value = value;
		this.strategy = strategy;
	}
	
	public static Cell createChangeable(int row, int column, SudokuBoard board) {
		return new Cell(row, column, board, 0, changeableStrategy);
	}

	public static Cell createGiven(int row, int column, SudokuBoard board, int value) {
		return new Cell(row, column, board, value, givenStrategy);
	}
	
	public int getValue() {
		return this.value;
	}

	public void setValue(int value) {
		int oldValue = value;
		strategy.setValue(this, value);	
		board.notifyCellChanged(this, value, oldValue);
	}

	/**
	 * This method returns all the possible values for this cell
	 * based on the current state of the board. 
	 * 
	 * @return a {@link List} containing possible Integer values 
	 * for this cell.
	 */
	public List<Integer> getPossibleValues() {
		List<Integer> values = new ArrayList<Integer>();
		for(int index=1;index<10;index++) values.add(index);
		
		values.removeAll(getRow().getValues());
		values.removeAll(getColumn().getValues());
		values.removeAll(getBox().getValues());
		
		return values;
	}

	private Box getBox() {
		return board.getBoxContainingCell(row, column);
	}

	private Column getColumn() {
		return board.getColumn(column);
	}

	private Row getRow() {
		return board.getRow(row);
	}

	public boolean isEmpty() {
		return value == 0;
	}

	@Override
	public String toString() {
		return "(" + row + "," + column + ") = " + value;
	}

	/**
	 * This method makes the receiver into a "given" cell.
	 * Effectively, this makes the cell identify itself as 
	 * given and prevents any future changes to the cell's value.
	 *
	 */
	public void makeGiven() {
		strategy = givenStrategy;
	}
	
	/**
	 * This method clears the cell, effectively resetting the
	 * cell to its original state.
	 *
	 */
	public void clear() {
		strategy.clear(this);
	}

	public boolean isGiven() {
		return strategy == givenStrategy;
	}

	/**
	 * Answers whether or not the receiver is valid. A cell is
	 * considered valid if the row, column, and box it occurs
	 * in are all valid (i.e. if any one of them is invalid, so 
	 * is the cell).
	 * 
	 * TODO This implementation is grossly inefficient. Profile and tune.
	 * 
	 * @return true if the cell is valid, false otherwise.
	 */
	public boolean isValid() {
		if (!getRow().isValid()) return false;
		if (!getColumn().isValid()) return false;
		if (!getBox().isValid()) return false;
		return true;
	}

	/**
	 * This method marks the cell. This is generally done so that
	 * the cell can be drawn in a different colour or some such thing.
	 */
	public void mark() {
		marked = !marked ;
		board.notifyCellChanged(this);
	}

	public boolean isMarked() {
		return marked;
	}

}

abstract class CellStrategy implements Serializable {
	public abstract void setValue(Cell cell, int value);

	public abstract void clear(Cell cell);
}
