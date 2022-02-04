package com.kreative.glyphutils;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import com.kreative.acc.pushchar.CharInFont;

public class MakeGlyphDir {
	public static void main(String[] args) {
		try { System.setProperty("apple.awt.UIElement", "true"); } catch (Exception e) {}
		if (args.length == 0) {
			printHelp();
		} else try {
			Options o = parseOptions(args);
			Map<Integer,BufferedImage> map = getGlyphMap(o);
			if (map.isEmpty()) return;
			o.output.mkdirs();
			for (Map.Entry<Integer,BufferedImage> e : map.entrySet()) {
				String name = (o.decimal ? e.getKey() : hex(e.getKey())) + ".png";
				ImageIO.write(e.getValue(), "png", new File(o.output, name));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void printHelp() {
		System.out.println();
		System.out.println("Syntax:");
		System.out.println("  java MakeGlyphDir <options> <codepoint>[-<codepoint>] [...]");
		System.out.println();
		System.out.println("Options:");
		System.out.println("  -f,  -font,              --font               <name>");
		System.out.println("  -s,  -size,              --size               <num>");
		System.out.println("  -n,  -normal,            --normal");
		System.out.println("  -b,  -bold,              --bold");
		System.out.println("  -i,  -italic,            --italic");
		System.out.println("  -bi, -bolditalic,        --bold-italic");
		System.out.println("  -a,  -alias,             --alias");
		System.out.println("  -A,  -antialias,         --anti-alias");
		System.out.println("  -T,  -tc, -textcolor,    --text-color         <color>");
		System.out.println("  -G,  -gc, -glyphbg,      --glyphbg            <color>");
		System.out.println("            -glyphfg,      --glyphfg            <color>");
		System.out.println("  -p,  -padding,           --padding            <num>[,<num>[,<num>[,<num>]]]");
		System.out.println("  -pv, -paddingvertical,   --padding-vertical   <num>");
		System.out.println("  -ph, -paddinghorizontal, --padding-horizontal <num>");
		System.out.println("  -pt, -paddingtop,        --padding-top        <num>");
		System.out.println("  -pl, -paddingleft,       --padding-left       <num>");
		System.out.println("  -pb, -paddingbottom,     --padding-bottom     <num>");
		System.out.println("  -pr, -paddingright,      --padding-right      <num>");
		System.out.println("  -o,  -output,            --output             <path>");
		System.out.println();
	}
	
	private static boolean matchFlag(String arg, int argi, int argc, int params, String... matches) {
		if (argi + params <= argc) {
			for (String match : matches) {
				if (arg.equals(match)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private static int parseInt(String s) {
		try { return Integer.parseInt(s.trim(), 10); }
		catch (NumberFormatException e) { return 0; }
	}
	
	private static int parseHex(String s) {
		s = s.replaceAll("\\s|0[Xx]|[Uu][+]", "");
		try { return Integer.parseInt(s, 16); }
		catch (NumberFormatException e) { return 0; }
	}
	
	private static Color parseColor(String s) {
		s = s.replaceAll("\\s|#|0[Xx]", "");
		try {
			int v = Integer.parseInt(s, 16);
			switch (s.length()) {
				case 3: return new Color(((v>>8)&15)*17, ((v>>4)&15)*17, ((v>>0)&15)*17);
				case 4: return new Color(((v>>8)&15)*17, ((v>>4)&15)*17, ((v>>0)&15)*17, ((v>>12)&15)*17);
				case 6: return new Color((v>>16)&255, (v>>8)&255, (v>>0)&255);
				case 8: return new Color((v>>16)&255, (v>>8)&255, (v>>0)&255, (v>>24)&255);
				default: return null;
			}
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	public static Options parseOptions(String... args) {
		Options o = new Options();
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (arg.startsWith("-")) {
				if (matchFlag(arg, argi, args.length, 0, "-help", "--help")) {
					printHelp();
				} else if (matchFlag(arg, argi, args.length, 1, "-f", "-font", "--font")) {
					o.fontName = args[argi++];
				} else if (matchFlag(arg, argi, args.length, 1, "-s", "-size", "--size")) {
					o.fontSize = parseInt(args[argi++]);
					if (o.fontSize < 1) o.fontSize = 12;
				} else if (matchFlag(arg, argi, args.length, 0, "-n", "-normal", "--normal")) {
					o.fontStyle = 0;
				} else if (matchFlag(arg, argi, args.length, 0, "-b", "-bold", "--bold")) {
					o.fontStyle = Font.BOLD;
				} else if (matchFlag(arg, argi, args.length, 0, "-i", "-italic", "--italic")) {
					o.fontStyle = Font.ITALIC;
				} else if (matchFlag(arg, argi, args.length, 0, "-bi", "-bolditalic", "--bold-italic")) {
					o.fontStyle = Font.BOLD | Font.ITALIC;
				} else if (matchFlag(arg, argi, args.length, 0, "-a", "-alias", "--alias")) {
					o.antialias = false;
				} else if (matchFlag(arg, argi, args.length, 0, "-A", "-antialias", "--anti-alias")) {
					o.antialias = true;
				} else if (matchFlag(arg, argi, args.length, 1, "-T", "-tc", "-textcolor", "--text-color")) {
					o.glyphfg = parseColor(args[argi++]);
				} else if (matchFlag(arg, argi, args.length, 1, "-glyphfg", "--glyphfg")) {
					o.glyphfg = parseColor(args[argi++]);
				} else if (matchFlag(arg, argi, args.length, 1, "-G", "-gc", "-glyphbg", "--glyphbg")) {
					o.glyphbg = parseColor(args[argi++]);
				} else if (matchFlag(arg, argi, args.length, 1, "-p", "-padding", "--padding")) {
					String[] fields = args[argi++].split(",", 4);
					o.padding.top = o.padding.right = o.padding.bottom = o.padding.left = parseInt(fields[0]);
					if (fields.length > 1) o.padding.right = o.padding.left = parseInt(fields[1]);
					if (fields.length > 2) o.padding.bottom = parseInt(fields[2]);
					if (fields.length > 3) o.padding.left = parseInt(fields[3]);
				} else if (matchFlag(arg, argi, args.length, 1, "-pv", "-paddingvertical", "--padding-vertical")) {
					o.padding.top = o.padding.bottom = parseInt(args[argi++]);
				} else if (matchFlag(arg, argi, args.length, 1, "-ph", "-paddinghorizontal", "--padding-horizontal")) {
					o.padding.left = o.padding.right = parseInt(args[argi++]);
				} else if (matchFlag(arg, argi, args.length, 1, "-pt", "-paddingtop", "--padding-top")) {
					o.padding.top = parseInt(args[argi++]);
				} else if (matchFlag(arg, argi, args.length, 1, "-pl", "-paddingleft", "--padding-left")) {
					o.padding.left = parseInt(args[argi++]);
				} else if (matchFlag(arg, argi, args.length, 1, "-pb", "-paddingbottom", "--padding-bottom")) {
					o.padding.bottom = parseInt(args[argi++]);
				} else if (matchFlag(arg, argi, args.length, 1, "-pr", "-paddingright", "--padding-right")) {
					o.padding.right = parseInt(args[argi++]);
				} else if (matchFlag(arg, argi, args.length, 0, "-h", "-hex", "--hex")) {
					o.decimal = false;
				} else if (matchFlag(arg, argi, args.length, 0, "-d", "-dec", "--dec")) {
					o.decimal = true;
				} else if (matchFlag(arg, argi, args.length, 1, "-o", "-output", "--output")) {
					o.output = new File(args[argi++]);
				} else {
					System.err.println("Unknown option: " + arg);
				}
			} else {
				String[] fields = arg.split("-+|[.]+", 2);
				int start = parseHex(fields[0]);
				int end = (fields.length > 1) ? parseHex(fields[1]) : start;
				o.ranges.add(new Range(start, end));
			}
		}
		return o;
	}
	
	private static Graphics2D withFont(Graphics2D g, Font font, boolean aa) {
		if (aa) g.setRenderingHint(
			RenderingHints.KEY_TEXT_ANTIALIASING,
			RenderingHints.VALUE_TEXT_ANTIALIAS_ON
		);
		g.setFont(font);
		return g;
	}
	
	public static BufferedImage getGlyphImage(Font font, int cp, boolean aa, Color fg, Color bg, Insets p) {
		String s = new String(Character.toChars(cp));
		BufferedImage img = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = withFont(img.createGraphics(), font, aa);
		FontMetrics fm = g.getFontMetrics(font);
		int a = fm.getAscent();
		int h = a + fm.getDescent();
		int w = fm.stringWidth(s);
		g.dispose();
		if (p != null) {
			a += p.top;
			w += p.left + p.right;
			h += p.top + p.bottom;
		}
		if (w <= 0 || h <= 0) return null;
		img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		g = withFont(img.createGraphics(), font, aa);
		if (bg != null) { g.setColor(bg); g.fillRect(0, 0, w, h); }
		g.setColor((fg != null) ? fg : Color.black);
		g.drawString(s, ((p != null) ? p.left : 0), a);
		g.dispose();
		return img;
	}
	
	public static Map<Integer,BufferedImage> getGlyphMap(Options o) {
		Map<Integer,BufferedImage> map = new HashMap<Integer,BufferedImage>();
		Font font = new Font(o.fontName, o.fontStyle, o.fontSize);
		CharInFont cif = CharInFont.getInstance();
		for (Range r : o.ranges) {
			for (int cp = r.start; cp <= r.end; cp++) {
				if (!cif.isCharInFont(font.getFamily(), cp)) continue;
				BufferedImage img = getGlyphImage(
					font, cp, o.antialias,
					o.glyphfg, o.glyphbg,
					o.padding
				);
				if (img == null) continue;
				map.put(cp, img);
			}
		}
		return map;
	}
	
	public static String hex(int cp) {
		String h = Integer.toHexString(cp).toUpperCase();
		while (h.length() < 4) h = "0" + h;
		return h;
	}
	
	public static class Range {
		public final int start;
		public final int end;
		public Range(int start, int end) {
			this.start = start;
			this.end = end;
		}
	}
	
	public static class Options {
		public String fontName = "SansSerif";
		public int fontSize = 12;
		public int fontStyle = 0;
		public boolean antialias = true;
		public Color glyphfg = Color.black;
		public Color glyphbg = Color.white;
		public Insets padding = new Insets(1,1,1,1);
		public boolean decimal = false;
		public File output = new File("glyphs");
		public List<Range> ranges = new ArrayList<Range>();
	}
}
