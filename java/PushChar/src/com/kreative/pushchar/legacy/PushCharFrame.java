package com.kreative.pushchar.legacy;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class PushCharFrame extends JFrame {
	private static final long serialVersionUID = 1;
	
	private final FontSelectorPanel fontPanel;
	private final PushCharPanel mainPanel;
	private final JLabel footerLabel;
	
	public PushCharFrame(Font font) {
		super("PushChar");
		
		fontPanel = new FontSelectorPanel();
		mainPanel = new PushCharPanel();
		footerLabel = new JLabel(" ");
		footerLabel.setFont(footerLabel.getFont().deriveFont(11.0f));
		footerLabel.setBorder(BorderFactory.createEmptyBorder(0, 4, 2, 4));
		
		JScrollPane scrollPane = new JScrollPane(mainPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		
		JPanel main = new JPanel(new BorderLayout());
		main.add(fontPanel, BorderLayout.PAGE_START);
		main.add(fontPanel.getFontNameComponent(), BorderLayout.LINE_START);
		main.add(fontPanel.getEncodingComponent(), BorderLayout.LINE_END);
		main.add(scrollPane, BorderLayout.CENTER);
		main.add(footerLabel, BorderLayout.PAGE_END);
		setContentPane(main);
		
		fontPanel.getFontNameComponent().setVisible(false);
		fontPanel.getEncodingComponent().setVisible(false);
		fontPanel.setSelectedFont(font);
		fontPanel.setSelectedEncoding(null);
		fontPanel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainPanel.loading();
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						Font font = fontPanel.getSelectedFont();
						List<Integer> enc = fontPanel.getSelectedEncoding();
						if (enc == null) mainPanel.update(font, footerLabel);
						else mainPanel.update(enc, font, footerLabel);
					}
				});
			}
		});
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(300, 200));
		setSize(new Dimension(520, 560));
		setLocationRelativeTo(null);
		setVisible(true);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Font font = fontPanel.getSelectedFont();
				List<Integer> enc = fontPanel.getSelectedEncoding();
				if (enc == null) mainPanel.update(font, footerLabel);
				else mainPanel.update(enc, font, footerLabel);
			}
		});
	}
}
