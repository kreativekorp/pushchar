package com.kreative.pushchar.main;

import java.awt.Window;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Map;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import com.kreative.pushchar.ttflib.PuaaTable;

public class CopyMenuBuilder {
	private final Map<Integer,String> entityMap;
	private final NameResolver resolver;
	private final Window parentWindow;
	private final int[] pasteKeyStroke;
	private String chars = null;
	
	public CopyMenuBuilder(NameResolver resolver, Window parentWindow, int[] pasteKeyStroke) {
		PuaaTable entities = PuaaCache.getPuaaTable("entities.ucd");
		this.entityMap = entities.getPropertyMap("HTML_Entity");
		this.resolver = resolver;
		this.parentWindow = parentWindow;
		this.pasteKeyStroke = pasteKeyStroke;
	}
	
	public void setDataChar(String s) {
		this.chars = s;
	}
	
	public void receiveEvent(MouseEvent e) {
		if (chars != null && e.isPopupTrigger()) {
			JPopupMenu m = buildMenu(parentWindow, pasteKeyStroke);
			m.show(e.getComponent(), e.getX(), e.getY());
		}
	}
	
	private JPopupMenu buildMenu(Window hw, int[] ks) {
		JPopupMenu menu = new JPopupMenu();
		menu.add(new CopyMenuItem("Copy", chars, null, null));
		if (hw != null) {
			menu.add(new CopyMenuItem("Copy and Close", chars, hw, null));
			if (ks != null) {
				menu.add(new CopyMenuItem("Copy and Paste", chars, hw, ks));
			}
		}
		menu.addSeparator();
		menu.add(buildSubmenu("Copy", chars, null, null));
		if (hw != null) {
			menu.add(buildSubmenu("Copy and Close", chars, hw, null));
			if (ks != null) {
				menu.add(buildSubmenu("Copy and Paste", chars, hw, ks));
			}
		}
		return menu;
	}
	
	private JMenu buildSubmenu(String title, String chars, Window hw, int[] ks) {
		JMenu menu = new JMenu(title);
		int[] utf8 = toUTF8(chars);
		int[] utf16 = toUTF16(chars);
		int[] utf32 = toUTF32(chars);
		String[] names = new String[utf32.length];
		String[] entities = new String[utf32.length];
		String[] python = new String[utf32.length];
		for (int i = 0; i < utf32.length; i++) {
			names[i] = resolver.getName(utf32[i]);
			entities[i] = entityMap.get(utf32[i]);
			if (entities[i] == null) entities[i] = ("&#" + utf32[i] + ";");
			python[i] = (
				(utf32[i] < 0x10000)
				? ("\\u" + toHexString(utf32[i], 4))
				: ("\\U" + toHexString(utf32[i], 8))
			);
		}
		menu.add(new CopyMenuItem("Text: @", chars, hw, ks));
		menu.add(new CopyMenuItem("Dec: @", toDecString(utf32, "", "", ", "), hw, ks));
		menu.add(new CopyMenuItem("Hex: @", toHexString(utf32, 4, "", "", ", "), hw, ks));
		menu.add(new CopyMenuItem("U+: @", "U+" + toHexString(utf32, 4, "", "", "+"), hw, ks));
		menu.add(new CopyMenuItem("Name: @", join(names, ", "), hw, ks));
		menu.addSeparator();
		menu.add(new CopyMenuItem("HTML Name: @", join(entities, ""), hw, ks));
		menu.add(new CopyMenuItem("HTML Dec: @", toDecString(utf32, "&#", ";", ""), hw, ks));
		menu.add(new CopyMenuItem("HTML Hex: @", toHexString(utf32, 0, "&#x", ";", ""), hw, ks));
		menu.add(new CopyMenuItem("URL: @", toHexString(utf8, 2, "%", "", ""), hw, ks));
		menu.add(new CopyMenuItem("C/C++: @", toHexString(utf8, 2, "\\x", "", ""), hw, ks));
		menu.add(new CopyMenuItem("Java: @", toHexString(utf16, 4, "\\u", "", ""), hw, ks));
		menu.add(new CopyMenuItem("Python Text: @", "u'" + chars + "'", hw, ks));
		menu.add(new CopyMenuItem("Python Hex: @", "u'" + join(python, "") + "'", hw, ks));
		menu.addSeparator();
		menu.add(new CopyMenuItem("UTF-8: @", toHexString(utf8, 2, true, " "), hw, ks));
		menu.add(new CopyMenuItem("UTF-16BE: @", toHexString(utf16, 4, false, " "), hw, ks));
		menu.add(new CopyMenuItem("UTF-16LE: @", toHexString(utf16, 4, true, " "), hw, ks));
		menu.add(new CopyMenuItem("UTF-32BE: @", toHexString(utf32, 8, false, " "), hw, ks));
		menu.add(new CopyMenuItem("UTF-32LE: @", toHexString(utf32, 8, true, " "), hw, ks));
		return menu;
	}
	
	private static int[] toUTF8(String s) {
		try {
			byte[] b = s.getBytes("UTF-8");
			int[] v = new int[b.length];
			for (int i = 0; i < b.length; i++) v[i] = b[i] & 0xFF;
			return v;
		} catch (IOException e) {
			return null;
		}
	}
	
	private static int[] toUTF16(String s) {
		char[] c = s.toCharArray();
		int[] v = new int[c.length];
		for (int i = 0; i < c.length; i++) v[i] = c[i] & 0xFFFF;
		return v;
	}
	
	private static int[] toUTF32(String s) {
		int p = 0, i = 0, n = s.length();
		int[] tmp = new int[n];
		while (i < n) {
			int ch = s.codePointAt(i);
			tmp[p++] = ch;
			i += Character.charCount(ch);
		}
		int[] v = new int[p];
		for (i = 0; i < p; i++) v[i] = tmp[i];
		return v;
	}
	
	private static String toHexString(int v, int p) {
		String s = Integer.toHexString(v).toUpperCase();
		while (s.length() < p) s = "0" + s;
		return s;
	}
	
	private static String toHexString(int[] v, int p, String pfx, String sfx, String delim) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < v.length; i++) {
			if (i > 0) sb.append(delim);
			sb.append(pfx);
			sb.append(toHexString(v[i], p));
			sb.append(sfx);
		}
		return sb.toString();
	}
	
	private static String toHexString(int[] v, int p, boolean le, String delim) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < v.length; i++) {
			if (i > 0) sb.append(delim);
			if (p > 6 && !le) { sb.append(toHexString((v[i] >> 24) & 0xFF, 2)); sb.append(delim); }
			if (p > 4 && !le) { sb.append(toHexString((v[i] >> 16) & 0xFF, 2)); sb.append(delim); }
			if (p > 2 && !le) { sb.append(toHexString((v[i] >>  8) & 0xFF, 2)); sb.append(delim); }
			sb.append(toHexString(v[i] & 0xFF, 2));
			if (p > 2 &&  le) { sb.append(delim); sb.append(toHexString((v[i] >>  8) & 0xFF, 2)); }
			if (p > 4 &&  le) { sb.append(delim); sb.append(toHexString((v[i] >> 16) & 0xFF, 2)); }
			if (p > 6 &&  le) { sb.append(delim); sb.append(toHexString((v[i] >> 24) & 0xFF, 2)); }
		}
		return sb.toString();
	}
	
	private static String toDecString(int[] v, String pfx, String sfx, String delim) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < v.length; i++) {
			if (i > 0) sb.append(delim);
			sb.append(pfx);
			sb.append(v[i]);
			sb.append(sfx);
		}
		return sb.toString();
	}
	
	private static String join(String[] strings, String delimiter) {
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		for (String s : strings) {
			if (first) first = false;
			else sb.append(delimiter);
			sb.append(s);
		}
		return sb.toString();
	}
}
