package com.kreative.pushchar.main;

import java.util.List;
import javax.swing.table.AbstractTableModel;

public class NameEntryTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	
	private List<NameDatabase.NameEntry> entries = null;
	
	public List<NameDatabase.NameEntry> getEntries() {
		return this.entries;
	}
	
	public void setEntries(List<NameDatabase.NameEntry> entries) {
		this.entries = entries;
		this.fireTableDataChanged();
	}
	
	@Override
	public int getColumnCount() {
		return 6;
	}
	
	@Override
	public Class<?> getColumnClass(int col) {
		return String.class;
	}
	
	@Override
	public String getColumnName(int col) {
		switch (col) {
			case 0: return "CP";
			case 1: return "Char";
			case 2: return "PUA";
			case 3: return "GC";
			case 4: return "Name";
			case 5: return "Font";
			default: return null;
		}
	}
	
	@Override
	public int getRowCount() {
		if (entries == null) return 0;
		return entries.size();
	}
	
	@Override
	public Object getValueAt(int row, int col) {
		if (entries == null || row < 0 || row >= entries.size()) return null;
		NameDatabase.NameEntry e = entries.get(row);
		switch (col) {
			case 0:
				String h = Integer.toHexString(e.codePoint).toUpperCase();
				if (h.length() < 4) h = ("0000" + h).substring(h.length());
				return "U+" + h;
			case 1:
				return String.valueOf(Character.toChars(e.codePoint));
			case 2:
				return e.isPUA() ? "PUA" : null;
			case 3:
				return e.category;
			case 4:
				return e.name;
			case 5:
				String font = e.shortestFontName();
				if (e.fonts.size() > 1) font += " (+" + (e.fonts.size()-1) + ")";
				return font;
			default:
				return null;
		}
	}
	
	public NameDatabase.NameEntry getEntry(int row) {
		if (entries == null || row < 0 || row >= entries.size()) return null;
		return entries.get(row);
	}
	
	public String getCharacterString(int row) {
		if (entries == null || row < 0 || row >= entries.size()) return null;
		NameDatabase.NameEntry e = entries.get(row);
		return String.valueOf(Character.toChars(e.codePoint));
	}
}
