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
package org.eclipse.sudoku.core.factories;

import org.eclipse.sudoku.core.exceptions.CannotCreateSudokuBoardException;
import org.eclipse.sudoku.core.models.SudokuBoard;

public interface SudokuBoardFactory {

	SudokuBoard createNewBoard() throws CannotCreateSudokuBoardException;

}
