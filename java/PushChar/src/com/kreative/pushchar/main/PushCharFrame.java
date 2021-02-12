package com.kreative.pushchar.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class PushCharFrame extends JFrame {
	private static final long serialVersionUID = 1;
	
	private final SelectorPanel selectorPanel;
	private final SectionListPanel mainPanel;
	private final CharDataLabel label;
	
	public PushCharFrame(Font font) {
		super("PushChar");
		SectionBuilder u = SectionBuilderFactory.getInstance().getUnicodeBuilder();
		
		label = new CharDataLabel();
		label.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		label.setDataFont(font);
		
		mainPanel = new SectionListPanel();
		mainPanel.addSectionPanelListener(label);
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
		
		selectorPanel.addSelectorPanelListener(new SelectorPanelListener() {
			@Override
			public void fontFamilyChanged(SelectorPanel sp, Font font, SectionBuilder builder) {
				mainPanel.clearSections();
				mainPanel.setFont(font);
				mainPanel.setSections(builder.build(font));
				label.setDataFont(font);
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
