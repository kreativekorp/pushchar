package com.kreative.pushchar.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.JComponent;

public class CharDataLabel extends JComponent {
	private static final long serialVersionUID = 1L;
	
	private static final Color PUA_COLOR = new Color(0xFF663399);
	private static final Color PUA_TEXT = Color.white;
	
	private final NameResolver resolver;
	private int index = -1;
	private int codePoint = -1;
	private String category = null;
	private String name = null;
	
	public CharDataLabel(NameResolver resolver) {
		this.resolver = resolver;
	}
	
	public void setDataChar(int index, String s) {
		if (s == null) {
			this.index = -1;
			this.codePoint = -1;
			this.category = null;
			this.name = null;
		} else if (s.codePointCount(0, s.length()) == 1) {
			this.index = index;
			this.codePoint = s.codePointAt(0);
			this.category = resolver.getCategory(codePoint);
			this.name = resolver.getName(codePoint);
		} else {
			this.index = index;
			this.codePoint = -1;
			this.category = null;
			StringBuffer sb = new StringBuffer("U");
			int i = 0, n = s.length();
			while (i < n) {
				sb.append("+");
				int ch = s.codePointAt(i);
				String h = Integer.toHexString(ch).toUpperCase();
				if (h.length() < 4) h = ("0000" + h).substring(h.length());
				sb.append(h);
				i += Character.charCount(ch);
			}
			this.name = sb.toString();
		}
		this.repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
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
		
		g.setColor(getForeground());
		g.setFont(getFont());
		FontMetrics fm = g.getFontMetrics();
		int tabWidth = fm.stringWidth("#" + (1 << 24));
		
		Insets i = getInsets();
		int h = getHeight() - i.top - i.bottom;
		int y = i.top + (h - fm.getHeight()) / 2 + fm.getAscent();
		int x = i.left;
		
		if (index >= 0) {
			String s = Integer.toHexString(index).toUpperCase();
			if ((s.length() & 1) == 1) s = "0" + s;
			g.drawString("$" + s, x, y);
			
		}
		x += tabWidth;
		if (index >= 0) g.drawString("#" + index, x, y);
		x += tabWidth;
		
		if (codePoint >= 0) {
			String s = Integer.toHexString(codePoint).toUpperCase();
			if (s.length() < 4) s = ("0000" + s).substring(s.length());
			g.drawString("U+" + s, x, y);
			x += tabWidth;
			
			if (codePoint >= 0xE000 && codePoint < 0xF900 || codePoint >= 0xF0000) {
				g.setFont(getFont().deriveFont(Font.BOLD));
				FontMetrics pm = g.getFontMetrics();
				int sy = y - pm.getAscent();
				int sw = pm.stringWidth("PUA");
				int sh = pm.getHeight();
				g.setColor(PUA_COLOR);
				g.fillRoundRect(x, sy, sw + 6, sh, 4, 4);
				g.setColor(PUA_TEXT);
				g.drawString("PUA", x + 3, y);
				g.setColor(getForeground());
				g.setFont(getFont());
				x += tabWidth * 3/4;
			}
			
			if (category != null) g.drawString(category, x, y);
			x += tabWidth / 2;
		}
		
		if (name != null) g.drawString(name, x, y);
	}
	
	@Override
	public Dimension getMinimumSize() {
		FontMetrics fm = getFontMetrics(getFont());
		int tabWidth = fm.stringWidth("#" + Integer.MAX_VALUE);
		Insets i = getInsets();
		int w = tabWidth * 4 + i.left + i.right;
		int h = fm.getHeight() + i.top + i.bottom;
		return new Dimension(w, h);
	}
	
	@Override
	public Dimension getPreferredSize() {
		FontMetrics fm = getFontMetrics(getFont());
		int tabWidth = fm.stringWidth("#" + Integer.MAX_VALUE);
		Insets i = getInsets();
		int w = tabWidth * 8 + i.left + i.right;
		int h = fm.getHeight() + i.top + i.bottom;
		return new Dimension(w, h);
	}
}
