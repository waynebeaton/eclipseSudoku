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

public class Column {
	private final int column;

	private final SudokuBoard board;

	public Column(int position, SudokuBoard board) {
		this.column = position;
		this.board = board;
	}

	public Object getPosition() {
		return column;
	}

	/**
	 * This method returns the list of values represented in the column's cells.
	 * There is no implied order in the returned values.
	 * 
	 * @return
	 */
	public List<Integer> getValues() {
		List<Integer> values = new ArrayList<Integer>();
		for (int row = 0; row < 9; row++) {
			Cell cell = board.getCell(row, column);
			if (!cell.isEmpty())
				values.add(cell.getValue());
		}
		return values;
	}

	/**
	 * This method answers true if the column is valid. A column is considered valid
	 * if all the values in it's cells are unique (i.e. no cell values are
	 * repeated). An incomplete column can be valid.
	 * 
	 * @return true if the column is valid, false otherwise.
	 */
	public boolean isValid() {
		boolean[] values = new boolean[10];
		for (int row = 0; row < 9; row++) {
			Cell cell = board.getCell(row, column);
			if (cell.isEmpty()) continue;
			int value = cell.getValue();
			if (values[value]) return false;
			values[value] = true;
		}
		return true;
	}
}
