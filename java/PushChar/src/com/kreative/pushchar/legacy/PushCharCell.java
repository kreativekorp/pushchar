package com.kreative.pushchar.legacy;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.SystemColor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import com.kreative.pushchar.legacy.unicode.CharacterData;
import com.kreative.pushchar.legacy.unicode.CharacterDatabase;

public class PushCharCell extends JLabel implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;
	
	private final int codePoint;
	private final JLabel footerLabel;
	private final CharacterDatabase cdb;
	
	public PushCharCell(Font font, int codePoint, JLabel footerLabel) {
		super(new String(Character.toChars(codePoint)));
		setFont(font);
		setHorizontalAlignment(JLabel.CENTER);
		setOpaque(true);
		setBackground(Color.white);
		setForeground(Color.black);
		FontMetrics fm = getFontMetrics(font);
		int vp = Math.max(2, (int)Math.round(fm.getHeight() / 6f));
		setBorder(BorderFactory.createEmptyBorder(vp, 2, vp, 2));
		addMouseListener(this);
		addMouseMotionListener(this);
		this.codePoint = codePoint;
		this.footerLabel = footerLabel;
		this.cdb = CharacterDatabase.instance();
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		setBackground(SystemColor.textHighlight);
		setForeground(SystemColor.textHighlightText);
		String h = Integer.toHexString(codePoint).toUpperCase();
		while (h.length() < 4) h = "0" + h;
		String s = "U+" + h + "    #" + codePoint;
		CharacterData cd = cdb.get(codePoint);
		if (cd != null) s += "    " + cd.category + "    " + cd;
		footerLabel.setText(s);
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		setBackground(Color.white);
		setForeground(Color.black);
		footerLabel.setText(" ");
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if (e.isPopupTrigger()) {
			new CopyMenu(codePoint).show(e.getComponent(), e.getX(), e.getY());
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger()) {
			new CopyMenu(codePoint).show(e.getComponent(), e.getX(), e.getY());
		}
	}
	
	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mouseMoved(MouseEvent e) {}
	@Override public void mouseDragged(MouseEvent e) {}
}
