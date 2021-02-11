package com.kreative.pushchar.main;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import com.kreative.acc.pushchar.CharInFont;
import com.kreative.pushchar.unilib.BlockList;
import com.kreative.pushchar.unilib.EncodingTable;
import com.kreative.pushchar.unilib.GlyphList;

public class Section {
	public static final int COLUMN_COUNT = 16;
	
	private final String title;
	private final int visibleCount;
	private final int definedCount;
	private final int rowCount;
	private final int[][] indices;
	private final String[][] chars;
	
	private Section(
		String title, int visibleCount, int definedCount,
		int rowCount, int[][] indices, String[][] chars
	) {
		this.title = title;
		this.visibleCount = visibleCount;
		this.definedCount = definedCount;
		this.rowCount = rowCount;
		this.indices = indices;
		this.chars = chars;
	}
	
	public String getTitle() {
		return title;
	}
	
	public int getVisibleCount() {
		return visibleCount;
	}
	
	public int getDefinedCount() {
		return definedCount;
	}
	
	public String getTitleWithCount() {
		return title + " (" + visibleCount + "/" + definedCount + ")";
	}
	
	public int getRowCount() {
		return rowCount;
	}
	
	public int getIndex(int row, int col) {
		return indices[row][col];
	}
	
	public String getChar(int row, int col) {
		return chars[row][col];
	}
	
	public static Section forBlock(Font font, BlockList.Entry block) {
		boolean[] cinf = CharInFont.getInstance().isCharInFont(
			font.getName(),
			block.firstCodePoint,
			block.lastCodePoint
		);
		int rowCount = 0;
		List<int[]> indices = new ArrayList<int[]>();
		List<String[]> chars = new ArrayList<String[]>();
		int count = 0, cp = block.firstCodePoint, bi = 0;
		while (bi < cinf.length) {
			int[] ria = new int[COLUMN_COUNT];
			String[] rca = new String[COLUMN_COUNT];
			boolean ok = false;
			for (int i = 0; i < COLUMN_COUNT; i++) {
				if (bi < cinf.length) {
					ria[i] = cp;
					if (cinf[bi]) {
						rca[i] = String.valueOf(Character.toChars(cp));
						ok = true;
						count++;
					}
					cp++; bi++;
				}
			}
			if (ok) {
				rowCount++;
				indices.add(ria);
				chars.add(rca);
			}
		}
		return new Section(
			block.blockName, count, cinf.length, rowCount,
			indices.toArray(new int[rowCount][]),
			chars.toArray(new String[rowCount][])
		);
	}
	
	public static Section forEncodingTable(Font font, String name, EncodingTable table) {
		CharInFont cinf = CharInFont.getInstance();
		String fontName = font.getName();
		int rowCount = 0;
		List<int[]> indices = new ArrayList<int[]>();
		List<String[]> chars = new ArrayList<String[]>();
		int count = 0, total = 0, ei = 0;
		while (ei < 256) {
			int[] ria = new int[COLUMN_COUNT];
			String[] rca = new String[COLUMN_COUNT];
			for (int i = 0; i < COLUMN_COUNT; i++) {
				if (ei < 256) {
					ria[i] = ei;
					String s = table.getSequence(ei);
					if (s != null) {
						total++;
						if (cinf.areCharsInFont(fontName, s)) {
							rca[i] = s;
							count++;
						}
					}
					ei++;
				}
			}
			rowCount++;
			indices.add(ria);
			chars.add(rca);
		}
		return new Section(
			name, count, total, rowCount,
			indices.toArray(new int[rowCount][]),
			chars.toArray(new String[rowCount][])
		);
	}
	
	public static Section forGlyphList(Font font, GlyphList gl) {
		boolean[] cinf = CharInFont.getInstance().isCharInFont(
			font.getName(),
			gl.getCodePoints()
		);
		int rowCount = 0;
		List<int[]> indices = new ArrayList<int[]>();
		List<String[]> chars = new ArrayList<String[]>();
		int count = 0, gi = 0;
		while (gi < cinf.length) {
			int[] ria = new int[COLUMN_COUNT];
			String[] rca = new String[COLUMN_COUNT];
			for (int i = 0; i < COLUMN_COUNT; i++) {
				if (gi < cinf.length) {
					ria[i] = gi;
					if (cinf[gi]) {
						rca[i] = String.valueOf(Character.toChars(gl.getCodePoint(gi)));
						count++;
					}
					gi++;
				}
			}
			rowCount++;
			indices.add(ria);
			chars.add(rca);
		}
		return new Section(
			gl.getName(), count, cinf.length, rowCount,
			indices.toArray(new int[rowCount][]),
			chars.toArray(new String[rowCount][])
		);
	}
}
