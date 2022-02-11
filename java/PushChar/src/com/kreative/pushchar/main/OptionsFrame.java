package com.kreative.pushchar.main;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class OptionsFrame extends JFrame {
	private static final long serialVersionUID = 1;
	
	private static final Dimension LABEL_SPACING = new Dimension(8, 8);
	private static final Dimension SECTION_SPACING = new Dimension(20, 20);
	
	private final Options o;
	private final WindowManager wm;
	private final HotSpotPanel hsp;
	private final FontPanel fp;
	private final JButton okButton;
	private final JButton cancelButton;
	
	public OptionsFrame(Options options, WindowManager windowManager) {
		super("PushChar Options");
		this.o = options;
		this.wm = windowManager;
		this.hsp = new HotSpotPanel();
		this.fp = new FontPanel();
		this.okButton = new JButton("OK");
		this.cancelButton = new JButton("Cancel");
		
		hsp.setPositionAndOrientation(o);
		fp.setSelectedFont(o.getDefaultFont());
		
		JPanel hspPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		hspPanel.add(hsp);
		
		JPanel buttonPanel1 = new JPanel(new GridLayout(1, 0, 8, 8));
		buttonPanel1.add(okButton);
		buttonPanel1.add(cancelButton);
		
		JPanel buttonPanel2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		buttonPanel2.add(buttonPanel1);
		
		JLabel hsl = new JLabel("Hot Spots:");
		hsl.setAlignmentX(0.5f);
		hsl.setHorizontalAlignment(JLabel.CENTER);
		JLabel fl = new JLabel("Default Font:");
		fl.setAlignmentX(0.5f);
		fl.setHorizontalAlignment(JLabel.CENTER);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		mainPanel.add(hsl);
		mainPanel.add(Box.createRigidArea(LABEL_SPACING));
		mainPanel.add(hspPanel);
		mainPanel.add(Box.createRigidArea(SECTION_SPACING));
		mainPanel.add(fl);
		mainPanel.add(Box.createRigidArea(LABEL_SPACING));
		mainPanel.add(fp);
		mainPanel.add(Box.createRigidArea(SECTION_SPACING));
		mainPanel.add(buttonPanel2);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		setContentPane(mainPanel);
		getRootPane().setDefaultButton(okButton);
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				hsp.getPositionAndOrientation(o);
				o.setDefaultFont(fp.getSelectedFont());
				try { o.write(); }
				catch (Exception ex) {}
				wm.createTriggers(o);
				wm.setTriggersVisible(true);
				setVisible(false);
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				hsp.setPositionAndOrientation(o);
				fp.setSelectedFont(o.getDefaultFont());
				setVisible(false);
			}
		});
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				hsp.setPositionAndOrientation(o);
				fp.setSelectedFont(o.getDefaultFont());
				setVisible(false);
			}
		});
	}
}
