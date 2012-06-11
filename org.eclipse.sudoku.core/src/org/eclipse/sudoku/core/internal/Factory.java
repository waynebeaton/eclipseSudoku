package org.eclipse.sudoku.core.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.sudoku.core.exceptions.CannotCreateSudokuBoardException;
import org.eclipse.sudoku.core.exceptions.SudokuBoardFactoryUnavailableException;
import org.eclipse.sudoku.core.factories.SudokuBoardFactory;
import org.eclipse.sudoku.core.models.SudokuBoard;

public class Factory {

	private final String name;
	private final IConfigurationElement element;

	public Factory(String name, IConfigurationElement element) {
		this.name = name;
		// TODO Auto-generated constructor stub
		this.element = element;
	}

	public String getName() {
		return name;
	}

	public SudokuBoard createNewBoard() throws SudokuBoardFactoryUnavailableException, CannotCreateSudokuBoardException {
		return getFactory().createNewBoard();
	}
	
	protected SudokuBoardFactory getFactory() throws SudokuBoardFactoryUnavailableException {
		try {
			Object object = element.createExecutableExtension("class");
			if (object instanceof SudokuBoardFactory) {
				return (SudokuBoardFactory)object;
			} 
		} catch (CoreException e) {
			// Ignore. Exception is thrown in the next statement.
		}
		throw new SudokuBoardFactoryUnavailableException();
	}
}
