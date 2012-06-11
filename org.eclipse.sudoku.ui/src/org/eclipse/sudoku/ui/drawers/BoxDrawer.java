/*******************************************************************************
 * Copyright (c) 2006 The Eclipse Foundation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Wayne Beaton (The Eclipse Foundation) - initial API and implementation
 *******************************************************************************/
package org.eclipse.sudoku.ui.drawers;

import org.eclipse.sudoku.core.models.Box;
import org.eclipse.sudoku.ui.views.DrawingContext;
import org.eclipse.swt.graphics.GC;

public class BoxDrawer {

	private int left;
	private int top;
	private final int colourIndex;
	private int boxSize;

	public BoxDrawer(Box box, int x, int y, int cellSize, int colourIndex) {
		this.colourIndex = colourIndex;
		this.left = x + box.startColumn * cellSize;
		this.top = y + box.startRow * cellSize;
		this.boxSize = 3 * cellSize;
	}

	public void drawBox(GC gc, DrawingContext context) {
		gc.setBackground(context.getBoxColor(colourIndex));
		gc.fillRectangle(left, top, boxSize, boxSize);
	}

}
