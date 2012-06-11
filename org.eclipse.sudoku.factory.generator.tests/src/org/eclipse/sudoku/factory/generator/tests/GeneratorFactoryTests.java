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
package org.eclipse.sudoku.factory.generator.tests;

import org.eclipse.sudoku.core.models.SudokuBoard;
import org.eclipse.sudoku.factory.generator.SudokuBoardGenerator;

import junit.framework.TestCase;

public class GeneratorFactoryTests extends TestCase {
	public void testDifficulty1() throws Exception {
		SudokuBoard board = new SudokuBoardGenerator().generate(1);
		assertEquals(9, board.getNonEmptyCells().size());
		assertTrue(board.isValid());
	}
	
	public void testDifficulty2() throws Exception {
		SudokuBoard board = new SudokuBoardGenerator().generate(2);
		assertEquals(18, board.getNonEmptyCells().size());
		assertTrue(board.isValid());
	}
	
	public void testDifficulty3() throws Exception {
		SudokuBoard board = new SudokuBoardGenerator().generate(3);
		assertEquals(27, board.getNonEmptyCells().size());
		assertTrue(board.isValid());
	}
	
	public void testDifficulty5() throws Exception {
		SudokuBoard board = new SudokuBoardGenerator().generate(5);
		assertEquals(45, board.getNonEmptyCells().size());
		assertTrue(board.isValid());
	}
	
	public void testDifficulty7() throws Exception {
		SudokuBoard board = new SudokuBoardGenerator().generate(7);
		assertEquals(63, board.getNonEmptyCells().size());
		assertTrue(board.isValid());
	}
	
	public void testDifficulty8() throws Exception {
		SudokuBoard board = new SudokuBoardGenerator().generate(8);
		assertEquals(72, board.getNonEmptyCells().size());
		assertTrue(board.isValid());
	}
	
	public void testDifficulty9() throws Exception {
		SudokuBoard board = new SudokuBoardGenerator().generate(9);
		assertEquals(81, board.getNonEmptyCells().size());
		assertTrue(board.isValid());
	}
}
