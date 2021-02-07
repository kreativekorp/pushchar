package com.kreative.pushchar.legacy;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;

public class CopyMenuItem extends JMenuItem implements ActionListener, ClipboardOwner {
	private static final long serialVersionUID = 1L;
	
	private String copyString;
	
	public CopyMenuItem(String copyString) {
		super("Copy \u201C" + copyString + "\u201D");
		this.copyString = copyString;
		addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(new StringSelection(copyString), this);
	}
	
	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// Nothing.
	}
}
