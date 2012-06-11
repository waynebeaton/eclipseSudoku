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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Platform;
import org.eclipse.sudoku.core.exceptions.CannotCreateSudokuBoardException;
import org.eclipse.sudoku.core.exceptions.SudokuBoardFactoryUnavailableException;
import org.eclipse.sudoku.core.internal.Factory;
import org.eclipse.sudoku.core.internal.Solver;
import org.eclipse.sudoku.core.listeners.SudokuBoardChangeListener;

public class SudokuGame {
	private SudokuBoard board;
	private ListenerList boardChangeListenerList = new ListenerList();
	
	public SudokuGame(String storedBoard) {
		setBoard(storedBoard);
	}	

	/**
	 * This method replaces the existing board with a new one generated using
	 * a randomly selected factory. If no factories exist, then nothing happens.
	 * @throws SudokuBoardFactoryUnavailableException
	 */
	public void createNewBoard() throws SudokuBoardFactoryUnavailableException {
		List<Factory> factories = getFactories();
		Collections.shuffle(factories);
		for (Factory factory : factories) {
			try {
				createNewBoard(factory);
			} catch (CannotCreateSudokuBoardException e) {
				// If an exception occurs, try the next factory.
			} catch (SudokuBoardFactoryUnavailableException e) {
				// If an exception occurs, try the next factory.
			}
		}
		throw new SudokuBoardFactoryUnavailableException();
	}
	
	public void createNewBoard(Factory factory) throws SudokuBoardFactoryUnavailableException, CannotCreateSudokuBoardException {
		setBoard(factory.createNewBoard());
	}
	
	public void createNewBoard(StringBuilder board) {
		setBoard(createBoardFromString(board.toString()));
	}

	private void setBoard(String storedBoard) {
		SudokuBoard board = createBoardFromString(storedBoard);
		if (board == null) board = createDefaultBoard();
		setBoard(board);
	}

	private void setBoard(SudokuBoard board) {
		SudokuBoard oldBoard = this.board;
		this.board = board;
		
		for (Object object : boardChangeListenerList.getListeners()) {
			SudokuBoardChangeListener listener = (SudokuBoardChangeListener)object;
			listener.boardChanged(this.board, oldBoard);
		}
	}

	private SudokuBoard createBoardFromString(String storedBoard) {
		if (storedBoard.length() == 0) return null;
		try {
			StringReader reader = new StringReader(storedBoard);
			SudokuBoard board = new SudokuBoard();
			for (int row = 0; row < 9; row++) {
				for (int column = 0; column < 9; column++) {
					Cell cell = board.getCell(row, column);
					char next = (char) reader.read();
					boolean isGiven = next == '*';
					if (isGiven) next = (char) reader.read();
					int value = Character.getNumericValue(next);
					cell.setValue(value);
					if (isGiven) cell.makeGiven();
				}
			}
			return board;
		} catch (Exception e) {
			return null;
		}		
	}

	public String getBoardStateStorage() {
		StringBuilder builder = new StringBuilder();

		for (int row = 0; row < 9; row++) {
			for (int column = 0; column < 9; column++) {
				Cell cell = board.getCell(row, column);
				if (cell.isGiven()) builder.append('*');
				builder.append(cell.getValue());
			}
		}
		return builder.toString();
	}
	
	public SudokuBoard getBoard() {
		return board;
	}
	
	public List<Factory> getFactories() {
		List<Factory> factories = new ArrayList<Factory>();
		for (IConfigurationElement element : getFactoryExtensionPoint().getConfigurationElements()) {
			if (!"factory".equals(element.getName())) continue;
			String name = element.getAttribute("name");
			factories.add(new Factory(name, element));
		}
		return factories;
	}

	protected IExtensionPoint getFactoryExtensionPoint() {
		return Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.sudoku.core.factory");
	}

	protected IExtensionPoint getSolverExtensionPoint() {
		return Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.sudoku.core.solver");
	}
	
	public void addSudokuBoardChangeListener(SudokuBoardChangeListener boardChangeListener) {
		boardChangeListenerList.add(boardChangeListener);
	}

	public void removeSudokuBoardChangeListener(SudokuBoardChangeListener boardChangeListener) {
		boardChangeListenerList.remove(boardChangeListener);
	}

	private SudokuBoard createDefaultBoard() {
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

	public List<Solver> getSolvers() {
		List<Solver> solvers = new ArrayList<Solver>();
		for (IConfigurationElement element : getSolverExtensionPoint().getConfigurationElements()) {
			if (!"solver".equals(element.getName())) continue;
			String name = element.getAttribute("name");
			solvers.add(new Solver(name, element));
		}
		return solvers;
	}
}
