package com.kreative.pushchar.test;

import java.awt.Font;
import java.awt.font.OpenType;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import com.kreative.pushchar.ttflib.FindOpenType;
import com.kreative.pushchar.ttflib.PuaaEntry;
import com.kreative.pushchar.ttflib.PuaaTable;
import com.kreative.pushchar.ttflib.TtfFile;
import com.kreative.pushchar.unilib.BlockList;
import com.kreative.pushchar.unilib.PropertyMap;

public class UcdLookup {
	public static void main(String[] args) {
		if (args.length == 0) {
			printHelp();
			return;
		}
		
		BlockListReader bl = new BlockListReader();
		PropertyMapReader pm = new PropertyMapReader();
		List<String> properties = new ArrayList<String>();
		List<Integer> codePoints = new ArrayList<Integer>();
		boolean parsingOptions = true;
		
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (parsingOptions && arg.startsWith("-")) {
				if (arg.equals("--")) {
					parsingOptions = false;
				} else if (arg.equals("-d") && argi < args.length) {
					addFromUcd(bl, pm, args[argi++]);
				} else if (arg.equals("-f") && argi < args.length) {
					PuaaTable t = readFromFont(args[argi++]);
					if (t != null) addFromPuaa(bl, pm, t);
				} else if (arg.equals("-i") && argi < args.length) {
					PuaaTable t = readFromFile(args[argi++]);
					if (t != null) addFromPuaa(bl, pm, t);
				} else if (arg.equals("-p") && argi < args.length) {
					properties.add(args[argi++]);
				} else if (arg.equals("-c") && argi < args.length) {
					codePoints.add(parseCP(args[argi++]));
				} else if (arg.equals("--help")) {
					printHelp();
				} else {
					System.out.println("Unknown option: " + arg);
				}
			} else {
				codePoints.add(parseCP(arg));
			}
		}
		
		printChars(bl, pm, properties, codePoints);
	}
	
	private static void addFromUcd(BlockListReader bl, PropertyMapReader pm, String arg) {
		try {
			File file = new File(arg);
			String name = file.getName().toLowerCase().replaceAll("\\.txt$", "");
			if (name.equals("blocks")) {
				bl.read(file);
			} else {
				pm.readUnidata(file);
			}
		} catch (Exception e) {
			System.out.println("Error: " + e);
		}
	}
	
	private static PuaaTable readFromFont(String arg) {
		try {
			Font font = new Font(arg, 0, 1);
			OpenType ot = FindOpenType.forFont(font);
			if (ot != null) {
				byte[] d = ot.getFontTable("PUAA");
				if (d != null) return new PuaaTable(d);
				System.out.println("Error: Table not found.");
				return null;
			}
			System.out.println("Error: Not an OpenType font.");
			return null;
		} catch (Exception e) {
			System.out.println("Error: " + e);
			return null;
		}
	}
	
	private static PuaaTable readFromFile(String arg) {
		try {
			File file = new File(arg);
			TtfFile ttf = new TtfFile(file);
			PuaaTable t = ttf.getTableAs(PuaaTable.class, "PUAA");
			if (t != null) return t;
			System.out.println("Error: Table not found.");
			return null;
		} catch (Exception e) {
			System.out.println("Error: " + e);
			return null;
		}
	}
	
	private static void addFromPuaa(BlockList bl, PropertyMap pm, PuaaTable t) {
		for (String prop : t.getProperties()) {
			if (prop.equals("Block")) {
				for (PuaaEntry e : t.getPropertyEntries(prop)) {
					int fcp = e.getFirstCodePoint();
					int lcp = e.getLastCodePoint();
					String name = e.getPropertyString(fcp);
					bl.put(fcp, lcp, name);
				}
			} else {
				pm.putAll(prop, t.getPropertyMap(prop));
			}
		}
	}
	
	private static void printChars(BlockList bl, PropertyMap pm, List<String> p, List<Integer> c) {
		if (c == null || c.isEmpty()) {
			printProps(bl, pm, p);
			return;
		}
		
		Set<String> props = new TreeSet<String>();
		props.add("Block");
		props.addAll(pm.properties());
		
		int nameWidth = 0;
		for (String prop : props) {
			if (prop.length() > nameWidth) {
				nameWidth = prop.length();
			}
		}
		nameWidth += 4;
		
		for (int cp : c) {
			System.out.println("U+" + toHexString(cp) + ":");
			for (String prop : props) {
				if (p.isEmpty() || p.contains(prop)) {
					String value = ("Block".equals(prop)) ? bl.get(cp).blockName : pm.get(cp, prop);
					if (value != null) {
						StringBuffer sb = new StringBuffer("  ");
						sb.append(prop);
						sb.append(":");
						while (sb.length() < nameWidth) sb.append(" ");
						sb.append(value);
						System.out.println(sb.toString());
					}
				}
			}
		}
	}
	
	private static void printProps(BlockList bl, PropertyMap pm, List<String> p) {
		if (p == null || p.isEmpty()) {
			printTOC(bl, pm);
			return;
		}
		
		Set<String> props = new TreeSet<String>();
		props.add("Block");
		props.addAll(pm.properties());
		
		for (String prop : props) {
			if (p.contains(prop)) {
				System.out.println(prop + ":");
				if ("Block".equals(prop)) {
					for (BlockList.Entry e : bl) {
						StringBuffer sb = new StringBuffer("  ");
						sb.append(toHexString(e.firstCodePoint));
						if (e.firstCodePoint != e.lastCodePoint) {
							sb.append("..");
							sb.append(toHexString(e.lastCodePoint));
						}
						sb.append(":");
						while (sb.length() < 18) sb.append(" ");
						sb.append(e.blockName);
						System.out.println(sb.toString());
					}
				} else {
					for (Map.Entry<Integer,String> e : pm.get(prop).entrySet()) {
						StringBuffer sb = new StringBuffer("  ");
						sb.append(toHexString(e.getKey()));
						sb.append(":");
						while (sb.length() < 18) sb.append(" ");
						sb.append(e.getValue());
						System.out.println(sb.toString());
					}
				}
			}
		}
	}
	
	private static void printTOC(BlockList bl, PropertyMap pm) {
		Set<String> props = new TreeSet<String>();
		props.add("Block");
		props.addAll(pm.properties());
		
		System.out.println("Properties:");
		for (String prop : props) {
			System.out.println("  " + prop);
		}
	}
	
	private static String toHexString(int v) {
		String s = Integer.toHexString(v).toUpperCase();
		if (s.length() < 4) s = ("0000" + s).substring(s.length());
		return s;
	}
	
	private static int parseCP(String s) {
		s = s.replaceAll("[Uu][+]|[0][Xx]|\\s+", "");
		return Integer.parseInt(s, 16);
	}
	
	private static void printHelp() {
		System.out.println();
		System.out.println("UcdLookup - Look up Unicode Character Database properties.");
		System.out.println();
		System.out.println("  -d <path>     Specify Unicode Character Database file or directory.");
		System.out.println("  -f <name>     Specify source TrueType font name.");
		System.out.println("  -i <path>     Specify source TrueType file.");
		System.out.println("  -p <prop>     Specify properties to look up.");
		System.out.println("  -c <cp>       Specify code points to look up.");
		System.out.println("  --            Process remaining arguments as code points.");
		System.out.println();
	}
}
