package com.kreative.pushchar.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class PushCharFrame extends JFrame {
	private static final long serialVersionUID = 1;
	
	private final SelectorPanel selectorPanel;
	private final SectionListPanel mainPanel;
	private final CharDataLabel label;
	private final CopyMenuBuilder menuBuilder;
	private final NameResolver resolver;
	
	public PushCharFrame(Font font, boolean hidable, int[] pasteKeyStroke) {
		super("PushChar");
		SectionBuilder u = SectionBuilderFactory.getInstance().getUnicodeBuilder();
		
		resolver = new NameResolver();
		resolver.setDataFont(font);
		
		menuBuilder = new CopyMenuBuilder(resolver, (hidable ? this : null), pasteKeyStroke);
		
		label = new CharDataLabel(resolver);
		label.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		
		mainPanel = new SectionListPanel();
		mainPanel.setAutoselects(true);
		mainPanel.setFont(font);
		mainPanel.setSections(u.build(font));
		
		selectorPanel = new SelectorPanel();
		selectorPanel.getFontNamePane().setVisible(false);
		selectorPanel.getSectionBuilderPane().setVisible(false);
		selectorPanel.setSelectedFont(font);
		selectorPanel.setSelectedBuilder(u);
		
		JScrollPane mainPane = new JScrollPane(mainPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		mainPane.setOpaque(false);
		mainPane.getViewport().setOpaque(false);
		
		JPanel main = new JPanel(new BorderLayout());
		main.add(selectorPanel, BorderLayout.PAGE_START);
		main.add(selectorPanel.getFontNamePane(), BorderLayout.LINE_START);
		main.add(selectorPanel.getSectionBuilderPane(), BorderLayout.LINE_END);
		main.add(mainPane, BorderLayout.CENTER);
		main.add(label, BorderLayout.PAGE_END);
		setContentPane(main);
		setMinimumSize(new Dimension(300, 200));
		setSize(new Dimension(520, 560));
		setLocationRelativeTo(null);
		
		mainPanel.addSectionPanelMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				menuBuilder.receiveEvent(e);
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				menuBuilder.receiveEvent(e);
			}
		});
		
		mainPanel.addSectionPanelListener(new SectionPanelListener() {
			@Override
			public void selectionChanged(SectionPanel sp, Section s, int row, int column) {
				if (row < 0 || column < 0) {
					label.setDataChar(-1, null);
					menuBuilder.setDataChar(null);
				} else {
					int index = s.getIndex(row, column);
					String chars = s.getChar(row, column);
					label.setDataChar(index, chars);
					menuBuilder.setDataChar(chars);
				}
			}
		});
		
		selectorPanel.addSelectorPanelListener(new SelectorPanelListener() {
			@Override
			public void fontFamilyChanged(SelectorPanel sp, Font font, SectionBuilder builder) {
				mainPanel.clearSections();
				mainPanel.setFont(font);
				mainPanel.setSections(builder.build(font));
				resolver.setDataFont(font);
			}
			@Override
			public void fontStyleChanged(SelectorPanel sp, Font font, SectionBuilder builder) {
				mainPanel.setFont(font);
			}
			@Override
			public void sectionBuilderChanged(SelectorPanel sp, Font font, SectionBuilder builder) {
				mainPanel.setSections(builder.build(font));
			}
		});
	}
}
