package com.kreative.pushchar.main;

import java.awt.Font;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.kreative.pushchar.ttflib.PuaaEntry;
import com.kreative.pushchar.ttflib.PuaaTable;
import com.kreative.pushchar.unilib.BlockList;
import com.kreative.pushchar.unilib.Encoding;
import com.kreative.pushchar.unilib.EncodingTable;
import com.kreative.pushchar.unilib.GlyphList;

public abstract class SectionBuilder {
	public abstract String getName();
	public abstract List<Section> build(Font font);
	
	@Override
	public final String toString() {
		return getName();
	}
	
	public static class ForBlockList extends SectionBuilder {
		private final BlockList bl;
		public ForBlockList(BlockList bl) {
			this.bl = bl;
		}
		@Override
		public String getName() {
			return "Unicode";
		}
		@Override
		public List<Section> build(Font font) {
			// Load block list.
			BlockList bl = this.bl;
			PuaaTable puaa = PuaaCache.getPuaaTable(font);
			if (puaa != null) {
				List<PuaaEntry> blocks = puaa.getPropertyEntries("Block");
				if (blocks != null && blocks.size() > 0) {
					bl = bl.clone();
					for (PuaaEntry block : blocks) {
						int fcp = block.getFirstCodePoint();
						int lcp = block.getLastCodePoint();
						String name = block.getPropertyString(fcp);
						bl.put(fcp, lcp, name);
					}
				}
			}
			// Create sections.
			List<Section> sections = new ArrayList<Section>();
			for (BlockList.Entry block : bl) {
				Section s = Section.forBlock(font, block);
				if (s.getVisibleCount() > 0) sections.add(s);
			}
			return sections;
		}
	}
	
	public static class ForCharset extends SectionBuilder {
		private final Charset cs;
		private ForEncoding forEnc;
		public ForCharset(Charset cs) {
			this.cs = cs;
			this.forEnc = null;
		}
		@Override
		public String getName() {
			return cs.displayName();
		}
		@Override
		public List<Section> build(Font font) {
			if (forEnc == null) {
				try { forEnc = new ForEncoding(new Encoding(cs)); }
				catch (Exception e) { return new ArrayList<Section>(); }
			}
			return forEnc.build(font);
		}
	}
	
	public static class ForEncoding extends SectionBuilder {
		private final Encoding enc;
		public ForEncoding(Encoding enc) {
			this.enc = enc;
		}
		@Override
		public String getName() {
			return enc.getName();
		}
		@Override
		public List<Section> build(Font font) {
			List<Section> sections = new ArrayList<Section>();
			Section s = Section.forEncodingTable(font, enc.getName(), enc);
			if (s.getVisibleCount() > 0) sections.add(s);
			build(font, enc, "Subtable ", sections);
			return sections;
		}
		private void build(Font font, EncodingTable enc, String pfx, List<Section> sections) {
			for (int i = 0; i < 256; i++) {
				EncodingTable sub = enc.getSubtable(i);
				if (sub != null) {
					StringBuffer sb = new StringBuffer(pfx);
					sb.append(Character.toUpperCase(Character.forDigit(i >> 4, 16)));
					sb.append(Character.toUpperCase(Character.forDigit(i & 15, 16)));
					String subpfx = sb.toString();
					Section s = Section.forEncodingTable(font, subpfx, sub);
					if (s.getVisibleCount() > 0) sections.add(s);
					build(font, sub, subpfx, sections);
				}
			}
		}
	}
	
	public static class ForGlyphList extends SectionBuilder {
		private final GlyphList gl;
		public ForGlyphList(GlyphList gl) {
			this.gl = gl;
		}
		@Override
		public String getName() {
			return gl.getName();
		}
		@Override
		public List<Section> build(Font font) {
			return Arrays.asList(Section.forGlyphList(font, gl));
		}
	}
}
