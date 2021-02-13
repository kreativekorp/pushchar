package com.kreative.pushchar.main;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JMenuItem;

public class CopyMenuItem extends JMenuItem implements ActionListener, ClipboardOwner {
	private static final long serialVersionUID = 1L;
	
	public static final int shortcutKeyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	public static final int shortcutKey = keyMaskToKey(shortcutKeyMask);
	private static final int keyMaskToKey(int keyMask) {
		switch (keyMask) {
			case KeyEvent.SHIFT_MASK: return KeyEvent.VK_SHIFT;
			case KeyEvent.CTRL_MASK: return KeyEvent.VK_CONTROL;
			case KeyEvent.META_MASK: return KeyEvent.VK_META;
			case KeyEvent.ALT_MASK: return KeyEvent.VK_ALT;
			case KeyEvent.ALT_GRAPH_MASK: return KeyEvent.VK_ALT_GRAPH;
			default: return 0;
		}
	}
	
	private final String copyString;
	private final Window hideWindow;
	private final int[] keyStroke;
	private final Window showWindow;
	
	public CopyMenuItem(
		String title, String copyString,
		Window hideWindow, int[] keyStroke, Window showWindow
	) {
		super(title.replace("@", copyString));
		this.copyString = copyString;
		this.hideWindow = hideWindow;
		this.keyStroke = keyStroke;
		this.showWindow = showWindow;
		addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(new StringSelection(copyString), this);
		if (hideWindow != null) hideWindow.setVisible(false);
		if (keyStroke != null && keyStroke.length > 0) {
			try {
				Robot r = new Robot();
				r.delay(10);
				for (int i = 0, n = keyStroke.length; i < n; i++) r.keyPress(keyStroke[i]);
				for (int i = keyStroke.length - 1; i >= 0; i--) r.keyRelease(keyStroke[i]);
				r.delay(10);
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
		if (showWindow != null) showWindow.setVisible(true);
	}
	
	@Override
	public void lostOwnership(Clipboard c, Transferable t) {
		// I don't care.
	}
}
