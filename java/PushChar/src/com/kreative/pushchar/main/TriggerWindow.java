package com.kreative.pushchar.main;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

public class TriggerWindow extends JWindow {
	private static final long serialVersionUID = 1L;
	
	public static enum Position {
		NORTHWEST {
			@Override
			protected Point getLocation(Rectangle screenRect, Dimension size) {
				return new Point(screenRect.x, screenRect.y);
			}
			@Override
			protected Point getMenuLocation(MouseEvent e, TriggerMenu m) {
				return e.getPoint();
			}
		},
		NORTHEAST {
			@Override
			protected Point getLocation(Rectangle screenRect, Dimension size) {
				int x = screenRect.x + screenRect.width - size.width;
				return new Point(x, screenRect.y);
			}
			@Override
			protected Point getMenuLocation(MouseEvent e, TriggerMenu m) {
				int x = e.getX() - m.getPreferredSize().width + 1;
				return new Point(x, e.getY());
			}
		},
		SOUTHWEST {
			@Override
			protected Point getLocation(Rectangle screenRect, Dimension size) {
				int y = screenRect.y + screenRect.height - size.height;
				return new Point(screenRect.x, y);
			}
			@Override
			protected Point getMenuLocation(MouseEvent e, TriggerMenu m) {
				int y = e.getY() - m.getPreferredSize().height + 1;
				return new Point(e.getX(), y);
			}
		},
		SOUTHEAST {
			@Override
			protected Point getLocation(Rectangle screenRect, Dimension size) {
				int x = screenRect.x + screenRect.width - size.width;
				int y = screenRect.y + screenRect.height - size.height;
				return new Point(x, y);
			}
			@Override
			protected Point getMenuLocation(MouseEvent e, TriggerMenu m) {
				Dimension ps = m.getPreferredSize();
				int x = e.getX() - ps.width + 1;
				int y = e.getY() - ps.height + 1;
				return new Point(x, y);
			}
		};
		protected abstract Point getLocation(Rectangle screenRect, Dimension size);
		protected abstract Point getMenuLocation(MouseEvent e, TriggerMenu m);
	}
	
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
	
	private final TriggerMenu menu;
	private final JLabel push;
	private final JLabel search;
	private JFrame pushWindow;
	private JFrame searchWindow;
	private Position position;
	private Orientation orientation;
	
	public TriggerWindow(WindowManager wm, Position position, Orientation orientation) {
		this.menu = new TriggerMenu(wm);
		this.push = new JLabel(new ImageIcon(TriggerWindow.class.getResource("push.png")));
		this.search = new JLabel(new ImageIcon(TriggerWindow.class.getResource("search.png")));
		this.push.setVisible(false);
		this.search.setVisible(false);
		this.pushWindow = null;
		this.searchWindow = null;
		this.position = position;
		this.orientation = orientation;
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(push, orientation.pushConstraints);
		panel.add(search, orientation.searchConstraints);
		
		setContentPane(panel);
		setFocusable(false);
		setFocusableWindowState(false);
		setAlwaysOnTop(true);
		packAndSetLocation();
		
		push.addMouseListener(new MyMouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.isPopupTrigger()) return;
				if (pushWindow == null) return;
				pushWindow.setVisible(true);
			}
		});
		
		search.addMouseListener(new MyMouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.isPopupTrigger()) return;
				if (searchWindow == null) return;
				searchWindow.setVisible(true);
			}
		});
	}
	
	private class MyMouseAdapter extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			if (e.isPopupTrigger()) {
				Point p = position.getMenuLocation(e, menu);
				menu.show(e.getComponent(), p.x, p.y);
			}
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger()) {
				Point p = position.getMenuLocation(e, menu);
				menu.show(e.getComponent(), p.x, p.y);
			}
		}
	}
	
	public JFrame getPushWindow() {
		return pushWindow;
	}
	
	public JFrame getSearchWindow() {
		return searchWindow;
	}
	
	public void setPushWindow(JFrame pushWindow) {
		if (pushWindow != null) pushWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.pushWindow = pushWindow;
		push.setVisible(this.pushWindow != null);
		packAndSetLocation();
	}
	
	public void setSearchWindow(JFrame searchWindow) {
		if (searchWindow != null) searchWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.searchWindow = searchWindow;
		search.setVisible(this.searchWindow != null);
		packAndSetLocation();
	}
	
	public Position getPosition() {
		return position;
	}
	
	public Orientation getOrientation() {
		return orientation;
	}
	
	public void setPosition(Position position) {
		this.position = position;
		packAndSetLocation();
	}
	
	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
		Container panel = getContentPane();
		panel.add(push, orientation.pushConstraints);
		panel.add(search, orientation.searchConstraints);
		packAndSetLocation();
	}
	
	private void packAndSetLocation() {
		pack();
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsConfiguration config = env.getScreenDevices()[0].getDefaultConfiguration();
		Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(config);
		Rectangle screenRect = config.getBounds();
		screenRect.x += insets.left;
		screenRect.y += insets.top;
		screenRect.width -= insets.left + insets.right;
		screenRect.height -= insets.top + insets.bottom;
		setLocation(position.getLocation(screenRect, getSize()));
	}
}
