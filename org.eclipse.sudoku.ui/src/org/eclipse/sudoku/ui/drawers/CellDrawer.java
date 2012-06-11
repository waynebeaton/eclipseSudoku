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

import java.util.Iterator;

import org.eclipse.sudoku.core.models.Cell;
import org.eclipse.sudoku.ui.views.DrawingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

public class CellDrawer {

	private int left;
	private int top;
	private final int cellSize;
	public final Cell cell;

	public CellDrawer(Cell cell, int x, int y, int cellSize) {
		this.cell = cell;
		left = x + cell.column * cellSize;
		top = y + cell.row * cellSize;
		this.cellSize = cellSize;
	}

	public void drawCell(GC gc, DrawingContext context) {
		if (cell.isMarked()) {
			gc.setBackground(context.getMarkedCellColor());
			gc.fillRectangle(left, top, cellSize, cellSize);
		}
		
		gc.setForeground(context.getFrameColor());
		gc.drawRectangle(left, top, cellSize, cellSize);
		gc.setLineWidth(1);

		if (!cell.isEmpty()) {
			if (cell.isGiven())
				gc.setFont(context.getStaticCellFont(cellSize/2));
			else
				gc.setFont(context.getCellFont(cellSize/2));

			String text = String.valueOf(cell.getValue());	
			Point textExtent = gc.textExtent(text);
			
			try {
				gc.drawText(text, left + (cellSize - textExtent.x) / 2, top + (cellSize - textExtent.y) / 2, true);
			} finally {
				gc.setFont(null);
			}
		}
		if (!cell.isValid()) {
			int oldAlpha = gc.getAlpha();
			gc.setAlpha(25);
			gc.setBackground(context.getInvalidCellColor());
			gc.fillRectangle(left, top, cellSize, cellSize);
			gc.setAlpha(oldAlpha);
		}
	}

	public boolean contains(int x, int y) {
		if (x < left) return false;
		if (x > left + cellSize) return false;
		if (y < top) return false;
		if (y > top + cellSize) return false;
		return true;
	}

	public void drawHoverCell(GC gc, DrawingContext context) {
		if (cell.isGiven()) return;
		//gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_WHITE));
		gc.setForeground(context.getCellHoverColor());
		gc.setLineWidth(cellSize / 10);
		gc.setLineJoin(SWT.JOIN_ROUND);
		gc.drawRectangle(left, top, cellSize, cellSize);
		
		//if (!cell.isEmpty()) return;
		
		int width = cellSize / 3;
		gc.setFont(context.getCellHintFont(width * 3 / 4));
		Iterator<Integer> values = cell.getPossibleValues().iterator();
		for (int column=0;column < 3; column++) {
			for (int row=0;row < 3; row++) {
				if (!values.hasNext()) return;
				int x = left + row * width;
				int y = top + column * width;
				String text = String.valueOf(values.next());
				Point textExtent = gc.stringExtent(text);
				gc.drawText(text, x + (width - textExtent.x) / 2, y + (width - textExtent.y) / 2, true);
			}
		}
		
	}
}
