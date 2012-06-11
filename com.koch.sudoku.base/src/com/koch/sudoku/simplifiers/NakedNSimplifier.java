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
package com.koch.sudoku.simplifiers;

import com.koch.sudoku.base.Board;
import com.koch.sudoku.base.Cell;
import com.koch.sudoku.base.Movement;
import com.koch.sudoku.base.UnitWalker;

import java.util.Set;

public class NakedNSimplifier implements Simplifier {
    private final int from;
    private final int to;

    public NakedNSimplifier(final int from, final int to) {
        this.from = from;
        this.to = to;
    }

    public void doIt(Movement move, Board board, Set<Movement> undoMoves) {
        checkUnit(board, UnitWalker.Unit.ROW, undoMoves);
        checkUnit(board, UnitWalker.Unit.COLUMN, undoMoves);
        checkUnit(board, UnitWalker.Unit.BOX, undoMoves);
    }

    private void checkUnit(final Board board, final UnitWalker.Unit unit, final Set<Movement> undoMoves) {
        for (int n = from; n <= to; n++) {
            final int[] indices = new int[n];
            final Cell[] cells = new Cell[n];
            // for every cell
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    final Cell cell = board.getCell(i, j);
                    if (isPossibleCell(cell, n)) {
                        assert board.getCell(i, j).getValue() == 0;
                        // check the rest of the cell in the same unit
                        final UnitWalker walker = UnitWalker.getInstance(board, i, j, unit);
                        final int index = walker.getBaseIndex();
                        if (index + n > 9) {
                            // not enough element in the unit
                            continue;
                        }
                        // generate initial vector
                        boolean overflow = !initialiseVectors(index, indices, walker, cells);
                        while (!overflow) {
                            final int bitMask = getBitMaskUnion(cells);
                            if (bits(bitMask) == cells.length) {
                                removeValues(bitMask, walker, indices, board, undoMoves);
                            }
                            overflow = !incrementVector(walker, cells, indices);
                        }
                    }
                }
            }
        }
    }

    private boolean initialiseVectors(final int index, final int[] indices, final UnitWalker walker, final Cell[] cells) {
        final int len = indices.length;
        int nextToTry = index;
        for (int i = 0; i < len; i++) {
            boolean found = false;
            while (nextToTry <= 8) {
                final Cell cell = walker.getCell(nextToTry);
                if (isPossibleCell(cell, len)) {
                    indices[i] = nextToTry;
                    cells[i] = cell;
                    // the search for the next index should start from the next number
                    nextToTry++;
                    found = true;
                    break;
                } else {
                    // try the next number
                    nextToTry++;
                }
            }
            if (!found) {
                // run out of numbers at one of the positions
                return false;
            }
        }
        return true;
    }

    private int getBitMaskUnion(final Cell[] cells) {
        assert cells.length > 1;
        // TODO: optimize it (based on the fact that the naked (N-1)'s are solved)
        int bitMask = 0;
        for (Cell cell : cells) {
            bitMask |= cell.getBitMask();
        }
        return bitMask;
    }

    private int bits(int bitMask) {
        int bits = 0;
        while (bitMask != 0) {
            bits += bitMask & 1;
            bitMask >>= 1;
        }
        return bits;
    }

    private void removeValues(final int bitMask, final UnitWalker walker, final int[] indices,
                              final Board board, final Set<Movement> undoMoves) {
        int pos = 0;
        for (int i = 0; i < 9; i++) {
            if (pos < indices.length && i == indices[pos]) {
                // this number is among the indices
                pos++;
            } else {
                // this number is not among the indices
                final int x = walker.getX(i);
                final int y = walker.getY(i);
                removeNumbers(bitMask, board, x, y, undoMoves);
            }
        }
    }

    private void removeNumbers(int bitMask, final Board board, final int x, final int y,
                               final Set<Movement> undoMoves) {
        for (int i = 1; bitMask != 0; i++) {
            if ((bitMask & 1) == 1) {
                board.clear(x, y, i, undoMoves);
            }
            bitMask >>= 1;
        }
    }

    private boolean incrementVector(final UnitWalker walker, final Cell[] cells, final int[] indices) {
        final int length = indices.length;
        int positionToIncrement = length - 1;
        boolean found = false;
        boolean overflow = false;
        while (!found && !overflow) {
            // suppose there is no more valid index vector
            overflow = true;
            // increment the vector (note: there is no need to increment the first index, it is done by the
            // double loop in checkUnit
            for (int i = positionToIncrement; i >= 1; i--) {
                if (indices[i] < 9 - length + i) {
                    // we found a possible index vector
                    overflow = false;
                    // set it and check if it can be ok
                    indices[i]++;
                    found = true;
                    for (int j = i; j < length; j++) {
                        if (j > i) {
                            indices[j] = indices[j - 1] + 1;
                            assert indices[j] < 9;
                        }
                        cells[j] = walker.getCell(indices[j]);
                        if (!isPossibleCell(cells[j], length)) {
                            if (found) {
                                positionToIncrement = i;
                            }
                            found = false;
                        }
                    }
                    break;
                }
            }
        }
        return found;
    }

    private boolean isPossibleCell(final Cell cell, final int maxCount) {
        final int availableNumbers = cell.getAvailableNumberCount();
        return availableNumbers > 0 && availableNumbers <= maxCount;
    }
}