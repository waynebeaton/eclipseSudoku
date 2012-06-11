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
 * @author George Koch
 */
public interface Simplifier {
    void doIt(Movement move, Board board, Set<Movement> undoMoves);
}
