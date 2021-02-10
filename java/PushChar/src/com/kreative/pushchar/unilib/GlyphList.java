package com.kreative.pushchar.unilib;

import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;

public class GlyphList {
	private final String name;
	private final int[] codePoints;
	
	public GlyphList(String name, int... codePoints) {
		this.name = name;
		this.codePoints = codePoints;
	}
	
	public GlyphList(String name, InputStream in) {
		this.name = name;
		this.codePoints = read(new Scanner(in));
	}
	
	public GlyphList(String name, Scanner scan) {
		this.name = name;
		this.codePoints = read(scan);
	}
	
	public GlyphList(DataInput in) throws IOException {
		this.name = in.readUTF();
		this.codePoints = new int[in.readUnsignedShort()];
		for (int i = 0; i < codePoints.length; i++) {
			int cp = in.readUnsignedShort();
			if (cp < 0x20) {
				cp <<= 16;
				cp |= in.readUnsignedShort();
			}
			codePoints[i] = cp;
		}
	}
	
	public String getName() {
		return name;
	}
	
	public int get(int index) {
		return codePoints[index];
	}
	
	public int length() {
		return codePoints.length;
	}
	
	private static int[] read(Scanner scan) {
		SortedSet<Integer> codePoints = new TreeSet<Integer>();
		while (scan.hasNextLine()) {
			String line = scan.nextLine().trim();
			if (line.length() > 0 && line.charAt(0) != '#') {
				String[] f = line.split("\\s+");
				String s = f[0].toUpperCase();
				if (s.startsWith("0X")) s = s.substring(2);
				try { codePoints.add(Integer.parseInt(s, 16)); }
				catch (NumberFormatException e) { continue; }
			}
		}
		int[] cpArray = new int[codePoints.size()]; int i = 0;
		for (int codePoint : codePoints) cpArray[i++] = codePoint;
		return cpArray;
	}
}
