package com.kreative.pushchar.main;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
	private static enum Mode { AUTO, INFO, GUI, ERROR; }
	
	public static void main(String[] args) {
		Mode mode = Mode.AUTO;
		String fontName = "SansSerif";
		int fontStyle = Font.PLAIN;
		int fontSize = 12;
		boolean showTriggerWindow = true;
		boolean showPushWindow = false;
		boolean showSearchWindow = false;
		
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
			} else if (arg.equalsIgnoreCase("-t") || arg.equalsIgnoreCase("-trigger") || arg.equalsIgnoreCase("--trigger")) {
				showTriggerWindow = true;
				if (mode == Mode.AUTO || mode == Mode.INFO) mode = Mode.GUI;
			} else if (arg.equalsIgnoreCase("-l") || arg.equalsIgnoreCase("-notrigger") || arg.equalsIgnoreCase("--notrigger")) {
				showTriggerWindow = false;
				if (mode == Mode.AUTO || mode == Mode.INFO) mode = Mode.GUI;
			} else if (arg.equalsIgnoreCase("-p") || arg.equalsIgnoreCase("-push") || arg.equalsIgnoreCase("--push")) {
				showPushWindow = true;
				if (mode == Mode.AUTO || mode == Mode.INFO) mode = Mode.GUI;
			} else if (arg.equalsIgnoreCase("-np") || arg.equalsIgnoreCase("-nopush") || arg.equalsIgnoreCase("--nopush")) {
				showPushWindow = false;
				if (mode == Mode.AUTO || mode == Mode.INFO) mode = Mode.GUI;
			} else if (arg.equalsIgnoreCase("-q") || arg.equalsIgnoreCase("-search") || arg.equalsIgnoreCase("--search")) {
				showSearchWindow = true;
				if (mode == Mode.AUTO || mode == Mode.INFO) mode = Mode.GUI;
			} else if (arg.equalsIgnoreCase("-nq") || arg.equalsIgnoreCase("-nosearch") || arg.equalsIgnoreCase("--nosearch")) {
				showSearchWindow = false;
				if (mode == Mode.AUTO || mode == Mode.INFO) mode = Mode.GUI;
			} else {
				System.err.println("Unrecognized option " + arg + ".");
				mode = Mode.ERROR;
			}
		}
		
		if (mode == Mode.AUTO || mode == Mode.GUI) {
			final String ffName = fontName;
			final int ffStyle = fontStyle;
			final int ffSize = fontSize;
			final boolean fsTrigger = showTriggerWindow;
			final boolean fsPush = showPushWindow;
			final boolean fsSearch = showSearchWindow;
			
			if (fsTrigger) try { System.setProperty("apple.awt.UIElement", "true"); } catch (Exception e) {}
			try { System.setProperty("com.apple.mrj.application.apple.menu.about.name", "PushChar"); } catch (Exception e) {}
			try { System.setProperty("apple.laf.useScreenMenuBar", "true"); } catch (Exception e) {}
			try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
			
			try {
				Method getModule = Class.class.getMethod("getModule");
				Object javaDesktop = getModule.invoke(Toolkit.getDefaultToolkit().getClass());
				Object allUnnamed = getModule.invoke(Main.class);
				Class<?> module = Class.forName("java.lang.Module");
				Method addOpens = module.getMethod("addOpens", String.class, module);
				addOpens.invoke(javaDesktop, "sun.awt.X11", allUnnamed);
			} catch (Exception e) {}
			
			try {
				Toolkit tk = Toolkit.getDefaultToolkit();
				Field aacn = tk.getClass().getDeclaredField("awtAppClassName");
				aacn.setAccessible(true);
				aacn.set(tk, "PushChar");
			} catch (Exception e) {}
			
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					Font font = new Font(ffName, ffStyle, ffSize);
					if (fsTrigger) {
						int[] pasteKeyStroke = new int[]{ CopyMenuItem.shortcutKey, KeyEvent.VK_V };
						TriggerWindow trigger = new TriggerWindow(
							TriggerWindow.Position.NORTHWEST,
							TriggerWindow.Orientation.WEST_EAST
						);
						
						PushCharFrame push = new PushCharFrame(font, true, pasteKeyStroke);
						trigger.setPushWindow(push);
						
						SearchCharFrame search = new SearchCharFrame(true, pasteKeyStroke);
						trigger.setSearchWindow(search);
						
						push.setVisible(fsPush);
						search.setVisible(fsSearch);
						trigger.setVisible(true);
					} else {
						if (fsPush || !fsSearch) {
							PushCharFrame push = new PushCharFrame(font, false, null);
							push.setDefaultCloseOperation(PushCharFrame.DISPOSE_ON_CLOSE);
							push.setVisible(true);
						}
						if (fsSearch) {
							SearchCharFrame search = new SearchCharFrame(false, null);
							search.setDefaultCloseOperation(SearchCharFrame.DISPOSE_ON_CLOSE);
							search.setVisible(true);
						}
					}
				}
			});
		}
	}
}
