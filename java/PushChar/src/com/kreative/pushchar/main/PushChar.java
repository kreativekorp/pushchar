package com.kreative.pushchar.main;

import java.awt.Font;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class PushChar {
	private static enum Mode { AUTO, INFO, GUI, ERROR; }
	
	public static void main(String[] args) {
		Mode mode = Mode.AUTO;
		String fontName = "SansSerif";
		int fontStyle = Font.PLAIN;
		int fontSize = 12;
		
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (arg.equalsIgnoreCase("-version") || arg.equalsIgnoreCase("--version")) {
				System.out.println("PushChar 2.0");
				System.out.println("(c) 2012-2021 Kreative Software");
				if (mode == Mode.AUTO) mode = Mode.INFO;
			} else if (arg.equalsIgnoreCase("-help") || arg.equalsIgnoreCase("--help")) {
				System.out.println("java -jar pushchar.jar [ -f <fontname> ] [ -b | -i | -bi ] [ -s <fontsize> ]");
				if (mode == Mode.AUTO) mode = Mode.INFO;
			} else if (arg.equalsIgnoreCase("-f") || arg.equalsIgnoreCase("-font") || arg.equalsIgnoreCase("--font")) {
				if (argi < args.length) {
					fontName = args[argi++];
					if (mode == Mode.AUTO || mode == Mode.INFO) mode = Mode.GUI;
				} else {
					System.err.println("Missing parameter for option " + arg + ".");
					mode = Mode.ERROR;
				}
			} else if (arg.equalsIgnoreCase("-b") || arg.equalsIgnoreCase("-bold") || arg.equalsIgnoreCase("--bold")) {
				fontStyle |= Font.BOLD;
				if (mode == Mode.AUTO || mode == Mode.INFO) mode = Mode.GUI;
			} else if (arg.equalsIgnoreCase("-i") || arg.equalsIgnoreCase("-italic") || arg.equalsIgnoreCase("--italic")) {
				fontStyle |= Font.ITALIC;
				if (mode == Mode.AUTO || mode == Mode.INFO) mode = Mode.GUI;
			} else if (arg.equalsIgnoreCase("-bi") || arg.equalsIgnoreCase("-bolditalic") || arg.equalsIgnoreCase("--bolditalic")) {
				fontStyle |= Font.BOLD | Font.ITALIC;
				if (mode == Mode.AUTO || mode == Mode.INFO) mode = Mode.GUI;
			} else if (arg.equalsIgnoreCase("-s") || arg.equalsIgnoreCase("-size") || arg.equalsIgnoreCase("--size")) {
				if (argi < args.length) {
					fontSize = Integer.parseInt(args[argi++]);
					if (mode == Mode.AUTO || mode == Mode.INFO) mode = Mode.GUI;
				} else {
					System.err.println("Missing parameter for option " + arg + ".");
					mode = Mode.ERROR;
				}
			} else {
				System.err.println("Unrecognized option " + arg + ".");
				mode = Mode.ERROR;
			}
		}
		
		if (mode == Mode.AUTO || mode == Mode.GUI) {
			final String ffName = fontName;
			final int ffStyle = fontStyle;
			final int ffSize = fontSize;
			
			try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					Font font = new Font(ffName, ffStyle, ffSize);
					PushCharFrame f = new PushCharFrame(font);
					f.setDefaultCloseOperation(PushCharFrame.DISPOSE_ON_CLOSE);
					f.setVisible(true);
				}
			});
		}
	}
}
