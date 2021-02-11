package com.kreative.pushchar.test;

import java.awt.Font;
import java.util.List;
import com.kreative.pushchar.main.Section;
import com.kreative.pushchar.main.SectionBuilder;
import com.kreative.pushchar.main.SectionBuilderFactory;

public class SectionTest {
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
					Font font = new Font(args[1], 0, 1);
					List<Section> sections = sb.build(font);
					for (Section s : sections) {
						System.out.println("\u001B[44m\u001B[37m\u001B[2K" + s.getTitleWithCount() + "\u001B[0m");
						for (int y = 0, n = s.getRowCount(); y < n; y++) {
							for (int x = 0; x < Section.COLUMN_COUNT; x++) {
								String ch = s.getChar(y, x);
								if (ch == null) System.out.print("\u001B[48;5;102m        \u001B[0m");
								else if (ch.trim().length() > 0) System.out.print("   " + ch + "\t");
								else System.out.print("\t");
							}
							System.out.println();
						}
					}
					break;
				}
			}
		}
	}
}
