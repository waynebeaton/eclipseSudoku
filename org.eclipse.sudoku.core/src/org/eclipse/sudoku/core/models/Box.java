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

public class Box {
	public final int startRow, endRow, startColumn, endColumn;

	private final SudokuBoard board;

	public Box(int startRow, int startColumn, int endRow, int endColumn,
			SudokuBoard board) {
		this.startRow = startRow;
		this.startColumn = startColumn;
		this.endRow = endRow;
		this.endColumn = endColumn;
		this.board = board;
	}

	public Object getStartRow() {
		return startRow;
	}

	public Object getEndRow() {
		return endRow;
	}

	public Object getStartColumn() {
		return startColumn;
	}

	public Object getEndColumn() {
		return endColumn;
	}

	/**
	 * This method returns the list of values represented in the box's cells.
	 * There is no implied order in the returned values.
	 * 
	 * @return
	 */
	public List<Integer> getValues() {
		List<Integer> values = new ArrayList<Integer>();
		for (int column = startColumn; column <= endColumn; column++) {
			for (int row = startRow; row <= endRow; row++) {
				Cell cell = board.getCell(row, column);
				if (!cell.isEmpty())
					values.add(cell.getValue());
			}
		}
		return values;
	}

	/**
	 * This method answers true if the box is valid. A box is considered valid
	 * if all the values in it's cells are unique (i.e. no cell values are
	 * repeated). An incomplete box can be valid.
	 * 
	 * @return true if the box is valid, false otherwise.
	 */
	public boolean isValid() {
		boolean[] values = new boolean[10];
		for (int column = startColumn; column <= endColumn; column++) {
			for (int row = startRow; row <= endRow; row++) {
				Cell cell = board.getCell(row, column);
				if (cell.isEmpty()) continue;
				int value = cell.getValue();
				if (values[value]) return false;
				values[value] = true;
			}
		}
		return true;
	}
}
