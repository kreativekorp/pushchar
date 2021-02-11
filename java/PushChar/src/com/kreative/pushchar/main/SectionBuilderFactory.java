package com.kreative.pushchar.main;

import java.io.DataInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import com.kreative.pushchar.ttflib.PuaaEntry;
import com.kreative.pushchar.ttflib.PuaaTable;
import com.kreative.pushchar.unilib.BlockList;
import com.kreative.pushchar.unilib.Encoding;
import com.kreative.pushchar.unilib.GlyphList;

public class SectionBuilderFactory {
	private static SectionBuilderFactory instance = null;
	public static SectionBuilderFactory getInstance() {
		if (instance == null) instance = new SectionBuilderFactory();
		return instance;
	}
	
	private final BlockList blockList = new BlockList();
	private final SortedSet<Charset> charsets = new TreeSet<Charset>(charsetComparator);
	private final SortedSet<Encoding> encodings = new TreeSet<Encoding>(encodingComparator);
	private final SortedSet<GlyphList> glyphLists = new TreeSet<GlyphList>(glyphListComparator);
	
	public SectionBuilderFactory() {
		addPuaaTable(PuaaCache.getPuaaTable("unidata.ucd"));
		charsets.addAll(Charset.availableCharsets().values());
		addGlyphList(getGlyphList("kgl1.pchgl"));
		addGlyphList(getGlyphList("wgl4.pchgl"));
	}
	
	public BlockList blockList() { return blockList; }
	public SortedSet<Encoding> encodings() { return encodings; }
	public SortedSet<GlyphList> glyphLists() { return glyphLists; }
	
	public boolean addPuaaTable(PuaaTable puaa) {
		if (puaa != null) {
			List<PuaaEntry> blocks = puaa.getPropertyEntries("Block");
			if (blocks != null && blocks.size() > 0) {
				for (PuaaEntry block : blocks) {
					int fcp = block.getFirstCodePoint();
					int lcp = block.getLastCodePoint();
					String name = block.getPropertyString(fcp);
					blockList.put(fcp, lcp, name);
				}
				return true;
			}
		}
		return false;
	}
	
	public boolean addCharset(Charset cs) {
		return cs != null && charsets.add(cs);
	}
	
	public boolean addEncoding(Encoding enc) {
		return enc != null && encodings.add(enc);
	}
	
	public boolean addGlyphList(GlyphList gl) {
		return gl != null && glyphLists.add(gl);
	}
	
	public List<SectionBuilder> createBuilders() {
		List<SectionBuilder> builders = new ArrayList<SectionBuilder>();
		builders.add(new SectionBuilder.ForBlockList(blockList));
		for (Charset cs : charsets) builders.add(new SectionBuilder.ForCharset(cs));
		for (Encoding enc : encodings) builders.add(new SectionBuilder.ForEncoding(enc));
		for (GlyphList gl : glyphLists) builders.add(new SectionBuilder.ForGlyphList(gl));
		return builders;
	}
	
	private static GlyphList getGlyphList(String name) {
		try {
			InputStream in = SectionBuilderFactory.class.getResourceAsStream(name);
			GlyphList gl = new GlyphList(new DataInputStream(in));
			in.close();
			return gl;
		} catch (Exception e) {
			return null;
		}
	}
	
	private static int naturalCompare(String a, String b) {
		List<String> na = naturalTokenize(a.trim());
		List<String> nb = naturalTokenize(b.trim());
		for (int i = 0; i < na.size() && i < nb.size(); i++) {
			try {
				double va = Double.parseDouble(na.get(i));
				double vb = Double.parseDouble(nb.get(i));
				int cmp = Double.compare(va, vb);
				if (cmp != 0) return cmp;
			} catch (NumberFormatException e) {
				int cmp = na.get(i).compareToIgnoreCase(nb.get(i));
				if (cmp != 0) return cmp;
			}
		}
		return na.size() - nb.size();
	}
	
	private static List<String> naturalTokenize(String s) {
		List<String> tokens = new ArrayList<String>();
		StringBuffer token = new StringBuffer();
		int tokenType = 0;
		CharacterIterator iter = new StringCharacterIterator(s);
		for (char ch = iter.first(); ch != CharacterIterator.DONE; ch = iter.next()) {
			int tt = Character.isDigit(ch) ? 1 : Character.isLetter(ch) ? 2 : 3;
			if (tt != tokenType) {
				if (token.length() > 0) {
					tokens.add(token.toString());
					token = new StringBuffer();
				}
				tokenType = tt;
			}
			token.append(ch);
		}
		if (token.length() > 0) tokens.add(token.toString());
		return tokens;
	}
	
	private static final Comparator<Charset> charsetComparator = new Comparator<Charset>() {
		@Override
		public int compare(Charset a, Charset b) {
			return naturalCompare(a.displayName(), b.displayName());
		}
	};
	
	private static final Comparator<Encoding> encodingComparator = new Comparator<Encoding>() {
		@Override
		public int compare(Encoding a, Encoding b) {
			return naturalCompare(a.getName(), b.getName());
		}
	};
	
	private static final Comparator<GlyphList> glyphListComparator = new Comparator<GlyphList>() {
		@Override
		public int compare(GlyphList a, GlyphList b) {
			return naturalCompare(a.getName(), b.getName());
		}
	};
}
