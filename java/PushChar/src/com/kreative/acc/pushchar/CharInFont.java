package com.kreative.acc.pushchar;

import java.util.BitSet;

public abstract class CharInFont {
	private static CharInFont instance = null;
	public static CharInFont getInstance() {
		if (instance == null) {
			if (isMacOS()) {
				try {
					Class<?> mcif = Class.forName("com.kreative.acc.pushchar.MacCharInFont");
					instance = (CharInFont)mcif.newInstance();
				} catch (Throwable e) {
					System.err.println("Could not load native CharInFont instance. Falling back on Java.");
					System.err.println("(" + e.getMessage() + ")");
					instance = new JavaCharInFont();
				}
			} else {
				instance = new JavaCharInFont();
			}
		}
		return instance;
	}
	
	public abstract boolean isCharInFont(String fontName, int charToCheck);
	public abstract boolean[] isCharInFont(String fontName, int startChar, int endChar);
	public abstract boolean[] isCharInFont(String fontName, int[] charsToCheck);
	
	public boolean areCharsInFont(String fontName, String s) {
		int i = 0, n = s.length();
		while (i < n) {
			int ch = s.codePointAt(i);
			if (!isCharInFont(fontName, ch)) return false;
			i += Character.charCount(ch);
		}
		return true;
	}
	
	public BitSet allCharsInFont(String fontName) {
		BitSet res = new BitSet(0x110000);
		for (int plane = 0; plane < 0x110000; plane += 0x10000) {
			for (int page = 0; page < 0x10000; page += 0x100) {
				boolean[] allCharsInPage = isCharInFont(fontName, plane+page, plane+page+0xFF);
				for (int chr = 0; chr < 0x100; chr++) {
					if (allCharsInPage[chr]) res.set(plane+page+chr);
				}
			}
		}
		return res;
	}
	
	private static String osString = null;
	private static boolean isMacOS() {
		if (osString == null) {
			try {
				osString = System.getProperty("os.name").toUpperCase();
			} catch (Exception e) {
				osString = "";
			}
		}
		return osString.contains("MAC OS");
	}
}
