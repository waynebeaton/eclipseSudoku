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
package org.eclipse.sudoku.core.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.sudoku.core.exceptions.CannotSolveSudokuBoardException;
import org.eclipse.sudoku.core.exceptions.SudokuBoardSolverUnavailableException;
import org.eclipse.sudoku.core.models.SudokuBoard;
import org.eclipse.sudoku.core.solvers.SudokuBoardSolver;

public class Solver {

	private final String name;
	private final IConfigurationElement element;

	public Solver(String name, IConfigurationElement element) {
		this.name = name;
		this.element = element;
	}

	public String getName() {
		return name;
	}

	public void solve(SudokuBoard board, IProgressMonitor monitor) throws SudokuBoardSolverUnavailableException, CannotSolveSudokuBoardException {
		if (!board.isValid()) throw new CannotSolveSudokuBoardException();
		getSolver().solve(board, monitor);
	}

	private SudokuBoardSolver getSolver() throws SudokuBoardSolverUnavailableException {
		Object object;
		try {
			object = element.createExecutableExtension("class");
			if (object instanceof SudokuBoardSolver) {
				return (SudokuBoardSolver) object;
			}
		} catch (CoreException e) {
			// Ignore. Exception is thrown in the next statement.
		}
		throw new SudokuBoardSolverUnavailableException();
	}
}
