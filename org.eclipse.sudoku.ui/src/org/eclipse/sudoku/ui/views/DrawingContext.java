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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class DrawingContext {

	private ColorRegistry colorRegistry;
		
	private static final String GIVEN_CELL_FONT = "given_cell_font";
	private static final String HINT_CELL_FONT = "hint_cell_font";
	private static final String CELL_FONT = "cell_font";

	private static final String BOX_COLOR_2 = "box_color_1";
	private static final String BOX_COLOR_1 = "box_color_2";
	private static final String[] boxColors = {BOX_COLOR_1, BOX_COLOR_2};
	private static final String HOVER_CELL_COLOR = "hover_cell_color";
	private static final String CELL_FRAME_COLOR = "cell_frame_color";
	private static final String INVALID_CELL_COLOR = "invalid_cell_color";
	private static final String MARKED_CELL_COLOR = "marked_cell_color";
	
	private Map<String, FontHandle> fonts = new HashMap<String, FontHandle>();
	private final Display display;
	
	public DrawingContext(Display display) {
		this.display = display;
		this.colorRegistry = new ColorRegistry(display);
		this.colorRegistry.put(BOX_COLOR_1, new RGB(235, 235, 235));
		this.colorRegistry.put(BOX_COLOR_2, new RGB(255, 255, 255));
		this.colorRegistry.put(CELL_FRAME_COLOR, new RGB(0, 0, 0));
		this.colorRegistry.put(HOVER_CELL_COLOR, new RGB(255, 0, 0));
		this.colorRegistry.put(INVALID_CELL_COLOR, new RGB(255,0,0));
		this.colorRegistry.put(MARKED_CELL_COLOR, new RGB(0,255,255));
	}

	public Color getBoxColor(int colourIndex) {
		return colorRegistry.get(boxColors[colourIndex % boxColors.length]);
	}

	public Font getCellFont(int size) {
		return getFont(CELL_FONT, "Arial", size, SWT.NORMAL);
	}

	public Color getCellHoverColor() {
		return colorRegistry.get(HOVER_CELL_COLOR);
	}

	public Color getFrameColor() {
		return colorRegistry.get(CELL_FRAME_COLOR);
	}

	public Font getStaticCellFont(int size) {
		return getFont(GIVEN_CELL_FONT, "Arial", size, SWT.BOLD);
	}

	public Font getCellHintFont(int size) {
		return getFont(HINT_CELL_FONT, "Arial", size, SWT.NORMAL);
	}

	public Color getInvalidCellColor() {
		return colorRegistry.get(INVALID_CELL_COLOR);
	}

	public Color getMarkedCellColor() {
		return colorRegistry.get(MARKED_CELL_COLOR);
	}
	
	public void dispose() {
		disposeFonts();
	}
	
	private void disposeFonts() {
		for (FontHandle handle : fonts.values()) {
			handle.dispose();
		}
	}

	private Font getFont(String name, String face, int size, int style) {
		FontHandle handle = fonts.get(name);
		if (handle == null) {
			handle = new FontHandle(allocateFont(face, size, style), size);
			fonts.put(name, handle);
		}
		if (handle.size != size) {
			handle.dispose();
			handle.setSize(size);
			handle.setFont(allocateFont(face, size, style));
		}
		return handle.font;
	}

	private Font allocateFont(String face, int size, int style) {
		return new Font(display, new FontData[] {new FontData(face, size, style)});
	}
}

class FontHandle {
	Font font;
	int size;

	public FontHandle(Font font, int size) {
		this.font = font;
		this.size = size;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void dispose() {
		font.dispose();
	}
}
