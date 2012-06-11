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

import com.koch.sudoku.simplifiers.BoxSimplifier;
import com.koch.sudoku.simplifiers.CellSimplifier;
import com.koch.sudoku.simplifiers.Naked1Simplifier;
import com.koch.sudoku.simplifiers.NakedNSimplifier;
import com.koch.sudoku.simplifiers.NakedPairsSimplifier;
import com.koch.sudoku.simplifiers.RowAndColumnSimplifier;
import com.koch.sudoku.simplifiers.Simplifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Backtracking puzzle solver. Can solve a puzzle or tell if the puzzle is solvable/not solvable/has more than one
 * solution.
 * 
 * @author George Koch
 */
public class Solver {

    private static final Random RND = new Random(System.currentTimeMillis());

    private static final List<Simplifier> SIMPLIFIERS;

    public enum Solvability {HAS_EMPTY_CELLS, DONE, NOT_SOLVABLE, MULTIPLE_SOLUTIONS}

    private int[][] firstSolutionCells;
    private List<Movement> movements;
    private boolean checkUniqueness;
    private final Board board;

    static {
        SIMPLIFIERS = new ArrayList<Simplifier>();
        SIMPLIFIERS.add(new CellSimplifier());
        SIMPLIFIERS.add(new RowAndColumnSimplifier());
        SIMPLIFIERS.add(new BoxSimplifier());
        SIMPLIFIERS.add(new Naked1Simplifier());
        SIMPLIFIERS.add(new NakedPairsSimplifier());
        SIMPLIFIERS.add(new NakedNSimplifier(3, 8));
    }

    public Solver(final Board board) {
        this.board = board;
    }

    /**
     * Returns the movements to solve the board.
     * 
     * <p>If the board is not solveable, returns an empty list.</p>
     * 
     * @return the list of movements to solve the puzzle
     */
    public List<Movement> solve() {
        startSolve(false);
        return movements;
    }
    
    /**
     * Returns the solvability state of the board.
     */
    public Solvability getSolvability() {
        return startSolve(true);
    }

    private Solvability startSolve(final boolean checkUniqueness) {
        firstSolutionCells = null;
        this.checkUniqueness = checkUniqueness;
        movements = new LinkedList<Movement>();
        board.setSimplifiers(SIMPLIFIERS);
        Solvability result = recursiveSolve();
        if (result == Solvability.NOT_SOLVABLE && firstSolutionCells != null) {
            result = Solvability.DONE;
        }
        return result;
    }

    private Solvability recursiveSolve() {
        if (hasIllegalCell()) {
            return Solvability.NOT_SOLVABLE;
        }
        final Solvability solved = isSolved();
        if (solved != Solvability.HAS_EMPTY_CELLS) {
            return solved;
        }
        // so we have empty cells, let's try to fill one
        final Set<Movement> testedMovements = new HashSet<Movement>();
        final int[][] testedGrid = new int[9][9];
        while (true) {
            // get the next best move
            final Movement nextMove = nextPossibleMovement(testedMovements, testedGrid);
            if (nextMove == null) {
                // we've tried all the possible moves
                return Solvability.NOT_SOLVABLE;
            }
            // move it
            final Set<Movement> undoMoves = board.move(nextMove);
            // store the move
            movements.add(nextMove);
            // call recursion
            final Solvability result = recursiveSolve();
            // undo the move so we'll end up with the original board at the end
            board.undo(undoMoves);
            if (result != Solvability.NOT_SOLVABLE && (!checkUniqueness || result == Solvability.MULTIPLE_SOLUTIONS))
            {
                // if we solved and don't need to check uniqueness or found out that multiple solutions exist
                // then return
                return result;
            }
            // remove it from the final movements list
            movements.remove(nextMove);
            // register as a tested movement
            testedMovements.add(nextMove);
            testedGrid[nextMove.x][nextMove.y]++;
            final Cell cell = board.getCell(nextMove.x, nextMove.y);
            if (cell.getAvailableNumberCount() == testedGrid[nextMove.x][nextMove.y])
            {
                // there are no other numbers to try for this cell
                return Solvability.NOT_SOLVABLE;
            }
        }
    }

    private Solvability isSolved() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board.getCell(i, j).getValue() == 0) {
                    return Solvability.HAS_EMPTY_CELLS;
                }
            }
        }
        if (checkUniqueness) {
            if (firstSolutionCells == null) {
                firstSolutionCells = new int[9][9];
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        firstSolutionCells[i][j] = board.getCell(i, j).getValue();
                    }
                }
                return Solvability.DONE;
            }
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (firstSolutionCells[i][j] != board.getCell(i, j).getValue())
                    {
                        return Solvability.MULTIPLE_SOLUTIONS;
                    }
                }
            }
        }
        return Solvability.DONE;
    }

    private Movement nextPossibleMovement(final Set<Movement> testedMovements, final int[][] testedGrid) {
        final Movement testMovement = new Movement(0, 0, 0);
        final int seed = RND.nextInt(81);
        // check in inverse order of possibilities
        final int minPossibility = minPossibility(testedGrid);
        for (int i = 0; i < 81; i++) {
            final int x = (seed + i) % 81 / 9;
            final int y = (seed + i) % 81 % 9;
            final Cell cell = board.getCell(x, y);
            final int availableNumberCount = cell.getAvailableNumberCount();
            if (availableNumberCount - testedGrid[x][y] == minPossibility) {
                testMovement.x = x;
                testMovement.y = y;
                final int seedInCell = RND.nextInt(availableNumberCount);
                for (int j = 0; j < availableNumberCount; j++) {
                    final int index = (seedInCell + j) % availableNumberCount;
                    testMovement.number = cell.getAvailableNumber(index);
                    if (!testedMovements.contains(testMovement)) {
                        return testMovement;
                    }
                }
            }
        }
        return null;
    }

    private boolean hasIllegalCell() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                final Cell cell = board.getCell(i, j);
                if (cell.getValue() == 0 && cell.getAvailableNumberCount() == 0)
                {
                    return true;
                }
            }
        }
        return false;
    }

    public int minPossibility(int[][] testedGrid) {
        int best = 9;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                final int available;
                if (testedGrid != null) {
                    available = board.getCell(i, j).getAvailableNumberCount() - testedGrid[i][j];
                } else {
                    available = board.getCell(i, j).getAvailableNumberCount();
                }
                if (available != 0 && available < best) {
                    best = available;
                }
            }
        }
        return best;
    }
}