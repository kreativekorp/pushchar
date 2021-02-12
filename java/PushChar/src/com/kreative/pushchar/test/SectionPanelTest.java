package com.kreative.pushchar.test;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import com.kreative.pushchar.main.CharDataLabel;
import com.kreative.pushchar.main.Section;
import com.kreative.pushchar.main.SectionBuilder;
import com.kreative.pushchar.main.SectionBuilderFactory;
import com.kreative.pushchar.main.SectionListPanel;

public class SectionPanelTest {
	public static void main(String[] args) {
		SectionBuilderFactory sbf = SectionBuilderFactory.getInstance();
		List<SectionBuilder> builders = sbf.createBuilders();
		if (args.length < 2) {
			for (SectionBuilder sb : builders) {
				System.out.println(sb.toString());
			}
		} else {
			for (SectionBuilder sb : builders) {
				if (sb.getName().equalsIgnoreCase(args[0])) {
					Font font = new Font(args[1], 0, 24);
					List<Section> sections = sb.build(font);
					
					CharDataLabel l = new CharDataLabel();
					l.setDataFont(font);
					l.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
					
					SectionListPanel p = new SectionListPanel(sections, font);
					p.setAutoselects(true);
					p.addSectionPanelListener(l);
					
					JScrollPane s = new JScrollPane(
						p,
						JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
						JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
					);
					
					JPanel m = new JPanel(new BorderLayout());
					m.add(s, BorderLayout.CENTER);
					m.add(l, BorderLayout.PAGE_END);
					
					JFrame f = new JFrame(font.getName());
					f.setContentPane(m);
					f.setSize(500, 500);
					f.setLocationRelativeTo(null);
					f.setVisible(true);
					break;
				}
			}
		}
	}
}
