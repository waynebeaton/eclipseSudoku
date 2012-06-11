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
package org.eclipse.sudoku.core.tests.factory;

import org.eclipse.sudoku.core.models.SudokuBoard;
import org.eclipse.sudoku.core.factories.SudokuBoardFactory;

public class MockSudokuBoardFactory implements SudokuBoardFactory {

	public SudokuBoard createNewBoard() {
		return new SudokuBoard(new int[][] {
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
	}

	public boolean canProvideBoards() {
		return false;
	}

	public String getName() {
		return "Test Factory";
	}

}
