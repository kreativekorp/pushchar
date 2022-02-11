package com.kreative.pushchar.main;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class SearchCharFrame extends JFrame {
	private static final long serialVersionUID = 1;
	
	private final JTextField searchField;
	private final NameEntryTable resultsTable;
	private final NameDatabase database;
	private final NameResolver resolver;
	private final JFrame hideWindow;
	private final int[] pasteKeyStroke;
	private final CopyMenuBuilder menuBuilder;
	
	public SearchCharFrame(boolean hidable, int[] pasteKeyStroke) {
		super("SearchChar");
		this.searchField = new JTextField();
		this.resultsTable = new NameEntryTable();
		this.database = new NameDatabase();
		this.resolver = new NameResolver();
		this.hideWindow = (hidable ? this : null);
		this.pasteKeyStroke = pasteKeyStroke;
		this.menuBuilder = new CopyMenuBuilder(resolver, hideWindow, pasteKeyStroke);
		
		JScrollPane resultsPane = new JScrollPane(
			resultsTable,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
		);
		
		JPanel main = new JPanel(new BorderLayout(4, 4));
		main.add(searchField, BorderLayout.PAGE_START);
		main.add(resultsPane, BorderLayout.CENTER);
		main.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		setContentPane(main);
		pack();
		setLocationRelativeTo(null);
		
		searchField.addKeyListener(myKeyListener);
		searchField.getDocument().addDocumentListener(new DocumentListener() {
			@Override public void changedUpdate(DocumentEvent e) { doIt(); }
			@Override public void insertUpdate(DocumentEvent e) { doIt(); }
			@Override public void removeUpdate(DocumentEvent e) { doIt(); }
			private void doIt() {
				String query = searchField.getText();
				List<NameDatabase.NameEntry> entries = database.find(query);
				resultsTable.setEntries(entries);
				if (entries.isEmpty()) return;
				resultsTable.clearSelection();
				resultsTable.addRowSelectionInterval(0, 0);
				resultsTable.scrollRectToVisible(resultsTable.getCellRect(0, 0, true));
			}
		});
		
		resultsTable.addKeyListener(myKeyListener);
		resultsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent evt) {
				NameDatabase.NameEntry e = resultsTable.getSelectedEntry();
				if (e == null || e.fonts.isEmpty()) resolver.setDataFont(null);
				else resolver.setDataFont(new Font(e.shortestFontName(), 0, 12));
				menuBuilder.setDataChar(resultsTable.getSelectedCharacterString());
			}
		});
		resultsTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				menuBuilder.receiveEvent(e);
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				menuBuilder.receiveEvent(e);
			}
		});
	}
	
	private final KeyAdapter myKeyListener = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
			int i, n;
			switch (e.getKeyCode()) {
				case KeyEvent.VK_ESCAPE:
				case KeyEvent.VK_CLEAR:
					searchField.setText("");
					searchField.requestFocusInWindow();
					e.consume();
					break;
				case KeyEvent.VK_UP:
					n = resultsTable.getRowCount();
					if (n > 0) {
						i = resultsTable.getSelectedRow() - 1;
						if (i < 0 || i >= n) i = n - 1;
						resultsTable.clearSelection();
						resultsTable.addRowSelectionInterval(i, i);
						resultsTable.scrollRectToVisible(resultsTable.getCellRect(i, 0, true));
					}
					e.consume();
					break;
				case KeyEvent.VK_DOWN:
					n = resultsTable.getRowCount();
					if (n > 0) {
						i = resultsTable.getSelectedRow() + 1;
						if (i < 0 || i >= n) i = 0;
						resultsTable.clearSelection();
						resultsTable.addRowSelectionInterval(i, i);
						resultsTable.scrollRectToVisible(resultsTable.getCellRect(i, 0, true));
					}
					e.consume();
					break;
				case KeyEvent.VK_PAGE_UP:
					n = resultsTable.getRowCount();
					if (n > 0) {
						resultsTable.clearSelection();
						resultsTable.addRowSelectionInterval(0, 0);
						resultsTable.scrollRectToVisible(resultsTable.getCellRect(0, 0, true));
					}
					e.consume();
					break;
				case KeyEvent.VK_PAGE_DOWN:
					n = resultsTable.getRowCount();
					if (n > 0) {
						resultsTable.clearSelection();
						resultsTable.addRowSelectionInterval(n-1, n-1);
						resultsTable.scrollRectToVisible(resultsTable.getCellRect(n-1, 0, true));
					}
					e.consume();
					break;
				case KeyEvent.VK_ENTER:
					e.consume();
					break;
			}
		}
		@Override
		public void keyReleased(KeyEvent e) {
			switch (e.getKeyCode()) {
				case KeyEvent.VK_ESCAPE:
				case KeyEvent.VK_CLEAR:
				case KeyEvent.VK_UP:
				case KeyEvent.VK_DOWN:
				case KeyEvent.VK_PAGE_UP:
				case KeyEvent.VK_PAGE_DOWN:
					e.consume();
					break;
				case KeyEvent.VK_ENTER:
					String chars = resultsTable.getSelectedCharacterString();
					if (chars != null) {
						if (isShortcutKeyDown(e)) {
							CopyMenuItem.copy(chars, hideWindow, pasteKeyStroke);
						} else if (!e.isShiftDown()) {
							CopyMenuItem.copy(chars, hideWindow, null);
						} else {
							CopyMenuItem.copy(chars, null, null);
						}
						if (!isVisible()) searchField.setText("");
					}
					e.consume();
					break;
			}
		}
	};
	
	private static boolean isShortcutKeyDown(KeyEvent e) {
		switch (Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) {
			case KeyEvent.SHIFT_MASK: return e.isShiftDown();
			case KeyEvent.CTRL_MASK: return e.isControlDown();
			case KeyEvent.META_MASK: return e.isMetaDown();
			case KeyEvent.ALT_MASK: return e.isAltDown();
			case KeyEvent.ALT_GRAPH_MASK: return e.isAltGraphDown();
			default: return false;
		}
	}
}
