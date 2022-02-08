package com.kreative.pushchar.main;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

public class TriggerWindow extends JWindow {
	private static final long serialVersionUID = 1L;
	
	public static enum Orientation {
		WEST_EAST(BorderLayout.WEST, BorderLayout.EAST),
		NORTH_SOUTH(BorderLayout.NORTH, BorderLayout.SOUTH),
		EAST_WEST(BorderLayout.EAST, BorderLayout.WEST),
		SOUTH_NORTH(BorderLayout.SOUTH, BorderLayout.NORTH);
		private final String pushConstraints;
		private final String searchConstraints;
		private Orientation(String pc, String sc) {
			this.pushConstraints = pc;
			this.searchConstraints = sc;
		}
	}
	
	private final JLabel push;
	private final JLabel search;
	private JFrame pushWindow;
	private JFrame searchWindow;
	
	public TriggerWindow(Orientation orientation) {
		push = new JLabel(new ImageIcon(TriggerWindow.class.getResource("push.png")));
		search = new JLabel(new ImageIcon(TriggerWindow.class.getResource("search.png")));
		push.setVisible(false);
		search.setVisible(false);
		pushWindow = null;
		searchWindow = null;
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(push, orientation.pushConstraints);
		panel.add(search, orientation.searchConstraints);
		
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
	
	public void setPushWindow(JFrame pushWindow) {
		if (pushWindow != null) pushWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.pushWindow = pushWindow;
		push.setVisible(this.pushWindow != null);
		pack();
	}
	
	public void setSearchWindow(JFrame searchWindow) {
		if (searchWindow != null) searchWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.searchWindow = searchWindow;
		search.setVisible(this.searchWindow != null);
		pack();
	}
}
