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

/**
 * Eliminates available numbers from the cells in the same box.
 * 
 * @author George Koch
 */
public class BoxSimplifier implements Simplifier {
    public BoxSimplifier() {
    }

    public void doIt(final Movement move, final Board board, final Set<Movement> undoMoves) {
        // clear grid
        final int baseX = move.x / 3 * 3;
        final int baseY = move.y / 3 * 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board.clear(baseX + i, baseY + j, move.number, undoMoves);
            }
        }
    }
}