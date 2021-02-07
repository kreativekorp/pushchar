package com.kreative.pushchar.legacy;

import java.io.UnsupportedEncodingException;
import javax.swing.JPopupMenu;

public class CopyMenu extends JPopupMenu {
	private static final long serialVersionUID = 1L;
	
	public CopyMenu(int codePoint) {
		// Straight
		String charString = new String(Character.toChars(codePoint));
		add(new CopyMenuItem(charString));
		
		addSeparator();
		
		// HTML
		add(new CopyMenuItem("&#" + Integer.toString(codePoint) + ";"));
		add(new CopyMenuItem("&#x" + Integer.toHexString(codePoint).toUpperCase() + ";"));
		
		// BBEdit
		if (codePoint < 256) {
			add(new CopyMenuItem("\\x" + intToHexString(codePoint, 2)));
		} else if (codePoint < 65536) {
			add(new CopyMenuItem("\\x" + intToHexString(codePoint, 4)));
		} else {
			add(new CopyMenuItem("\\x" + intToHexString(codePoint, 6)));
		}
		
		// Java
		StringBuffer javaEscapedString = new StringBuffer();
		for (char ch : Character.toChars(codePoint)) {
			javaEscapedString.append("\\u");
			javaEscapedString.append(intToHexString(ch, 4));
		}
		add(new CopyMenuItem(javaEscapedString.toString()));
		
		// XION
		if (codePoint >= 65536) {
			add(new CopyMenuItem("\\w" + intToHexString(codePoint, 6)));
		}
		
		// Python
		add(new CopyMenuItem("u'" + charString + "'"));
		if (codePoint < 65536) {
			add(new CopyMenuItem("u'\\u" + intToHexString(codePoint, 4) + "'"));
		} else {
			add(new CopyMenuItem("u'\\U" + intToHexString(codePoint, 8) + "'"));
		}
		
		addSeparator();
		
		// UTF-8
		try {
			byte[] utf8 = charString.getBytes("UTF-8");
			add(new CopyMenuItem(byteToHexString(utf8, "", "", " ")));
			add(new CopyMenuItem(byteToHexString(utf8, "\\x", "", "")));
			add(new CopyMenuItem(byteToHexString(utf8, "%", "", "")));
		} catch (UnsupportedEncodingException e) {
			// Ignored.
		}
		
		// UTF-16BE
		try {
			byte[] utf16be = charString.getBytes("UTF-16BE");
			add(new CopyMenuItem(byteToHexString(utf16be, "", "", " ")));
		} catch (UnsupportedEncodingException e) {
			// Ignored.
		}
		
		// UTF-16LE
		try {
			byte[] utf16le = charString.getBytes("UTF-16LE");
			add(new CopyMenuItem(byteToHexString(utf16le, "", "", " ")));
		} catch (UnsupportedEncodingException e) {
			// Ignored.
		}
		
		// UTF-32BE
		try {
			byte[] utf32be = charString.getBytes("UTF-32BE");
			add(new CopyMenuItem(byteToHexString(utf32be, "", "", " ")));
		} catch (UnsupportedEncodingException e) {
			// Ignored.
		}
		
		// UTF-32LE
		try {
			byte[] utf32le = charString.getBytes("UTF-32LE");
			add(new CopyMenuItem(byteToHexString(utf32le, "", "", " ")));
		} catch (UnsupportedEncodingException e) {
			// Ignored.
		}
	}
	
	private String intToHexString(int i, int l) {
		String h = "00000000" + Integer.toHexString(i);
		return h.substring(h.length() - l).toUpperCase();
	}
	
	private String byteToHexString(byte[] data, String prefix, String suffix, String delimiter) {
		StringBuffer dataString = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			String h = "00" + Integer.toHexString(data[i]);
			h = h.substring(h.length() - 2).toUpperCase();
			if (i > 0) dataString.append(delimiter);
			dataString.append(prefix);
			dataString.append(h);
			dataString.append(suffix);
		}
		return dataString.toString();
	}
}
