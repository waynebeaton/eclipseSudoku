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
package org.eclipse.sudoku.factory.generator.tests;

import org.eclipse.sudoku.factory.generator.SudokuBoardGenerator;

public class GeneratorFactoryPerformance {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SudokuBoardGenerator generator = new SudokuBoardGenerator();
		long max = 0;
		for (int index=0;index<10000;index++) {
			long startTime = System.currentTimeMillis();
			generator.generate();
			long endTime = System.currentTimeMillis();
			long elapsed = endTime - startTime;
			if (elapsed > max) max = elapsed;
			
			System.out.println ("Run #" + index + " took " + elapsed + " milliseconds.");
		}
		System.out.println ("The longest run took " + max + " milliseconds.");
	}

}
