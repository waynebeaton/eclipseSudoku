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
package org.eclipse.sudoku.ui.tests;

import org.eclipse.sudoku.core.models.SudokuBoard;
import org.eclipse.sudoku.ui.drawers.BoardDrawer;
import org.eclipse.sudoku.ui.views.DrawingContext;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class BoardDrawerPerformance {

	public static void main(String[] args) {
		SudokuBoard board = new SudokuBoard(new int[][] {
				{0,6,0, 1,0,4, 0,5,0},
				{0,0,8, 3,0,5, 6,0,0},
				{2,0,0, 0,0,0, 0,0,1},
				
				{8,0,0, 4,0,7, 0,0,6},
				{0,0,6, 0,0,0, 3,0,0},
				{7,0,0, 9,0,1, 0,0,4},
				
				{5,0,0, 0,0,0, 0,0,2},
				{0,0,7, 2,0,6, 9,0,0},
				{0,4,0, 5,0,8, 0,7,0}
		});
		
		BoardDrawer drawer = new BoardDrawer(board);
		
		Image image = new Image(Display.getDefault(), 100, 100);
		GC gc = new GC(image);
		DrawingContext context = new DrawingContext(Display.getDefault());
		try {
			drawer.setBounds(image.getBounds());
			for(int index=0;index<100;index++)
				drawer.drawBoard(gc, context);
		} finally {
			gc.dispose();
			image.dispose();
		}		
	}

}
