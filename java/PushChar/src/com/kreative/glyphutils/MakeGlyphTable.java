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
import java.util.List;
import javax.imageio.ImageIO;
import com.kreative.acc.pushchar.CharInFont;

public class MakeGlyphTable {
	public static void main(String[] args) {
		try { System.setProperty("apple.awt.UIElement", "true"); } catch (Exception e) {}
		if (args.length == 0) {
			printHelp();
		} else try {
			Options o = parseOptions(args);
			GlyphImageTable table = getGlyphTable(o);
			BufferedImage img = renderGlyphTable(table);
			ImageIO.write(img, "png", o.output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void printHelp() {
		System.out.println();
		System.out.println("Syntax:");
		System.out.println("  java MakeGlyphTable <options> <codepoint>[-<codepoint>] [...]");
		System.out.println();
		System.out.println("Options:");
		System.out.println("  -f,  -font,              --font               <name>");
		System.out.println("  -s,  -size,              --size               <num>");
		System.out.println("  -n,  -normal,            --normal");
		System.out.println("  -b,  -bold,              --bold");
		System.out.println("  -i,  -italic,            --italic");
		System.out.println("  -bi, -bolditalic,        --bold-italic");
		System.out.println("  -E,  -equal,             --equal");
		System.out.println("  -F,  -fit,               --fit");
		System.out.println("  -a,  -alias,             --alias");
		System.out.println("  -A,  -antialias,         --anti-alias");
		System.out.println("  -T,  -tc, -textcolor,    --text-color         <color>");
		System.out.println("  -G,  -gc, -glyphbg,      --glyphbg            <color>");
		System.out.println("            -glyphfg,      --glyphfg            <color>");
		System.out.println("  -H,  -hc, -hexbg,        --hexbg              <color>");
		System.out.println("            -hexfg,        --hexfg              <color>");
		System.out.println("  -B,  -bc, -bordercolor,  --border-color       <color>");
		System.out.println("  -W,  -bw, -borderwidth,  --border-width       <num>");
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
				} else if (matchFlag(arg, argi, args.length, 0, "-E", "-equal", "--equal")) {
					o.fit = false;
				} else if (matchFlag(arg, argi, args.length, 0, "-F", "-fit", "--fit")) {
					o.fit = true;
				} else if (matchFlag(arg, argi, args.length, 0, "-a", "-alias", "--alias")) {
					o.antialias = false;
				} else if (matchFlag(arg, argi, args.length, 0, "-A", "-antialias", "--anti-alias")) {
					o.antialias = true;
				} else if (matchFlag(arg, argi, args.length, 1, "-T", "-tc", "-textcolor", "--text-color")) {
					o.glyphfg = o.hexfg = parseColor(args[argi++]);
				} else if (matchFlag(arg, argi, args.length, 1, "-glyphfg", "--glyphfg")) {
					o.glyphfg = parseColor(args[argi++]);
				} else if (matchFlag(arg, argi, args.length, 1, "-G", "-gc", "-glyphbg", "--glyphbg")) {
					o.glyphbg = parseColor(args[argi++]);
				} else if (matchFlag(arg, argi, args.length, 1, "-hexfg", "--hexfg")) {
					o.hexfg = parseColor(args[argi++]);
				} else if (matchFlag(arg, argi, args.length, 1, "-H", "-hc", "-hexbg", "--hexbg")) {
					o.hexbg = parseColor(args[argi++]);
				} else if (matchFlag(arg, argi, args.length, 1, "-B", "-bc", "-bordercolor", "--border-color")) {
					o.borderColor = parseColor(args[argi++]);
				} else if (matchFlag(arg, argi, args.length, 1, "-W", "-bw", "-borderwidth", "--border-width")) {
					o.borderWidth = Math.max(0, parseInt(args[argi++]));
				} else if (matchFlag(arg, argi, args.length, 1, "-p", "-padding", "--padding")) {
					String[] fields = args[argi++].split(",", 4);
					o.padding.top = o.padding.right = o.padding.bottom = o.padding.left = Math.max(0, parseInt(fields[0]));
					if (fields.length > 1) o.padding.right = o.padding.left = Math.max(0, parseInt(fields[1]));
					if (fields.length > 2) o.padding.bottom = Math.max(0, parseInt(fields[2]));
					if (fields.length > 3) o.padding.left = Math.max(0, parseInt(fields[3]));
				} else if (matchFlag(arg, argi, args.length, 1, "-pv", "-paddingvertical", "--padding-vertical")) {
					o.padding.top = o.padding.bottom = Math.max(0, parseInt(args[argi++]));
				} else if (matchFlag(arg, argi, args.length, 1, "-ph", "-paddinghorizontal", "--padding-horizontal")) {
					o.padding.left = o.padding.right = Math.max(0, parseInt(args[argi++]));
				} else if (matchFlag(arg, argi, args.length, 1, "-pt", "-paddingtop", "--padding-top")) {
					o.padding.top = Math.max(0, parseInt(args[argi++]));
				} else if (matchFlag(arg, argi, args.length, 1, "-pl", "-paddingleft", "--padding-left")) {
					o.padding.left = Math.max(0, parseInt(args[argi++]));
				} else if (matchFlag(arg, argi, args.length, 1, "-pb", "-paddingbottom", "--padding-bottom")) {
					o.padding.bottom = Math.max(0, parseInt(args[argi++]));
				} else if (matchFlag(arg, argi, args.length, 1, "-pr", "-paddingright", "--padding-right")) {
					o.padding.right = Math.max(0, parseInt(args[argi++]));
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
	
	public static BufferedImage getGlyphImage(Font font, int cp, boolean aa, Color fg, Color bg) {
		String s = new String(Character.toChars(cp));
		BufferedImage img = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = withFont(img.createGraphics(), font, aa);
		FontMetrics fm = g.getFontMetrics(font);
		int a = fm.getAscent();
		int h = a + fm.getDescent();
		int w = fm.stringWidth(s);
		g.dispose();
		if (w <= 0 || h <= 0) return null;
		img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		g = withFont(img.createGraphics(), font, aa);
		if (bg != null) { g.setColor(bg); g.fillRect(0, 0, w, h); }
		g.setColor((fg != null) ? fg : Color.black);
		g.drawString(s, 0, a);
		g.dispose();
		return img;
	}
	
	public static BufferedImageArray getHexArray(Font font, boolean aa, Color fg, Color bg) {
		BufferedImage[] images = new BufferedImage[16];
		CharInFont cif = CharInFont.getInstance();
		for (int i = 0; i < 16; i++) {
			int cp = (i < 10) ? ('0' + i) : ('A' + i - 10);
			if (!cif.isCharInFont(font.getFamily(), cp)) continue;
			images[i] = getGlyphImage(font, cp, aa, fg, bg);
		}
		return new BufferedImageArray(images);
	}
	
	public static GlyphImageArray getGlyphArray(Font font, int row, boolean aa, Color fg, Color bg) {
		BufferedImage[] images = new BufferedImage[16];
		CharInFont cif = CharInFont.getInstance();
		for (int i = 0; i < 16; i++) {
			int cp = ((row &~ 0xF) | i);
			if (!cif.isCharInFont(font.getFamily(), cp)) continue;
			images[i] = getGlyphImage(font, cp, aa, fg, bg);
		}
		return new GlyphImageArray(row, images);
	}
	
	public static GlyphImageMatrix getGlyphMatrix(Font font, int start, int end, boolean fit, boolean aa, Color fg, Color bg) {
		List<GlyphImageArray> arrays = new ArrayList<GlyphImageArray>();
		for (int i = (start >> 4), m = (end >> 4); i <= m; i++) {
			arrays.add(getGlyphArray(font, (i << 4), aa, fg, bg));
		}
		return new GlyphImageMatrix(
			start, end, fit,
			arrays.toArray(new GlyphImageArray[arrays.size()])
		);
	}
	
	public static GlyphImageTable getGlyphTable(Options o) {
		List<GlyphImageMatrix> matrices = new ArrayList<GlyphImageMatrix>();
		Font font = new Font(o.fontName, o.fontStyle, o.fontSize);
		for (Range r : o.ranges) {
			matrices.add(getGlyphMatrix(font, r.start, r.end, o.fit, o.antialias, o.glyphfg, null));
		}
		return new GlyphImageTable(
			o, getHexArray(font, o.antialias, o.hexfg, null),
			matrices.toArray(new GlyphImageMatrix[matrices.size()])
		);
	}
	
	public static BufferedImage renderGlyphTable(GlyphImageTable table) {
		int hexDigits = Math.max(4, Integer.toHexString(table.maxEnd).length());
		int headerHeight = (
			table.hexArray.maxHeight * (hexDigits - 1) +
			table.options.padding.top +
			table.options.padding.bottom +
			table.options.borderWidth
		);
		int rowHeight = (
			table.hexArray.maxHeight +
			table.options.padding.top +
			table.options.padding.bottom +
			table.options.borderWidth
		);
		int height = (
			headerHeight +
			rowHeight * 16 +
			table.options.borderWidth
		);
		int headerWidth = (
			table.hexArray.maxWidth +
			table.options.padding.left +
			table.options.padding.right +
			table.options.borderWidth
		);
		int insetWidth = (
			table.options.padding.left +
			table.options.padding.right +
			table.options.borderWidth
		);
		int width = table.options.borderWidth;
		for (GlyphImageMatrix matrix : table.matrices) {
			width += headerWidth;
			for (BufferedImageArray array : matrix.arrays) {
				int columnWidth = Math.max(
					(matrix.fit ? array.maxWidth : matrix.maxWidth),
					table.hexArray.maxWidth
				);
				width += columnWidth + insetWidth;
			}
		}
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Font font = new Font(table.options.fontName, table.options.fontStyle, table.options.fontSize);
		Graphics2D g = withFont(img.createGraphics(), font, table.options.antialias);
		int a = g.getFontMetrics(font).getAscent();
		// Paint background.
		if (table.options.hexbg != null) {
			g.setColor(table.options.hexbg);
			g.fillRect(0, 0, width, headerHeight);
		}
		int x = 0;
		for (GlyphImageMatrix matrix : table.matrices) {
			if (table.options.hexbg != null) {
				g.setColor(table.options.hexbg);
				g.fillRect(x, headerHeight, headerWidth, height - headerHeight);
			}
			x += headerWidth;
			for (BufferedImageArray array : matrix.arrays) {
				int columnWidth = Math.max(
					(matrix.fit ? array.maxWidth : matrix.maxWidth),
					table.hexArray.maxWidth
				);
				if (table.options.glyphbg != null) {
					g.setColor(table.options.glyphbg);
					g.fillRect(x, headerHeight, columnWidth + insetWidth, height - headerHeight);
				}
				x += columnWidth + insetWidth;
			}
		}
		// Paint text.
		x = 0;
		for (GlyphImageMatrix matrix : table.matrices) {
			int hx = x + table.options.borderWidth + table.options.padding.left;
			int hy = headerHeight + table.options.borderWidth + table.options.padding.top;
			g.setColor((table.options.hexfg != null) ? table.options.hexfg : Color.black);
			for (int i = 0; i < 16; i++) {
				int cp = (i < 10) ? ('0' + i) : ('A' + i - 10);
				BufferedImage glyph = table.hexArray.images[i];
				if (glyph != null) {
					g.drawString(
						new String(Character.toChars(cp)),
						hx + (table.hexArray.maxWidth - glyph.getWidth()) / 2,
						hy + (table.hexArray.maxHeight - glyph.getHeight()) / 2 + a
					);
				}
				hy += rowHeight;
			}
			x += headerWidth;
			for (BufferedImageArray array : matrix.arrays) {
				int columnWidth = Math.max(
					(matrix.fit ? array.maxWidth : matrix.maxWidth),
					table.hexArray.maxWidth
				);
				int cx = x + table.options.borderWidth + table.options.padding.left;
				if (array instanceof GlyphImageArray) {
					int row = ((GlyphImageArray)array).row;
					int hd = Math.max(4, Integer.toHexString(row).length());
					int cy = headerHeight - table.options.padding.bottom;
					g.setColor((table.options.hexfg != null) ? table.options.hexfg : Color.black);
					while (--hd > 0) {
						cy -= table.hexArray.maxHeight;
						int i = (row >>= 4) & 15;
						int cp = (i < 10) ? ('0' + i) : ('A' + i - 10);
						BufferedImage glyph = table.hexArray.images[i];
						if (glyph != null) {
							g.drawString(
								new String(Character.toChars(cp)),
								cx + (columnWidth - glyph.getWidth()) / 2,
								cy + (table.hexArray.maxHeight - glyph.getHeight()) / 2 + a
							);
						}
					}
					row = ((GlyphImageArray)array).row;
					cy = headerHeight + table.options.borderWidth + table.options.padding.top;
					g.setColor((table.options.glyphfg != null) ? table.options.glyphfg : Color.black);
					for (int i = 0; i < 16; i++) {
						int cp = ((row &~ 0xF) | i);
						BufferedImage glyph = array.images[i];
						if (glyph != null) {
							g.drawString(
								new String(Character.toChars(cp)),
								cx + (columnWidth - glyph.getWidth()) / 2,
								cy + (table.hexArray.maxHeight - glyph.getHeight()) / 2 + a
							);
						}
						cy += rowHeight;
					}
				} else {
					int cy = headerHeight + table.options.borderWidth + table.options.padding.top;
					for (BufferedImage glyph : array.images) {
						if (glyph != null) {
							g.drawImage(
								glyph, null,
								cx + (columnWidth - glyph.getWidth()) / 2,
								cy + (table.hexArray.maxHeight - glyph.getHeight()) / 2
							);
						}
						cy += rowHeight;
					}
				}
				x += columnWidth + insetWidth;
			}
		}
		// Paint borders.
		if (table.options.borderColor != null && table.options.borderWidth > 0) {
			g.setColor(table.options.borderColor);
			// Horizontal borders.
			g.fillRect(0, 0, width, table.options.borderWidth);
			for (int y = headerHeight; y < height; y += rowHeight) {
				g.fillRect(0, y, width, table.options.borderWidth);
			}
			// Vertical borders.
			x = 0;
			for (GlyphImageMatrix matrix : table.matrices) {
				g.fillRect(x, 0, table.options.borderWidth, height);
				x += headerWidth;
				for (BufferedImageArray array : matrix.arrays) {
					int columnWidth = Math.max(
						(matrix.fit ? array.maxWidth : matrix.maxWidth),
						table.hexArray.maxWidth
					);
					g.fillRect(x, 0, table.options.borderWidth, height);
					x += columnWidth + insetWidth;
				}
			}
			g.fillRect(x, 0, table.options.borderWidth, height);
		}
		g.dispose();
		return img;
	}
	
	public static class BufferedImageArray {
		public final BufferedImage[] images;
		public final int maxWidth;
		public final int maxHeight;
		public BufferedImageArray(BufferedImage... images) {
			int maxWidth = 0;
			int maxHeight = 0;
			for (BufferedImage image : images) {
				if (image != null) {
					int w = image.getWidth();
					int h = image.getHeight();
					if (w > maxWidth) maxWidth = w;
					if (h > maxHeight) maxHeight = h;
				}
			}
			this.images = images;
			this.maxWidth = maxWidth;
			this.maxHeight = maxHeight;
		}
	}
	
	public static class GlyphImageArray extends BufferedImageArray {
		public final int row;
		public GlyphImageArray(int row, BufferedImage... images) {
			super(images);
			this.row = row;
		}
	}
	
	public static class BufferedImageMatrix {
		public final BufferedImageArray[] arrays;
		public final int maxWidth;
		public final int maxHeight;
		public final int maxWidthSum;
		public final int maxHeightSum;
		public BufferedImageMatrix(BufferedImageArray... arrays) {
			int maxWidth = 0;
			int maxHeight = 0;
			int maxWidthSum = 0;
			int maxHeightSum = 0;
			for (BufferedImageArray array : arrays) {
				if (array != null) {
					if (array.maxWidth > maxWidth) maxWidth = array.maxWidth;
					if (array.maxHeight > maxHeight) maxHeight = array.maxHeight;
					maxWidthSum += array.maxWidth;
					maxHeightSum += array.maxHeight;
				}
			}
			this.arrays = arrays;
			this.maxWidth = maxWidth;
			this.maxHeight = maxHeight;
			this.maxWidthSum = maxWidthSum;
			this.maxHeightSum = maxHeightSum;
		}
	}
	
	public static class GlyphImageMatrix extends BufferedImageMatrix {
		public final int start;
		public final int end;
		public final boolean fit;
		public GlyphImageMatrix(int start, int end, boolean fit, BufferedImageArray... arrays) {
			super(arrays);
			this.start = start;
			this.end = end;
			this.fit = fit;
		}
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
		public boolean fit = false;
		public boolean antialias = true;
		public Color glyphfg = Color.black;
		public Color glyphbg = Color.white;
		public Color hexfg = Color.black;
		public Color hexbg = new Color(0xEBEBEB);
		public Color borderColor = new Color(0xD6D6D6);
		public int borderWidth = 1;
		public Insets padding = new Insets(1,1,1,1);
		public File output = new File("image.png");
		public List<Range> ranges = new ArrayList<Range>();
	}
	
	public static class GlyphImageTable {
		public final Options options;
		public final BufferedImageArray hexArray;
		public final GlyphImageMatrix[] matrices;
		public final int minStart;
		public final int maxEnd;
		public GlyphImageTable(Options options, BufferedImageArray hexArray, GlyphImageMatrix... matrices) {
			int minStart = Integer.MAX_VALUE;
			int maxEnd = Integer.MIN_VALUE;
			for (GlyphImageMatrix matrix : matrices) {
				if (matrix != null) {
					if (matrix.start < minStart) minStart = matrix.start;
					if (matrix.end > maxEnd) maxEnd = matrix.end;
				}
			}
			this.options = options;
			this.hexArray = hexArray;
			this.matrices = matrices;
			this.minStart = minStart;
			this.maxEnd = maxEnd;
		}
	}
}
