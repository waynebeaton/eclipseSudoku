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

public class Row {
	private final int row;
	private final SudokuBoard board;

	public Row(int row, SudokuBoard board) {
		this.row = row;
		this.board = board;
	}

	public Object getPosition() {
		return row;
	}

	/**
	 * This method returns the list of values represented in the row's cells.
	 * There is no implied order in the returned values.
	 * 
	 * @return
	 */
	public List<Integer> getValues() {
		List<Integer> values = new ArrayList<Integer>();
		for (int column=0;column<9;column++) {
			Cell cell = board.getCell(row, column);
			if (!cell.isEmpty()) values.add(cell.getValue());
		}
		return values;
	}

	/**
	 * This method answers true if the row is valid. A row is considered valid
	 * if all the values in it's cells are unique (i.e. no cell values are
	 * repeated). An incomplete row can be valid.
	 * 
	 * @return true if the row is valid, false otherwise.
	 */
	public boolean isValid() {
		boolean[] values = new boolean[10];
		for (Cell cell : getCells()) {
			if (cell.isEmpty()) continue;
			int value = cell.getValue();
			if (values[value]) return false;
			values[value] = true;
		}
		return true;
	}

	public List<Cell> getCells() {
		List<Cell> cells = new ArrayList<Cell>();
		for(int column=0;column<9;column++) {
			cells.add(board.getCell(row, column));
		}
		return cells;
	}

	public Cell getCell(int column) {
		return board.getCell(row, column);
	}

}
