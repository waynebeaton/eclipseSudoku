package org.eclipse.sudoku.factory.samples;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Collector {

	/**
	 * @param args
	 * http://www.csse.uwa.edu.au/~gordon/sudokumin.php
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		FileOutputStream out = new FileOutputStream("c:\\plugin.xml");
		InputStream in = Collector.class.getResourceAsStream("puzzles.txt");
		try {
			PrintWriter writer = new PrintWriter(out);
			writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			writer.println("<?eclipse version=\"3.2\"?>");
			writer.println("<plugin>");
			writer.println("\t<extension id=\"www.csse.uwa.edu.au\" name=\"Gordon Royle Puzzles\" point=\"org.eclipse.sudoku.factory.samples.sample\">");
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = reader.readLine();
			while (line != null) {
				writer.println("\t\t<sample data=\"" + line + "\"/>");
				line = reader.readLine();
			}
			writer.println("\t</extension>");
			writer.println("</plugin>");
			writer.flush();
		} finally {
			in.close();
			out.close();
		}
	}

}
