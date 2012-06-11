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
package org.eclipse.sudoku.factory.samples;

import java.util.Iterator;
import java.util.Random;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.sudoku.core.exceptions.CannotCreateSudokuBoardException;
import org.eclipse.sudoku.core.factories.SudokuBoardFactory;
import org.eclipse.sudoku.core.models.Cell;
import org.eclipse.sudoku.core.models.SudokuBoard;

public class SudokuSampleBoardFactory implements SudokuBoardFactory {

	public SudokuBoard createNewBoard() throws CannotCreateSudokuBoardException {
		IConfigurationElement[] elements = getExtensionPoint().getConfigurationElements();
		if (elements.length == 0) throw new CannotCreateSudokuBoardException();
		IConfigurationElement element = elements[new Random().nextInt(elements.length)];
		return createNewBoard(element.getAttribute("data"));
	}

	private SudokuBoard createNewBoard(String data) {
		Iterator<Integer> contents = split(data);
		SudokuBoard board = new SudokuBoard();
		for (int row=0;row<9;row++) {
			for (int column=0;column<9;column++) {
				int value = contents.next();
				Cell cell = board.getCell(row, column);
				cell.setValue(value);
				if (value != 0) cell.makeGiven();
			}
		}
		return board;
	}

	private Iterator<Integer> split(final String data) {		
		return new Iterator<Integer>() {
			int index=0;
			
			public boolean hasNext() {
				return true;
			}

			public Integer next() {
				if (index > data.length()) return 0;
				return Character.getNumericValue(data.charAt(index++));
			}

			public void remove() {
			}
			
		};
	}

	protected IExtensionPoint getExtensionPoint() {
		return Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.sudoku.factory.samples.sample");
	}
}
