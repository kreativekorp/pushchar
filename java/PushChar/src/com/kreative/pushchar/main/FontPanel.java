package com.kreative.pushchar.main;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class FontPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private final JComboBox fontNameList;
	private final JCheckBox boldCheckbox;
	private final JCheckBox italicCheckbox;
	private final SpinnerNumberModel fontSizeModel;
	
	public FontPanel() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		fontNameList = new JComboBox(ge.getAvailableFontFamilyNames());
		fontNameList.setEditable(false);
		boldCheckbox = new JCheckBox("Bold");
		italicCheckbox = new JCheckBox("Italic");
		fontSizeModel = new SpinnerNumberModel(12, 4, 288, 1);
		
		JPanel stylePanel = new JPanel(new GridLayout(1, 0, 8, 8));
		stylePanel.add(boldCheckbox);
		stylePanel.add(italicCheckbox);
		stylePanel.add(new JSpinner(fontSizeModel));
		
		setLayout(new BorderLayout(8, 8));
		add(fontNameList, BorderLayout.CENTER);
		add(stylePanel, BorderLayout.PAGE_END);
	}
	
	public Font getSelectedFont() {
		String fontName = fontNameList.getSelectedItem().toString();
		int fontStyle = (boldCheckbox.isSelected() ? Font.BOLD : 0);
		fontStyle |= (italicCheckbox.isSelected() ? Font.ITALIC : 0);
		int fontSize = fontSizeModel.getNumber().intValue();
		return new Font(fontName, fontStyle, fontSize);
	}
	
	public void setSelectedFont(Font font) {
		fontNameList.setSelectedItem(font.getFamily());
		boldCheckbox.setSelected((font.getStyle() & Font.BOLD) != 0);
		italicCheckbox.setSelected((font.getStyle() & Font.ITALIC) != 0);
		fontSizeModel.setValue(font.getSize());
	}
}
