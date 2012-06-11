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


import junit.framework.TestCase;

import org.eclipse.sudoku.core.SudokuCoreActivator;
import org.eclipse.sudoku.core.models.SudokuBoard;

public class SudokuGameTests extends TestCase {
	SudokuBoard expected = new SudokuBoard(new int[][] {
			{0,6,0, 1,0,4, 0,5,0},
			{0,0,8, 3,0,5, 6,0,0},
			{2,0,0, 0,0,0, 0,0,1},
			
			{8,0,0, 4,0,7, 0,0,6},
			{0,0,6, 0,0,0, 3,0,0},
			{7,0,0, 9,0,1, 0,0,4},
			
			{5,0,0, 0,0,0, 0,0,2},
			{0,0,7, 2,0,6, 9,0,0},
			{0,4,0, 5,0,8, 0,7,0}
	});
	
	public void testNewBoard() throws Exception {
		SudokuCoreActivator.getGame().createNewBoard();
		assertEquals(expected, SudokuCoreActivator.getGame().getBoard());
	}
}

