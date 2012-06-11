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

/**
 * Helper class to easily walk units (row, column, box).
 * 
 * @author George Koch
 */
public class UnitWalker {
    
	public enum Unit {ROW, COLUMN, BOX}

    private static final UnitWalker INSTANCE = new UnitWalker();

    private Board board;
    private int x;
    private int y;
    private Unit unit;
    private int baseIndex;

    private UnitWalker() {
        // hide it
    }

    public static UnitWalker getInstance(final Board board, final int x, final int y, final Unit unit) {
        assert unit == Unit.ROW || unit == Unit.COLUMN || unit == Unit.BOX;
        INSTANCE.board = board;
        if (unit != Unit.BOX) {
            INSTANCE.x = x;
            INSTANCE.y = y;
            if (unit == Unit.ROW) {
                INSTANCE.baseIndex = x;
            } else {
                INSTANCE.baseIndex = y;
            }

        } else {
            INSTANCE.x = x - x % 3;
            INSTANCE.y = y - y % 3;
            INSTANCE.baseIndex = (y - INSTANCE.y) * 3 + x % 3;
        }
        assert x < 9;
        assert y < 9;
        INSTANCE.unit = unit;
        return INSTANCE;
    }

    public Cell getCell(final int index) {
        final Cell cell;
        if (unit == Unit.ROW) {
            cell = board.getCell(index, y);
        } else if (unit == Unit.COLUMN) {
            cell = board.getCell(x, index);
        } else {
            cell = board.getCell(x + index % 3, y + index / 3);
        }
        return cell;
    }

    public int getBaseIndex() {
        return baseIndex;
    }

    public int getX(final int index) {
        final int result;
        if (unit == Unit.ROW) {
            result = index;
        } else if (unit == Unit.COLUMN) {
            result = x;
        } else {
            result = x + index % 3;
        }
        return result;
    }

    public int getY(final int index) {
        final int result;
        if (unit == Unit.ROW) {
            result = y;
        } else if (unit == Unit.COLUMN) {
            result = index;
        } else {
            result = y + index / 3;
        }
        return result;
    }
}