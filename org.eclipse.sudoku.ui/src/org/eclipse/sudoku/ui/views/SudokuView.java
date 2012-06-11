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
package org.eclipse.sudoku.ui.views;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.sudoku.core.SudokuCoreActivator;
import org.eclipse.sudoku.core.listeners.SudokuBoardChangeListener;
import org.eclipse.sudoku.core.listeners.SudokuBoardStateAdapter;
import org.eclipse.sudoku.core.listeners.SudokuBoardStateListener;
import org.eclipse.sudoku.core.models.Cell;
import org.eclipse.sudoku.core.models.SudokuBoard;
import org.eclipse.sudoku.core.models.SudokuGame;
import org.eclipse.sudoku.ui.drawers.BoardDrawer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

public class SudokuView extends ViewPart {
	private Canvas canvas;
	private SudokuGame game;
	private SudokuBoard board;
	private BoardDrawer drawer;
	
	DrawingContext context;
	
	SudokuBoardChangeListener boardChangeListener = new SudokuBoardChangeListener() {
		public void boardChanged(SudokuBoard newBoard, SudokuBoard oldBoard) {
			unhookStateListener(oldBoard);
			hookStateListener(newBoard);
			
			board = newBoard;
			drawer.setBoard(board);
			canvas.redraw();
		}
	};
	
	final SudokuBoardStateListener stateListener = new SudokuBoardStateAdapter() {
		private static final long serialVersionUID = 6014398143480263098L;
		
		public void boardChanged(SudokuBoard board) {
			canvas.getDisplay().syncExec(new Runnable() {
				public void run() {
					canvas.redraw();
				}
			});
		}
	};
	
	public void createPartControl(Composite parent) {
		game = SudokuCoreActivator.getGame();
		board = game.getBoard();
		drawer = new BoardDrawer(board);
		
		canvas = new Canvas(parent, SWT.NO_BACKGROUND);
		context = new DrawingContext(parent.getDisplay());
		
		game.addSudokuBoardChangeListener(boardChangeListener);
		
		hookEventListeners();
		hookStateListener(board);
		contributeToActionBars();
	}
	
	private void hookStateListener(SudokuBoard board) {
		board.addSudokuBoardStateListener(stateListener);
	}
	
	private void unhookStateListener(SudokuBoard board) {
		board.removeSudokuBoardStateListener(stateListener);
	}

	@Override
	public void dispose() {
		super.dispose();
		game.removeSudokuBoardChangeListener(boardChangeListener);
		context.dispose();
	}
			
	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
	}
	
	private void hookEventListeners() {
		hookResizeListener();
		hookPaintListener();
		hookMouseListener();
		hookKeyboardListener();
	}

	/**
	 * This method adds a keyboard listener to the canvas. The listener responds
	 * to key presses and does the following:
	 * <ul>
	 * <li>If the Backspace (SWT.BS) or Delete (SWT.DEL) is pressed, the
	 * highlighted cell (if any) is cleared.</li>
	 * <li>If a digit key is pressed, the value of that key is put into the
	 * cell. If '0' is pressed, the cell is cleared.</li>
	 * </ul>
	 */
	private void hookKeyboardListener() {
		canvas.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				Cell hoverCell = drawer.getHighlightedCell();
				if (hoverCell == null) return;
				if (e.character == SWT.BS || e.character == SWT.DEL) {
					hoverCell.clear();
				} else if (Character.isDigit(e.character)) {
					hoverCell.setValue(Character.getNumericValue(e.character));
				} else if (e.keyCode == SWT.SHIFT) {
					hoverCell.mark();
				}
			}

			public void keyReleased(KeyEvent e) {
			}
			
		});
	}

	/**
	 * This method adds listeners to the canvas to deal with 
	 * mouse movements and clicks.
	 */
	private void hookMouseListener() {
		/*
		 * Add a listener for mouse movement. Whenever the mouse moves,
		 * highlight the cell that falls under it.
		 */
		canvas.addListener(SWT.MouseMove, new Listener() {
			public void handleEvent(Event e) {
				drawer.highlightCellAt(e.x, e.y);
				
				/* 
				 * Since the board isn't changed, no change event will 
				 * be fired and updates will not happen automagically;
				 * explicity force the board to redraw.
				 */
				canvas.redraw();
			}			
		});
		
		/*
		 * Add a listener to detect when the mouse exits the canvas.
		 * When this happens, remove any highlighting on the board.
		 */
		canvas.addListener(SWT.MouseExit, new Listener() {
			public void handleEvent(Event event) {
				drawer.dontHighlight();
				/* 
				 * Since the board isn't changed, no change event will 
				 * be fired and updates will not happen automagically;
				 * explicity force the board to redraw.
				 */
				canvas.redraw();
			}			
		});
		
		/*
		 * When the mouse is clicked, set the value of the highlighted
		 * cell (if any) to the next value. The next value is the value
		 * immediately following the current value of the cell. If the
		 * cell is empty, the next value is 1; if the cell's current
		 * value is 9, the cell is made empty (by setting the value
		 * to 0).
		 */
		canvas.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				Cell hoverCell = drawer.getHighlightedCell();
				if (hoverCell == null) return;
				if (hoverCell.isGiven()) return;
				int value = hoverCell.getValue();
				hoverCell.setValue((value + 1) % 10);
			}
		});
	}
	
	private void hookPaintListener() {
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				Image buffer = createBufferImage();
				drawBoard(buffer);
				try {
					e.gc.drawImage(buffer, 0, 0);
				} finally {
					buffer.dispose();
				}
			}		
		});
	}

	private void hookResizeListener() {
		canvas.addControlListener(new ControlListener() {
			public void controlMoved(ControlEvent e) {		
			}

			public void controlResized(ControlEvent e) {
				drawer.setBounds(canvas.getBounds());
			}
			
		});
	}
	
	protected void drawBoard(Image buffer) {
		GC gc = new GC(buffer);
		try {
			drawer.drawBoard(gc, context);
		} finally {
			gc.dispose();
		}
	}

	
	public void setFocus() {
		canvas.setFocus();
	}
	
	private Image createBufferImage() {
		Rectangle bounds = canvas.getBounds();
		return new Image(canvas.getDisplay(), bounds.width, bounds.height); 
	}
}