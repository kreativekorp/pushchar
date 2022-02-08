package com.kreative.pushchar.main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

public class NameEntryTable extends JTable {
	private static final long serialVersionUID = 1L;
	
	private static final float ROW_SPACING = 1.333f;
	private static final String NO_RESULTS = "No results found.";
	private static final Color PUA_COLOR = new Color(0xFF663399);
	private static final Color PUA_TEXT = Color.white;
	
	public NameEntryTable() {
		super(new NameEntryTableModel());
		this.setIntercellSpacing(new Dimension(0, 0));
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.setShowGrid(false);
		this.setTableHeader(null);
		
		FontMetrics fm = this.getFontMetrics(this.getFont());
		int tabWidth = fm.stringWidth("#" + (1 << 24));
		setColumnWidths(0, tabWidth, tabWidth, tabWidth, tabWidth);
		setColumnWidths(1, tabWidth/2, tabWidth/2, tabWidth/2, tabWidth/2);
		setColumnWidths(2, tabWidth*3/4, tabWidth*3/4, tabWidth*3/4, tabWidth*3/4);
		setColumnWidths(3, tabWidth/2, tabWidth/2, tabWidth/2, tabWidth/2);
		setColumnWidths(4, tabWidth, tabWidth*4, 0, tabWidth*4);
		setColumnWidths(5, tabWidth, tabWidth*2, 0, tabWidth*2);
		
		int rowHeight = (int)Math.round(fm.getHeight() * ROW_SPACING);
		this.setRowHeight(rowHeight);
		
		Dimension d = new Dimension(tabWidth*8 + tabWidth*3/4, rowHeight * 10);
		this.setPreferredScrollableViewportSize(d);
		this.setPreferredSize(d);
		
		this.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 1L;
			@Override
			public Component getTableCellRendererComponent(
				JTable t, Object v, boolean s, boolean f, int row, int col
			) {
				Component c = super.getTableCellRendererComponent(t, v, s, f, row, col);
				NameDatabase.NameEntry e = getEntry(row);
				if (e != null && e.isPUA() && !e.fonts.isEmpty()) {
					Font font = c.getFont();
					font = new Font(e.shortestFontName(), font.getStyle(), font.getSize());
					c.setFont(font);
				}
				return c;
			}
		});
		this.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 1L;
			private final PUALabel label = new PUALabel();
			@Override
			public Component getTableCellRendererComponent(
				JTable t, Object v, boolean s, boolean f, int row, int col
			) {
				Component c = super.getTableCellRendererComponent(t, v, s, f, row, col);
				label.setBackground(c.getBackground());
				label.setBorder(((JLabel)c).getBorder());
				label.setFont(c.getFont());
				label.setForeground(c.getForeground());
				label.setOpaque(c.isOpaque());
				label.setText(((JLabel)c).getText());
				return label;
			}
		});
	}
	
	public List<NameDatabase.NameEntry> getEntries() {
		return ((NameEntryTableModel)dataModel).getEntries();
	}
	
	public void setEntries(List<NameDatabase.NameEntry> entries) {
		((NameEntryTableModel)dataModel).setEntries(entries);
	}
	
	public NameDatabase.NameEntry getEntry(int row) {
		return ((NameEntryTableModel)dataModel).getEntry(row);
	}
	
	public String getCharacterString(int row) {
		return ((NameEntryTableModel)dataModel).getCharacterString(row);
	}
	
	public NameDatabase.NameEntry getSelectedEntry() {
		return getEntry(getSelectedRow());
	}
	
	public String getSelectedCharacterString() {
		return getCharacterString(getSelectedRow());
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (this.getRowCount() == 0) {
			Insets i = getInsets();
			int w = getWidth() - i.left - i.right;
			int h = getHeight() - i.top - i.bottom;
			FontMetrics fm = g.getFontMetrics();
			int x = i.left + (w - fm.stringWidth(NO_RESULTS)) / 2;
			int y = i.top + (h - fm.getHeight()) / 2 + fm.getAscent();
			antialias(g);
			g.setColor(Color.gray);
			g.drawString(NO_RESULTS, x, y);
		}
	}
	
	private void setColumnWidths(int index, int min, int pref, int max, int width) {
		TableColumn col = this.getColumnModel().getColumn(index);
		if (min > 0) col.setMinWidth(min);
		if (pref > 0) col.setPreferredWidth(pref);
		if (max > 0) col.setMaxWidth(max);
		if (width > 0) col.setWidth(width);
	}
	
	private static void antialias(Graphics g) {
		if (g instanceof Graphics2D) {
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON
			);
			g2.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON
			);
		}
	}
	
	private static class PUALabel extends JLabel {
		private static final long serialVersionUID = 1L;
		@Override
		protected void paintComponent(Graphics g) {
			if (isOpaque()) {
				g.setColor(getBackground());
				g.fillRect(0, 0, getWidth(), getHeight());
			}
			
			String s = this.getText();
			if (s == null || s.length() == 0) return;
			
			antialias(g);
			g.setFont(getFont().deriveFont(Font.BOLD));
			FontMetrics pm = g.getFontMetrics();
			Insets i = getInsets();
			int h = getHeight() - i.top - i.bottom;
			int sh = pm.getHeight();
			int sy = i.top + (h - sh) / 2;
			int y = sy + pm.getAscent();
			int sw = pm.stringWidth(s);
			int x = i.left;
			g.setColor(PUA_COLOR);
			g.fillRoundRect(x, sy, sw + 6, sh, 4, 4);
			g.setColor(PUA_TEXT);
			g.drawString(s, x + 3, y);
		}
	}
}
