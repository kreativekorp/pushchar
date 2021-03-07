package com.kreative.pushchar.main;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

public class TriggerWindow extends JWindow {
	private static final long serialVersionUID = 1L;
	
	private final JLabel push;
	private final JLabel search;
	private Window pushWindow;
	private Window searchWindow;
	
	public TriggerWindow() {
		push = new JLabel(new ImageIcon(TriggerWindow.class.getResource("push.png")));
		search = new JLabel(new ImageIcon(TriggerWindow.class.getResource("search.png")));
		push.setVisible(false);
		search.setVisible(false);
		pushWindow = null;
		searchWindow = null;
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(push, BorderLayout.WEST);
		panel.add(search, BorderLayout.EAST);
		
		setContentPane(panel);
		setFocusable(false);
		setFocusableWindowState(false);
		setAlwaysOnTop(true);
		pack();
		
		push.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (pushWindow == null) return;
				pushWindow.setVisible(true);
			}
		});
		
		search.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (searchWindow == null) return;
				searchWindow.setVisible(true);
			}
		});
	}
	
	public Window getPushWindow() {
		return pushWindow;
	}
	
	public Window getSearchWindow() {
		return searchWindow;
	}
	
	public void setPushWindow(Window pushWindow) {
		if (pushWindow instanceof JFrame) {
			JFrame f = (JFrame)pushWindow;
			f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		}
		if (this.pushWindow != null) this.pushWindow.removeWindowListener(windowListener);
		this.pushWindow = pushWindow;
		if (this.pushWindow != null) this.pushWindow.addWindowListener(windowListener);
		push.setVisible(this.pushWindow != null);
		pack();
	}
	
	public void setSearchWindow(Window searchWindow) {
		if (searchWindow instanceof JFrame) {
			JFrame f = (JFrame)searchWindow;
			f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		}
		if (this.searchWindow != null) this.searchWindow.removeWindowListener(windowListener);
		this.searchWindow = searchWindow;
		if (this.searchWindow != null) this.searchWindow.addWindowListener(windowListener);
		search.setVisible(this.searchWindow != null);
		pack();
	}
	
	private final WindowListener windowListener = new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			e.getWindow().setVisible(false);
		}
	};
}
