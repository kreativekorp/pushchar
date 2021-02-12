package com.kreative.pushchar.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.Scrollable;

public class SectionPanel extends JComponent implements Scrollable {
	private static final long serialVersionUID = 1L;
	
	private static final Font HEADER_FONT = new Font("Dialog", Font.BOLD, 12);
	private static final Insets HEADER_PADDING = new Insets(3,3,3,3);
	private static final Color HEADER_COLOR = new Color(0xFF4D4C67);
	private static final Color HEADER_TEXT = Color.white;
	private static final Color PUA_COLOR = new Color(0xFF663399);
	private static final Color PUA_TEXT = Color.white;
	private static final Color UNDEF_COLOR = Color.lightGray;
	private static final float CELL_SPACING = 1.333f;
	private static final Color CELL_COLOR = Color.white;
	private static final Color CELL_TEXT = Color.black;
	
	private final Section section;
	private int selectedRow, selectedColumn;
	private final List<SectionPanelListener> listeners;
	
	public SectionPanel(Section section, Font font) {
		this.section = section;
		this.selectedRow = -1;
		this.selectedColumn = -1;
		this.listeners = new ArrayList<SectionPanelListener>();
		this.setFont(font);
	}
	
	public int getSelectedIndex() {
		if (selectedRow < 0 || selectedRow >= section.getRowCount()) return -1;
		if (selectedColumn < 0 || selectedColumn >= Section.COLUMN_COUNT) return -1;
		return section.getIndex(selectedRow, selectedColumn);
	}
	
	public String getSelectedChar() {
		if (selectedRow < 0 || selectedRow >= section.getRowCount()) return null;
		if (selectedColumn < 0 || selectedColumn >= Section.COLUMN_COUNT) return null;
		return section.getChar(selectedRow, selectedColumn);
	}
	
	public void setSelectedChar(int row, int column) {
		if (this.selectedRow != row || this.selectedColumn != column) {
			this.selectedRow = row;
			this.selectedColumn = column;
			for (SectionPanelListener l : listeners) {
				l.selectionChanged(this, section, row, column);
			}
			this.repaint();
		}
	}
	
	public void setSelectedCharAt(int x, int y) {
		Insets i = getInsets();
		int w = getWidth() - i.left - i.right;
		int h = getHeight() - i.top - i.bottom;
		
		FontMetrics hm = getFontMetrics(HEADER_FONT);
		int hh = hm.getHeight() + HEADER_PADDING.top + HEADER_PADDING.bottom;
		
		if (x < i.left || x >= i.left + w || y < i.top + hh || y >= i.top + h) {
			setSelectedChar(-1, -1);
		} else {
			int sx = (x - i.left) * Section.COLUMN_COUNT / w;
			int sy = (y - i.top - hh) * section.getRowCount() / (h - hh);
			setSelectedChar(sy, sx);
		}
	}
	
	public void setAutoselects(boolean autoselects) {
		if (autoselects) {
			addMouseListener(autoselector);
			addMouseMotionListener(autoselector);
		} else {
			removeMouseListener(autoselector);
			removeMouseMotionListener(autoselector);
		}
	}
	
	public void addSectionPanelListener(SectionPanelListener l) {
		listeners.add(l);
	}
	
	public void removeSectionPanelListener(SectionPanelListener l) {
		listeners.remove(l);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		if (g instanceof Graphics2D) {
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON
			);
		}
		
		Insets i = getInsets();
		int w = getWidth() - i.left - i.right;
		int h = getHeight() - i.top - i.bottom;
		Rectangle vr = getVisibleRect();
		
		g.setFont(HEADER_FONT);
		FontMetrics hm = g.getFontMetrics();
		int hh = hm.getHeight() + HEADER_PADDING.top + HEADER_PADDING.bottom;
		if (vr.intersects(i.left, i.top, w, hh)) {
			int hx = i.left + HEADER_PADDING.left;
			int hy = i.top + HEADER_PADDING.top + hm.getAscent();
			g.setColor(HEADER_COLOR);
			g.fillRect(i.left, i.top, w, hh);
			if (section.isPUA()) {
				int sy = hy - hm.getAscent();
				int sw = hm.stringWidth("PUA");
				int sh = hm.getHeight();
				g.setColor(PUA_COLOR);
				g.fillRoundRect(hx, sy, sw + 6, sh, 4, 4);
				g.setColor(PUA_TEXT);
				g.drawString("PUA", hx + 3, hy);
				hx += sw + 12;
			}
			g.setColor(HEADER_TEXT);
			g.drawString(section.getTitleWithCount(), hx, hy);
		}
		
		g.setFont(getFont());
		FontMetrics bm = g.getFontMetrics();
		for (int y = 0, ymax = section.getRowCount(); y < ymax; y++) {
			int y0 = (int)Math.round((h - hh) * y / (float)ymax);
			int y1 = (int)Math.round((h - hh) * (y + 1) / (float)ymax);
			int rt = i.top + hh + y0;
			int rh = y1 - y0;
			if (vr.intersects(i.left, rt, w, rh)) {
				int ry = rt + (rh - bm.getHeight()) / 2 + bm.getAscent();
				for (int x = 0; x < Section.COLUMN_COUNT; x++) {
					int x0 = (int)Math.round(w * x / (float)Section.COLUMN_COUNT);
					int x1 = (int)Math.round(w * (x + 1) / (float)Section.COLUMN_COUNT);
					int cl = i.left + x0;
					int cw = x1 - x0;
					String s = section.getChar(y, x);
					if (s == null) {
						g.setColor(UNDEF_COLOR);
						g.fillRect(cl, rt, cw, rh);
					} else {
						int cx = cl + (cw - bm.stringWidth(s)) / 2;
						boolean sel = (y == selectedRow && x == selectedColumn);
						g.setColor(sel ? SystemColor.textHighlight : CELL_COLOR);
						g.fillRect(cl, rt, cw, rh);
						g.setColor(sel ? SystemColor.textHighlightText : CELL_TEXT);
						Shape clip = g.getClip();
						g.clipRect(cl, rt, cw, rh);
						g.drawString(s, cx, ry);
						g.setClip(clip);
					}
				}
			}
		}
	}
	
	@Override
	public Dimension getMinimumSize() {
		FontMetrics hm = getFontMetrics(HEADER_FONT);
		int hh = hm.getHeight() + HEADER_PADDING.top + HEADER_PADDING.bottom;
		
		FontMetrics bm = getFontMetrics(getFont());
		int rh = (int)Math.round(bm.getHeight() * CELL_SPACING);
		
		Insets i = getInsets();
		int w = rh * Section.COLUMN_COUNT / 2 + i.left + i.right;
		int h = rh * section.getRowCount() + hh + i.top + i.bottom;
		return new Dimension(w, h);
	}
	
	@Override
	public Dimension getPreferredSize() {
		FontMetrics hm = getFontMetrics(HEADER_FONT);
		int hh = hm.getHeight() + HEADER_PADDING.top + HEADER_PADDING.bottom;
		
		FontMetrics bm = getFontMetrics(getFont());
		int rh = (int)Math.round(bm.getHeight() * CELL_SPACING);
		
		Insets i = getInsets();
		int w = rh * Section.COLUMN_COUNT + i.left + i.right;
		int h = rh * section.getRowCount() + hh + i.top + i.bottom;
		return new Dimension(w, h);
	}
	
	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}
	
	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}
	
	@Override
	public boolean getScrollableTracksViewportWidth() {
		return true;
	}
	
	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		FontMetrics bm = getFontMetrics(getFont());
		int rh = (int)Math.round(bm.getHeight() * CELL_SPACING);
		return rh;
	}
	
	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return visibleRect.height;
	}
	
	private final MouseAdapter autoselector = new MouseAdapter() {
		@Override
		public void mouseEntered(MouseEvent e) {
			setSelectedCharAt(e.getX(), e.getY());
		}
		@Override
		public void mouseMoved(MouseEvent e) {
			setSelectedCharAt(e.getX(), e.getY());
		}
		@Override
		public void mouseDragged(MouseEvent e) {
			setSelectedCharAt(e.getX(), e.getY());
		}
		@Override
		public void mouseExited(MouseEvent e) {
			setSelectedChar(-1, -1);
		}
	};
}
