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

public class NakedPairsSimplifier implements Simplifier {
    public void doIt(Movement move, Board board, Set<Movement> undoMoves) {
        checkUnit(board, UnitWalker.Unit.ROW, undoMoves);
        checkUnit(board, UnitWalker.Unit.COLUMN, undoMoves);
        checkUnit(board, UnitWalker.Unit.BOX, undoMoves);
    }

    private void checkUnit(final Board board, final UnitWalker.Unit unit, final Set<Movement> undoMoves) {
        // for every cell
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                final Cell cell = board.getCell(i, j);
                // if it has 2 available numbers
                if (cell.getAvailableNumberCount() == 2) {
                    final int bitMask = cell.getBitMask();
                    // check the rest of the cell in the same unit
                    final UnitWalker walker = UnitWalker.getInstance(board, i, j, unit);
                    final int index = walker.getBaseIndex();
                    for (int k = walker.getBaseIndex() + 1; k < 9; k++) {
                        final Cell other = walker.getCell(k);
                        if (other.getAvailableNumberCount() == 2) {
                            // if the other cell has the same numbers
                            if ((bitMask & other.getBitMask()) == bitMask) {
                                // clear these numbers from the other cells in the same unit
                                for (int l = 0; l < 2; l++) {
                                    final int number = cell.getAvailableNumber(l);
                                    for (int m = 0; m < 9; m++) {
                                        if (m != index && m != k) {
                                            final Cell toCheck = walker.getCell(m);
                                            if (toCheck.clear(number)) {
                                                final int x = walker.getX(m);
                                                final int y = walker.getY(m);
                                                undoMoves.add(new Movement.ClearMovement(x, y, number));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}