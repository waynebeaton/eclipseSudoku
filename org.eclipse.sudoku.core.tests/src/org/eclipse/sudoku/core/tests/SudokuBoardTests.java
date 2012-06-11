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
package org.eclipse.sudoku.core.tests;

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.sudoku.core.models.Box;
import org.eclipse.sudoku.core.models.Column;
import org.eclipse.sudoku.core.models.Row;
import org.eclipse.sudoku.core.models.SudokuBoard;

public class SudokuBoardTests extends TestCase {
	public void testEmptyBoardValid() throws Exception {
		SudokuBoard board = new SudokuBoard();
		assertTrue(board.isValid());
	}

	public void testNonemptyBoardValid() throws Exception {
		SudokuBoard board = new SudokuBoard();
		board.setValue(0, 0, 4);
		board.setValue(0, 1, 5);
		assertTrue(board.isValid());
	}

	public void testInvalidRow() throws Exception {
		SudokuBoard board = new SudokuBoard();
		board.setValue(0, 0, 4);
		board.setValue(0, 1, 4);
		assertFalse(board.isValid());
	}

	public void testInvalidColumn() throws Exception {
		SudokuBoard board = new SudokuBoard();
		board.setValue(0, 0, 4);
		board.setValue(1, 0, 4);
		assertFalse(board.isValid());
	}

	public void testInvalidBoxAt0x0() throws Exception {
		SudokuBoard board = new SudokuBoard();
		board.setValue(0, 1, 4);
		board.setValue(1, 0, 4);
		assertFalse(board.isValid());
	}

	public void testInvalidBoxAt0x1() throws Exception {
		SudokuBoard board = new SudokuBoard();
		board.setValue(0, 4, 4);
		board.setValue(1, 3, 4);
		assertFalse(board.isValid());
	}

	public void testInvalidBoxAt0x2() throws Exception {
		SudokuBoard board = new SudokuBoard();
		board.setValue(0, 7, 4);
		board.setValue(1, 6, 4);
		assertFalse(board.isValid());
	}

	public void testInvalidBoxAt1x0() throws Exception {
		SudokuBoard board = new SudokuBoard();
		board.setValue(3, 4, 4);
		board.setValue(4, 3, 4);
		assertFalse(board.isValid());
	}

	public void testInvalidBoxAt1x1() throws Exception {
		SudokuBoard board = new SudokuBoard();
		board.setValue(3, 4, 4);
		board.setValue(4, 3, 4);
		assertFalse(board.isValid());
	}

	public void testInvalidBoxAt1x2() throws Exception {
		SudokuBoard board = new SudokuBoard();
		board.setValue(3, 7, 4);
		board.setValue(4, 6, 4);
		assertFalse(board.isValid());
	}

	public void testInvalidBoxAt2x0() throws Exception {
		SudokuBoard board = new SudokuBoard();
		board.setValue(6, 1, 4);
		board.setValue(7, 0, 4);
		assertFalse(board.isValid());
	}

	public void testInvalidBoxAt2x1() throws Exception {
		SudokuBoard board = new SudokuBoard();
		board.setValue(6, 4, 4);
		board.setValue(7, 3, 4);
		assertFalse(board.isValid());
	}

	public void testInvalidBoxAt2x2() throws Exception {
		SudokuBoard board = new SudokuBoard();
		board.setValue(6, 7, 4);
		board.setValue(7, 6, 4);
		assertFalse(board.isValid());
	}

	public void testFindInvalidBox() {
		SudokuBoard board = new SudokuBoard(new int[][] {
				{ 4, 0, 0, 0, 0, 0, 0, 0, 0 }, 
				{ 0, 4, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 4, 0, 0, 0, 0, 0, 0 }, 
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0 }, 
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0 }, 
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0 } 
			});
		List<Box> boxes = board.getInvalidBoxes();
		assertEquals(1, boxes.size());
		Box box = boxes.iterator().next();
		assertEquals(0, box.getStartRow());
		assertEquals(2, box.getEndRow());
		assertEquals(0, box.getStartColumn());
		assertEquals(2, box.getEndColumn());
	}
	
	public void testFindInvalidBoxes() {
		SudokuBoard board = new SudokuBoard(new int[][] {
				{ 4, 0, 0, 0, 0, 0, 0, 0, 0 }, 
				{ 0, 4, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 4, 0, 0, 0, 0, 0, 0 }, 
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0 }, 
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 4, 0, 0 }, 
				{ 0, 0, 0, 0, 0, 0, 0, 4, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 4 } 
			});
		List<Box> boxes = board.getInvalidBoxes();
		assertEquals(2, boxes.size());
		Iterator<Box> boxIterator = boxes.iterator(); 
		Box box1 = boxIterator.next();
		Box box2 = boxIterator.next();
		assertEquals(0, box1.getStartRow());
		assertEquals(2, box1.getEndRow());
		assertEquals(0, box1.getStartColumn());
		assertEquals(2, box1.getEndColumn());
		assertEquals(6, box2.getStartRow());
		assertEquals(8, box2.getEndRow());
		assertEquals(6, box2.getStartColumn());
		assertEquals(8, box2.getEndColumn());
	}
	
	public void testFindInvalidRows() {
		SudokuBoard board = new SudokuBoard(new int[][] {
				{ 4, 4, 4, 0, 0, 0, 0, 0, 0 }, 
				{ 0, 4, 0, 0, 4, 0, 0, 0, 0 },
				{ 0, 4, 0, 0, 0, 0, 0, 0, 0 }, 
				{ 0, 0, 0, 0, 0, 0, 0, 4, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0 }, 
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 4, 0, 0, 0, 0 }, 
				{ 0, 0, 0, 0, 0, 0, 0, 4, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0 } 
			});
		List<Row> rows = board.getInvalidRows();
		assertEquals(2, rows.size());
		Iterator<Row> rowIterator = rows.iterator(); 
		Row row1 = rowIterator.next();
		Row row2 = rowIterator.next();
		assertEquals(0, row1.getPosition());
		assertEquals(1, row2.getPosition());
	}
	
	public void testFindInvalidColumns() {
		SudokuBoard board = new SudokuBoard(new int[][] {
				{ 4, 4, 4, 0, 0, 0, 0, 0, 0 }, 
				{ 0, 4, 0, 0, 4, 0, 0, 0, 0 },
				{ 0, 4, 0, 0, 0, 0, 0, 0, 0 }, 
				{ 0, 0, 0, 0, 0, 0, 0, 4, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0 }, 
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 4, 0, 0, 0, 0 }, 
				{ 0, 0, 0, 0, 0, 0, 0, 4, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0 } 
			});
		List<Column> columns = board.getInvalidColumns();
		assertEquals(3, columns.size());
		Iterator<Column> columnIterator = columns.iterator(); 
		Column column1 = columnIterator.next();
		Column column2 = columnIterator.next();
		Column column3 = columnIterator.next();
		assertEquals(1, column1.getPosition());
		assertEquals(4, column2.getPosition());
		assertEquals(7, column3.getPosition());
	}
	
	public void testSetStaticCell() {
		SudokuBoard board = new SudokuBoard(new int[][] {
				{ 4, 4, 4, 0, 0, 0, 0, 0, 0 }, 
				{ 0, 4, 0, 0, 4, 0, 0, 0, 0 },
				{ 0, 4, 0, 0, 0, 0, 0, 0, 0 }, 
				{ 0, 0, 0, 0, 0, 0, 0, 4, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0 }, 
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 4, 0, 0, 0, 0 }, 
				{ 0, 0, 0, 0, 0, 0, 0, 4, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0 } 
			});
		board.setValue(0,0, 7);
		assertEquals(4, board.getValue(0,0));
	}	
	
	public void testSetDynamicCell() throws Exception {
		SudokuBoard board = new SudokuBoard(new int[][] {
				{ 4, 4, 4, 0, 0, 0, 0, 0, 0 }, 
				{ 0, 4, 0, 0, 4, 0, 0, 0, 0 },
				{ 0, 4, 0, 0, 0, 0, 0, 0, 0 }, 
				{ 0, 0, 0, 0, 0, 0, 0, 4, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0 }, 
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 0, 0, 0, 4, 0, 0, 0, 0 }, 
				{ 0, 0, 0, 0, 0, 0, 0, 4, 0 },
				{ 0, 0, 0, 0, 0, 0, 0, 0, 0 } 
			});
		board.setValue(0,3, 7);
		assertEquals(7, board.getValue(0,3));
	}
}
