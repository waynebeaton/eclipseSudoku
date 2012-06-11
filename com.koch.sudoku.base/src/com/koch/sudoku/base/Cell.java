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
 * Stores and manages information for an individual cell.
 * 
 * @author George Koch
 */
public class Cell {
    private static final int[] BITS = {0, 1, 2, 4, 8, 16, 32, 64, 128, 256};
    private final int[] availableNumbers;
    private int bitMask;
    private int value;

    public Cell() {
        availableNumbers = new int[10];
        for (int i = 1; i < 10; i++) {
            availableNumbers[i] = i;
        }
        availableNumbers[0] = 9;
        value = 0;
        bitMask = 511;
    }

    public int getAvailableNumber(final int index) {
        return availableNumbers[index + 1];
    }

    public int getAvailableNumberCount() {
        return availableNumbers[0];
    }

    public int getValue() {
        return value;
    }

    public void resetValue() {
        value = 0;
    }

    public void setValue(final int number) {
        value = number;
    }

    public boolean set(final int number) {
        final boolean hasNumber = hasAvailableNumber(number);
        if (!hasNumber) {
            availableNumbers[0]++;
            availableNumbers[availableNumbers[0]] = number;
            bitMask |= BITS[number];
        }
        return !hasNumber;
    }

    public boolean clear(final int number) {
        if (hasAvailableNumber(number)) {
            bitMask ^= BITS[number];
            final int count = availableNumbers[0];
            for (int i = 1; i <= count; i++) {
                if (availableNumbers[i] == number) {
                    availableNumbers[i] = availableNumbers[count];
                    availableNumbers[0]--;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasAvailableNumber(final int number) {
        return (bitMask & BITS[number]) > 0;
    }

    public int getBitMask() {
        return bitMask;
    }
}