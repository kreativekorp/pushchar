package com.kreative.pushchar.main;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class SelectorPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final JList fontNameList;
	private final JScrollPane fontNamePane;
	private final JLabel fontNameLabel;
	private final JToggleButton fontNameButton;
	private final SpinnerNumberModel fontSizeModel;
	private final JSpinner fontSizeSpinner;
	private final JToggleButton fontBoldButton;
	private final JToggleButton fontItalicButton;
	private final JList sectionBuilderList;
	private final JScrollPane sectionBuilderPane;
	private final JLabel sectionBuilderLabel;
	private final JToggleButton sectionBuilderButton;
	private final List<SelectorPanelListener> listeners;
	private boolean settingFont;
	private boolean settingBuilder;
	private SectionBuilder selectedBuilder;
	
	public SelectorPanel() {
		fontNameList = new JList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
		fontNamePane = new JScrollPane(fontNameList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		fontNameLabel = new JLabel();
		fontNameButton = new JToggleButton(" A ");
		fontNameButton.setToolTipText("Show Font List");
		fontNameButton.setFont(fontNameButton.getFont().deriveFont(Font.ITALIC));
		fontNameButton.putClientProperty("JButton.buttonType", "square");
		fontSizeModel = new SpinnerNumberModel(12, 4, 288, 1);
		fontSizeSpinner = new JSpinner(fontSizeModel);
		fontSizeSpinner.setToolTipText("Font Size");
		fontBoldButton = new JToggleButton(" B ");
		fontBoldButton.setToolTipText("Bold");
		fontBoldButton.setFont(fontBoldButton.getFont().deriveFont(Font.BOLD));
		fontBoldButton.putClientProperty("JButton.buttonType", "square");
		fontItalicButton = new JToggleButton(" i ");
		fontItalicButton.setToolTipText("Italic");
		fontItalicButton.setFont(fontItalicButton.getFont().deriveFont(Font.ITALIC));
		fontItalicButton.putClientProperty("JButton.buttonType", "square");
		sectionBuilderList = new JList(SectionBuilderFactory.getInstance().createBuilders().toArray());
		sectionBuilderPane = new JScrollPane(sectionBuilderList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sectionBuilderLabel = new JLabel();
		sectionBuilderButton = new JToggleButton(" E ");
		sectionBuilderButton.setToolTipText("Show Encoding List");
		sectionBuilderButton.putClientProperty("JButton.buttonType", "square");
		listeners = new ArrayList<SelectorPanelListener>();
		settingFont = false;
		settingBuilder = false;
		selectedBuilder = null;
		
		JPanel fontStylePanel = new JPanel(new GridLayout(1,0,-1,-1));
		fontStylePanel.add(fontBoldButton);
		fontStylePanel.add(fontItalicButton);
		fontStylePanel.add(sectionBuilderButton);
		
		JPanel fontSizeStylePanel = new JPanel(new BorderLayout(12,12));
		fontSizeStylePanel.add(fontSizeSpinner, BorderLayout.CENTER);
		fontSizeStylePanel.add(fontStylePanel, BorderLayout.LINE_END);
		
		JPanel fontNamePanel = new JPanel(new BorderLayout(8,8));
		fontNamePanel.add(fontNameLabel, BorderLayout.CENTER);
		fontNamePanel.add(fontNameButton, BorderLayout.LINE_START);
		
		setLayout(new BorderLayout(12,12));
		add(fontNamePanel, BorderLayout.CENTER);
		add(fontSizeStylePanel, BorderLayout.LINE_END);
		setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
		
		fontNameList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				String oldValue = fontNameLabel.getText();
				String newValue = fontNameList.getSelectedValue().toString();
				if (oldValue.equals(newValue)) return;
				fontNameLabel.setText(newValue);
				if (settingFont) return;
				fontFamilyChanged();
			}
		});
		fontNameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fontNamePane.setVisible(fontNameButton.isSelected());
				revalidate();
			}
		});
		fontSizeModel.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (settingFont) return;
				fontStyleChanged();
			}
		});
		fontBoldButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (settingFont) return;
				fontStyleChanged();
			}
		});
		fontItalicButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (settingFont) return;
				fontStyleChanged();
			}
		});
		sectionBuilderList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				SectionBuilder oldValue = selectedBuilder;
				SectionBuilder newValue = (SectionBuilder)sectionBuilderList.getSelectedValue();
				if (oldValue == newValue) return;
				sectionBuilderLabel.setText(newValue.getName());
				selectedBuilder = newValue;
				if (settingBuilder) return;
				sectionBuilderChanged();
			}
		});
		sectionBuilderButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sectionBuilderPane.setVisible(sectionBuilderButton.isSelected());
				revalidate();
			}
		});
	}
	
	public JComponent getFontNamePane() {
		return fontNamePane;
	}
	
	public JComponent getSectionBuilderPane() {
		return sectionBuilderPane;
	}
	
	public Font getSelectedFont() {
		String fontName = fontNameLabel.getText();
		int fontSize = fontSizeModel.getNumber().intValue();
		int fontStyle = 0;
		if (fontBoldButton.isSelected()) fontStyle |= Font.BOLD;
		if (fontItalicButton.isSelected()) fontStyle |= Font.ITALIC;
		return new Font(fontName, fontStyle, fontSize);
	}
	
	public void setSelectedFont(Font font) {
		settingFont = true;
		fontNameList.setSelectedValue(font.getName(), true);
		fontNameLabel.setText(font.getName());
		fontNameButton.setSelected(fontNamePane.isVisible());
		fontSizeSpinner.setValue(font.getSize());
		fontBoldButton.setSelected((font.getStyle() & Font.BOLD) != 0);
		fontItalicButton.setSelected((font.getStyle() & Font.ITALIC) != 0);
		settingFont = false;
	}
	
	public SectionBuilder getSelectedBuilder() {
		return selectedBuilder;
	}
	
	public void setSelectedBuilder(SectionBuilder builder) {
		settingBuilder = true;
		sectionBuilderList.setSelectedValue(builder, true);
		sectionBuilderLabel.setText(builder.getName());
		sectionBuilderButton.setSelected(sectionBuilderPane.isVisible());
		selectedBuilder = builder;
		settingBuilder = false;
	}
	
	public void addSelectorPanelListener(SelectorPanelListener l) {
		listeners.add(l);
	}
	
	public void removeSelectorPanelListener(SelectorPanelListener l) {
		listeners.remove(l);
	}
	
	private void fontFamilyChanged() {
		Font font = getSelectedFont();
		SectionBuilder builder = getSelectedBuilder();
		for (SelectorPanelListener l : listeners) l.fontFamilyChanged(this, font, builder);
	}
	
	private void fontStyleChanged() {
		Font font = getSelectedFont();
		SectionBuilder builder = getSelectedBuilder();
		for (SelectorPanelListener l : listeners) l.fontStyleChanged(this, font, builder);
	}
	
	private void sectionBuilderChanged() {
		Font font = getSelectedFont();
		SectionBuilder builder = getSelectedBuilder();
		for (SelectorPanelListener l : listeners) l.sectionBuilderChanged(this, font, builder);
	}
}
