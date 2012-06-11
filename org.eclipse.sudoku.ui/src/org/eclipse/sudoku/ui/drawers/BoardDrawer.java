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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sudoku.core.models.Box;
import org.eclipse.sudoku.core.models.Cell;
import org.eclipse.sudoku.core.models.SudokuBoard;
import org.eclipse.sudoku.ui.views.DrawingContext;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

public class BoardDrawer {
	private SudokuBoard board;
	private List<BoxDrawer> boxDrawers;
	private List<CellDrawer> cellDrawers;
	private CellDrawer highlightedCell;
	
	private int left;
	private int top;
	private int width;
	private int height;
			
	public BoardDrawer(SudokuBoard board) {
		this.board = board;
	}

	public void highlightCellAt(int x, int y) {
		highlightedCell = findCellDrawer(x, y);
	}

	public void dontHighlight() {
		highlightedCell = null;		
	}
	
	public void setBounds(Rectangle bounds) {
		this.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
	}
	
	public void setBounds(int left, int top, int width, int height) {
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
		rebuildCaches();
	}
	
	private CellDrawer findCellDrawer(int x, int y) {
		for (CellDrawer drawer : cellDrawers) {
			if (drawer.contains(x, y)) {
				return drawer;
			}
		}
		return null;
	}

	public void drawBoard(GC gc, DrawingContext context) {
		for (BoxDrawer drawer : boxDrawers) {
			drawer.drawBox(gc, context);
		}
		for (CellDrawer drawer : cellDrawers) {
			drawer.drawCell(gc, context);
		}
		if (highlightedCell != null) highlightedCell.drawHoverCell(gc, context);
	}
	
	private void rebuildCaches() {
		cellDrawers = new ArrayList<CellDrawer>();
		boxDrawers = new ArrayList<BoxDrawer>();
				
		int size = Math.min(width, height);
		int cellSize = size / 9;
		size = cellSize * 9;
		
		if (size <= 0) return;
		
		int x = left + (width - size) / 2;
		int y = top + (height - size) / 2;
		
		int colourIndex = 0;
		for (Box box : board.getBoxes()) {
			boxDrawers.add(new BoxDrawer(box, x, y, cellSize, colourIndex++ % 2));
		}
		
		for (Cell cell : board.getCells()) {
			cellDrawers.add(new CellDrawer(cell, x, y, cellSize));
		}
	}

	public void setBoard(SudokuBoard board) {
		this.board = board;
		rebuildCaches();
	}

	public Cell getHighlightedCell() {
		if (highlightedCell == null) return null;
		return highlightedCell.cell;
	}
}