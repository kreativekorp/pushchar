package com.kreative.pushchar.unilib;

import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class BlockList implements Cloneable, Iterable<BlockList.Entry> {
	private final SortedMap<Integer,String> blocks;
	
	public BlockList() {
		this.blocks = new TreeMap<Integer,String>();
		this.blocks.put(0x000000, "Undefined (BMP)");
		this.blocks.put(0x010000, "Undefined (SMP)");
		this.blocks.put(0x020000, "Undefined (SIP)");
		this.blocks.put(0x030000, "Undefined (TIP)");
		this.blocks.put(0x040000, "Undefined (Plane 4)");
		this.blocks.put(0x050000, "Undefined (Plane 5)");
		this.blocks.put(0x060000, "Undefined (Plane 6)");
		this.blocks.put(0x070000, "Undefined (Plane 7)");
		this.blocks.put(0x080000, "Undefined (Plane 8)");
		this.blocks.put(0x090000, "Undefined (Plane 9)");
		this.blocks.put(0x0A0000, "Undefined (Plane 10)");
		this.blocks.put(0x0B0000, "Undefined (Plane 11)");
		this.blocks.put(0x0C0000, "Undefined (Plane 12)");
		this.blocks.put(0x0D0000, "Undefined (Plane 13)");
		this.blocks.put(0x0E0000, "Undefined (SSP)");
		this.blocks.put(0x0F0000, "Undefined (SPUA-A)");
		this.blocks.put(0x100000, "Undefined (SPUA-B)");
		this.blocks.put(0x110000, "Invalid");
	}
	
	public BlockList(BlockList original) {
		this.blocks = new TreeMap<Integer,String>();
		this.blocks.putAll(original.blocks);
	}
	
	@Override
	public BlockList clone() {
		return new BlockList(this);
	}
	
	public Entry get(int cp) {
		int firstCodePoint = blocks.headMap(cp + 1).lastKey();
		int lastCodePoint = blocks.tailMap(cp + 1).firstKey() - 1;
		String blockName = blocks.get(firstCodePoint);
		return new Entry(firstCodePoint, lastCodePoint, blockName);
	}
	
	@Override
	public Iterator<Entry> iterator() {
		return new Iterator<Entry>() {
			private Iterator<Map.Entry<Integer,String>> i = blocks.entrySet().iterator();
			private Map.Entry<Integer,String> p = i.hasNext() ? i.next() : null;
			@Override
			public boolean hasNext() {
				return i.hasNext();
			}
			@Override
			public Entry next() {
				Map.Entry<Integer,String> n = i.next();
				Entry e = new Entry(p.getKey(), n.getKey()-1, p.getValue());
				p = n;
				return e;
			}
			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
	
	public void put(Entry e) {
		put(e.firstCodePoint, e.lastCodePoint, e.blockName);
	}
	
	public void put(int firstCodePoint, int lastCodePoint, String blockName) {
		String nextBlockName = blocks.get(blocks.headMap(lastCodePoint + 2).lastKey());
		blocks.put(firstCodePoint, blockName);
		blocks.put(lastCodePoint + 1, nextBlockName);
		blocks.subMap(firstCodePoint + 1, lastCodePoint + 1).clear();
	}
	
	public static final class Entry {
		public final int firstCodePoint;
		public final int lastCodePoint;
		public final String blockName;
		private Entry(int fcp, int lcp, String name) {
			this.firstCodePoint = fcp;
			this.lastCodePoint = lcp;
			this.blockName = name;
		}
	}
}
