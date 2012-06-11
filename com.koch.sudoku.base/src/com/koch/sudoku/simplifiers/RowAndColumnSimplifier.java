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
import com.koch.sudoku.base.Movement;

import java.util.Set;

public class RowAndColumnSimplifier implements Simplifier {
    public RowAndColumnSimplifier() {
    }

    public void doIt(final Movement move, final Board board, final Set<Movement> undoMoves) {
        // clear row and column
        for (int i = 0; i < 9; i++) {
            board.clear(move.x, i, move.number, undoMoves);
            board.clear(i, move.y, move.number, undoMoves);
        }
    }
}