package com.kreative.pushchar.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class TriggerMenu extends JPopupMenu {
	private static final long serialVersionUID = 1L;
	
	public TriggerMenu(WindowManager wm) {
		final JFrame push = wm.getPushWindow();
		if (push != null) {
			JMenuItem mi = new JMenuItem("Open Character Picker");
			mi.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					push.setVisible(true);
				}
			});
			add(mi);
		}
		
		final JFrame search = wm.getSearchWindow();
		if (search != null) {
			JMenuItem mi = new JMenuItem("Open Character Search");
			mi.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					search.setVisible(true);
				}
			});
			add(mi);
		}
		
		final JFrame options = wm.getOptionsWindow();
		if (options != null) {
			addSeparator();
			JMenuItem mi = new JMenuItem("Options");
			mi.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					options.setVisible(true);
				}
			});
			add(mi);
		}
		
		addSeparator();
		JMenuItem mi = new JMenuItem(CopyMenuItem.IS_MAC_OS ? "Quit" : "Exit");
		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		add(mi);
	}
}
