package com.kreative.pushchar.test;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import com.kreative.pushchar.unilib.GlyphList;

public class GlyphListTest {
	public static void main(String[] args) {
		boolean binaryMode = false;
		boolean parsingOptions = true;
		for (String arg : args) {
			if (parsingOptions && arg.startsWith("-")) {
				if (arg.equals("--")) parsingOptions = false;
				else if (arg.equals("-b")) binaryMode = true;
				else if (arg.equals("-t")) binaryMode = false;
				else System.err.println("Unknown option: " + arg);
			} else try {
				File file = new File(arg);
				FileInputStream in = new FileInputStream(file);
				GlyphList gl;
				if (binaryMode) gl = new GlyphList(new DataInputStream(in));
				else gl = new GlyphList(file.getName().replaceAll("\\.[Tt][Xx][Tt]$", ""), in);
				System.out.println(gl.getName());
				for (int i = 0, n = gl.length(); i < n; i++) {
					System.out.print(Character.toChars(gl.get(i)));
				}
				System.out.println();
			} catch (IOException e) {
				System.err.println("Error: " + e);
			}
		}
	}
}
