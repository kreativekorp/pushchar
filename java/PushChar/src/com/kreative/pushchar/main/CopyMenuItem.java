package com.kreative.pushchar.main;

import java.awt.MouseInfo;
import java.awt.Point;
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
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;

public class CopyMenuItem extends JMenuItem {
	private static final long serialVersionUID = 1L;
	
	public static final boolean IS_MAC_OS;
	static {
		boolean isMacOS;
		try { isMacOS = System.getProperty("os.name").toUpperCase().contains("MAC OS"); }
		catch (Exception e) { isMacOS = false; }
		IS_MAC_OS = isMacOS;
	}
	
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
	
	public CopyMenuItem(final String title, final String copyString, final Window hideWindow, final int[] keyStroke) {
		super(title.replace("@", copyString));
		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				copy(copyString, hideWindow, keyStroke);
			}
		});
	}
	
	public static void copy(String copyString, Window hideWindow, int[] keyStroke) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(new StringSelection(copyString), new ClipboardOwner() {
			@Override public void lostOwnership(Clipboard c, Transferable t) {}
		});
		if (hideWindow != null) hideWindow.setVisible(false);
		if (keyStroke != null && keyStroke.length > 0) {
			try {
				Robot r = new Robot();
				r.delay(10);
				if (IS_MAC_OS) {
					// Hack for Mac OS to get focus back on the front window.
					Point p = MouseInfo.getPointerInfo().getLocation();
					int m = Toolkit.getDefaultToolkit().getScreenSize().width / 2;
					r.mouseMove(m, 10);
					r.mousePress(MouseEvent.BUTTON1_DOWN_MASK);
					r.mouseRelease(MouseEvent.BUTTON1_DOWN_MASK);
					r.mousePress(MouseEvent.BUTTON1_DOWN_MASK);
					r.mouseRelease(MouseEvent.BUTTON1_DOWN_MASK);
					r.mouseMove(p.x, p.y);
					for (int i = 0, n = keyStroke.length; i < n; i++) {
						r.delay(10);
						r.keyPress(keyStroke[i]);
					}
					for (int i = keyStroke.length - 1; i >= 0; i--) {
						r.keyRelease(keyStroke[i]);
					}
				} else {
					for (int i = 0, n = keyStroke.length; i < n; i++) {
						r.keyPress(keyStroke[i]);
					}
					for (int i = keyStroke.length - 1; i >= 0; i--) {
						r.keyRelease(keyStroke[i]);
					}
				}
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
	}
}
