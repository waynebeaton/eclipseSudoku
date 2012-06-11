/*******************************************************************************
 * Copyright (c) 2006 George Koch.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    George Koch - initial API and implementation
 *******************************************************************************/
package com.koch.sudoku.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Class for the puzzle generator. It can generate two levels of puzzles.
 * 
 *  <ul>
 *  <li>easy - starting from an empty board sets numbers to cells until the puzzle has unique solution.</li>
 *  <li>hard - starting from an easy board, eliminates the numbers that are not necessary for the uniqueness</li>
 *  </ul>
 *
 *  <p>Not thread safe.</p>
 *  
 * @author George Koch
 */
public class Generator {
    private static final Random RND = new Random(System.currentTimeMillis());
    private Board board;
    private final List<Movement> moves;
    private final List<Set<Movement>> undoMoves;

    public Generator() {
        moves = new ArrayList<Movement>(81);
        undoMoves = new ArrayList<Set<Movement>>(81);
    }

    /**
     * Generates a puzzle.
     */
    public Board generate(final boolean easy) {
        // start with an empty board.
    	this.board = new Board();
        moves.clear();
        undoMoves.clear();
        // the first 17 movement probably won't result unique solution
        for (int i = 0; i < 17; i++) {
            moveOneStep();
        }
        // check if the board is solvable, rollback until it is
        final Solver solver = new Solver(board);
        Solver.Solvability solvability = solver.getSolvability();
        while (solvability == Solver.Solvability.NOT_SOLVABLE) {
            undoOneStep();
            solvability = solver.getSolvability();
        }
        // loop until we have a unique solution
        while (solvability != Solver.Solvability.DONE) {
            moveOneStep();
            solvability = solver.getSolvability();
            // if necessary, change it to another one that doesn't break solvability
            while (solvability == Solver.Solvability.NOT_SOLVABLE) {
                undoOneStep();
                moveOneStep();
                solvability = solver.getSolvability();
            }
        }
        if (!easy) {
        	// in hard mode we have to eliminate the extra numbers to get a minimal puzzle
            final Iterator<Movement> iter = moves.iterator();
            while (iter.hasNext()) {
                final Movement baseMove = iter.next();
                // create a board without the current movement
                final Board testBoard = new Board();
                for (Movement move : moves) {
                    if (!move.equals(baseMove)) {
                        testBoard.move(move);
                    }
                }
                // check if it still has unique solution
                final Solver testSolver = new Solver(testBoard);
                solvability = testSolver.getSolvability();
                assert solvability != Solver.Solvability.NOT_SOLVABLE;
                if (solvability == Solver.Solvability.DONE) {
                    iter.remove();
                    board = testBoard;
                }
            }
        }
        return board;
    }

    /**
     * Undo the last movement.
     */
    private void undoOneStep() {
        final Set<Movement> undos = undoMoves.remove(undoMoves.size() - 1);
        board.undo(undos);
    }

    /**
     * Make a random movement.
     */
    private void moveOneStep() {
        boolean found = false;
        while (!found) {
            final int next = getRandomCellIndex();
            final int x = next % 9;
            final int y = next / 9;
            final Cell cell = board.getCell(x, y);
            if (cell.getValue() == 0) {
                // yet to fill
                final int count = cell.getAvailableNumberCount();
                final int number = cell.getAvailableNumber(RND.nextInt(count));
                final Movement move = new Movement(x, y, number);
                final Set<Movement> undos = board.move(move);
                if (isSolvable()) {
                    found = true;
                    moves.add(move);
                    undoMoves.add(undos);
                } else {
                    board.undo(undos);
                }
            }
        }
    }

    private int getRandomCellIndex() {
        final int availableNumberCount = getMaxAvailableNumberCount();
        assert availableNumberCount != 0;
        int next = RND.nextInt(81);
        for (int i = 0; i < 81; i++, next++) {
            if (next >= 81) {
                next = 0;
            }
            final int x = next % 9;
            final int y = next / 9;
            final Cell cell = board.getCell(x, y);
            if (cell.getAvailableNumberCount() == availableNumberCount) {
                break;
            }
        }
        return next;
    }

    private int getMaxAvailableNumberCount() {
        int maxAvailableNumberCount = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                final Cell cell = board.getCell(i, j);
                if (cell.getValue() == 0) {
                    int availableNumberCount = cell.getAvailableNumberCount();
                    if (availableNumberCount > maxAvailableNumberCount) {
                        maxAvailableNumberCount = availableNumberCount;
                    }
                }
            }
        }
        return maxAvailableNumberCount;
    }

    /**
     * Returns false iff the board is obviously not solvable.
     * 
     * <p>Only checks whether all the cells has values of at least one available numbers.</p>
     * 
     * <p>This method cannot be used to decide if the board is really solvable. It is only a rough, but quick estimate
     * to detect obvious problems.</p>
     *  
     * @return true iff the board seems to be solvable
     */
    private boolean isSolvable() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                final Cell cell = board.getCell(i, j);
                if (cell.getValue() == 0 && cell.getAvailableNumberCount() == 0)
                {
                    return false;
                }
            }
        }
        return true;
    }
}