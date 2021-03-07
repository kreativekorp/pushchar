package com.kreative.pushchar.test;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JWindow;

public class RobotTest {
	private static final JWindow dummyWindow = makeDummy();
	private static final JFrame popupWindow = makePopup();
	
	public static void main(String[] args) {
		try { System.setProperty("apple.awt.UIElement", "true"); } catch (Exception e) {}
		dummyWindow.setVisible(true);
	}
	
	private static JWindow makeDummy() {
		JWindow f = new JWindow();
		JPanel p = new JPanel();
		MouseListener m = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				popupWindow.setVisible(true);
			}
		};
		p.addMouseListener(m);
		p.setOpaque(true);
		p.setBackground(Color.red);
		f.setContentPane(p);
		f.setFocusable(false);
		f.setFocusableWindowState(false);
		f.setAlwaysOnTop(true);
		f.setSize(20, 20);
		return f;
	}
	
	private static JFrame makePopup() {
		JFrame f = new JFrame();
		JPanel p = new JPanel(new FlowLayout());
		JButton b = new JButton("A");
		ActionListener a = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Robot r = new Robot();
					Toolkit t = Toolkit.getDefaultToolkit();
					Clipboard c = t.getSystemClipboard();
					StringSelection s = new StringSelection("A");
					ClipboardOwner o = new ClipboardOwner() {
						@Override
						public void lostOwnership(Clipboard c, Transferable t) {}
					};
					c.setContents(s, o);
					popupWindow.setVisible(false);
					r.delay(10);
					r.keyPress(KeyEvent.VK_CONTROL);
					//r.keyPress(KeyEvent.VK_SHIFT);
					r.keyPress(KeyEvent.VK_V);
					r.keyRelease(KeyEvent.VK_V);
					//r.keyRelease(KeyEvent.VK_SHIFT);
					r.keyRelease(KeyEvent.VK_CONTROL);
				} catch (Exception x) {
					x.printStackTrace();
				}
			}
		};
		b.addActionListener(a);
		p.add(b);
		f.setContentPane(p);
		f.pack();
		f.setLocationRelativeTo(null);
		return f;
	}
}
