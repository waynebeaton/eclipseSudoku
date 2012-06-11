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
 * Class to store movement information (x, y coordinate and a number).
 * 
 * @author George Koch
 */
public class Movement {
    public int x;
    public int y;
    public int number;

    public Movement(final int x, final int y, final int number) {
        this.x = x;
        this.y = y;
        this.number = number;
    }

    public boolean equals(final Object obj) {
        if (obj == null || !obj.getClass().equals(getClass())) {
            return false;
        }
        final Movement other = (Movement) obj;
        return other.x == x && other.y == y && other.number == number;
    }

    public int hashCode() {
        return (x * 10 + y) * 10 + number;
    }

    public String toString() {
        return "Movement [x = " + x + ", y = " + y + ", number = " + number + "]";
    }

    public static class NormalMovement extends Movement {
        public NormalMovement(int x, int y, int number) {
            super(x, y, number);
        }

        public NormalMovement(final Movement move) {
            this(move.x, move.y, move.number);
        }
    }

    public static class ClearMovement extends Movement {
        public ClearMovement(int x, int y, int number) {
            super(x, y, number);
        }
    }
}