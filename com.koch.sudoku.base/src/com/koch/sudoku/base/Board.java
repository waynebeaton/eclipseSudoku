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
import java.util.List;
import java.util.Set;

/**
 * Sudoku board. Contains the cells and performs moves/undos on them.
 * 
 * <p>Cells can be addressen using x and y coordinates (directly or indirectly through Movements). A coordinate must be
 * a nonnegative number less than 9.</p>
 * 
 * <p>Values/available numbers of the cells are positive numbers and are less than 10.</p> 
 * 
 * @author George Koch
 */
public class Board {
    /**
     * Default list of simplifiers.
     */
	private static final List<Simplifier> SIMPLIFIERS;

    static {
        SIMPLIFIERS = new ArrayList<Simplifier>();
        SIMPLIFIERS.add(new CellSimplifier());
        SIMPLIFIERS.add(new RowAndColumnSimplifier());
        SIMPLIFIERS.add(new BoxSimplifier());
        SIMPLIFIERS.add(new Naked1Simplifier());
        SIMPLIFIERS.add(new NakedPairsSimplifier());
        SIMPLIFIERS.add(new NakedNSimplifier(3, 8));
    }

    private List<Simplifier> simplifiers;

    private final Cell[][] cells;

    public Board() {
        cells = new Cell[9][9];
        reset();
    }

    /**
     * Recreates the cells and restores the simplifiers.
     */
    private void reset() {
        simplifiers = SIMPLIFIERS;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                cells[i][j] = new Cell();
            }
        }
    }

    /**
     * Undoes a set of movements.
     * 
     * @param undoMoves the movements to undo
     */
    public void undo(final Set<Movement> undoMoves) {
        for (Movement undoMove : undoMoves) {
            if (undoMove instanceof Movement.NormalMovement) {
                cells[undoMove.x][undoMove.y].resetValue();
            } else {
                cells[undoMove.x][undoMove.y].set(undoMove.number);
            }
        }
    }

    /**
     * Sets the value of the specified cell. Also executes the simplifiers to eliminate the freed available numbers. 
     * 
     * @param move the move object that contains the cell coordinates and the value to set. Must not be null.
     * 
     * @return the set of undo moves that will revert the move.
     */
    public Set<Movement> move(final Movement move) {
        final Set<Movement> undoMoves = new HashSet<Movement>();
        // register movement
        cells[move.x][move.y].setValue(move.number);
        undoMoves.add(new Movement.NormalMovement(move));

        if (simplifiers != null) {
            int undoEnds = simplifiers.size();
            int undoSize = undoMoves.size();
            for (int i = 0; i < undoEnds; i++) {
                final Simplifier simplifier = simplifiers.get(i % simplifiers.size());
                simplifier.doIt(move, this, undoMoves);

                final int newUndoSize = undoMoves.size();
                if (newUndoSize > undoSize) {
                    undoSize = newUndoSize;
                    undoEnds = i + simplifiers.size();
                }
            }
        }
        return undoMoves;
    }

    /**
     * Deletes an available number from the specified cell.
     * 
     * @param x the x coordinate of the cell
     * @param y the y coordinate of the cell
     * @param number the number to delete
     * @param undoMoves the set of unfo movements to extend.
     */
    public void clear(final int x, final int y, final int number, final Set<Movement> undoMoves) {
        if (cells[x][y].clear(number)) {
            undoMoves.add(new Movement.ClearMovement(x, y, number));
        }
    }

    public Cell getCell(final int x, final int y) {
        assert x < 9;
        assert y < 9;
        return cells[x][y];
    }

    public void setSimplifiers(final List<Simplifier> simplifiers) {
        this.simplifiers = simplifiers;
    }

    public String toString() {
        final StringBuffer buffer = new StringBuffer(180);
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                buffer.append(cells[j][i].getValue()).append(" ");
            }
            buffer.append("\r\n");
        }
        return buffer.toString();
    }
}