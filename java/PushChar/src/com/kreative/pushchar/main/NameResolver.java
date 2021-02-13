package com.kreative.pushchar.main;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import com.kreative.pushchar.ttflib.PuaaTable;

public class NameResolver {
	private final Map<Integer,String> baseCategoryMap;
	private final SortedMap<Integer,String> baseNameMap;
	private final Map<Integer,String> baseUni1NameMap;
	private Map<Integer,String> categoryMap;
	private Map<Integer,String> nameMap;
	private Map<Integer,String> uni1NameMap;
	
	public NameResolver() {
		PuaaTable base = PuaaCache.getPuaaTable("unidata.ucd");
		this.baseCategoryMap = base.getPropertyMap("General_Category");
		this.baseNameMap = base.getPropertySortedMap("Name");
		this.baseUni1NameMap = base.getPropertyMap("Unicode_1_Name");
		this.categoryMap = this.baseCategoryMap;
		this.nameMap = this.baseNameMap;
		this.uni1NameMap = this.baseUni1NameMap;
	}
	
	public void setDataFont(Font font) {
		PuaaTable puaa = PuaaCache.getPuaaTable(font);
		if (puaa == null) {
			this.categoryMap = this.baseCategoryMap;
			this.nameMap = this.baseNameMap;
			this.uni1NameMap = this.baseUni1NameMap;
		} else {
			this.categoryMap = new HashMap<Integer,String>();
			this.nameMap = new HashMap<Integer,String>();
			this.uni1NameMap = new HashMap<Integer,String>();
			if (baseCategoryMap != null) categoryMap.putAll(baseCategoryMap);
			if (baseNameMap != null) nameMap.putAll(baseNameMap);
			if (baseUni1NameMap != null) uni1NameMap.putAll(baseUni1NameMap);
			Map<Integer,String> puaaCategoryMap = puaa.getPropertyMap("General_Category");
			Map<Integer,String> puaaNameMap = puaa.getPropertyMap("Name");
			Map<Integer,String> puaaUni1NameMap = puaa.getPropertyMap("Unicode_1_Name");
			if (puaaCategoryMap != null) categoryMap.putAll(puaaCategoryMap);
			if (puaaNameMap != null) nameMap.putAll(puaaNameMap);
			if (puaaUni1NameMap != null) uni1NameMap.putAll(puaaUni1NameMap);
		}
	}
	
	public String getCategory(int cp) {
		if (categoryMap == null) return null;
		String category = categoryMap.get(cp);
		if (category != null) return category;
		try {
			int prevcp = baseNameMap.headMap(cp).lastKey();
			int nextcp = baseNameMap.tailMap(cp).firstKey();
			String prevName = baseNameMap.get(prevcp);
			String nextName = baseNameMap.get(nextcp);
			if (isRangePair(prevName, nextName)) {
				String prevcat = baseCategoryMap.get(prevcp);
				String nextcat = baseCategoryMap.get(nextcp);
				if (prevcat.equals(nextcat)) return prevcat;
			}
			return "Cn";
		} catch (Exception e) {
			return "Cn";
		}
	}
	
	public String getName(int cp) {
		if (nameMap == null) return null;
		String name = nameMap.get(cp);
		if (name != null) {
			if (!(name.startsWith("<") && name.endsWith(">"))) return name;
			if (name.equals("<control>")) {
				name = uni1NameMap.get(cp);
				if (name != null) return name;
				return "CONTROL-" + toHexString(cp);
			}
		} else {
			try {
				int prevcp = baseNameMap.headMap(cp).lastKey();
				int nextcp = baseNameMap.tailMap(cp).firstKey();
				String prevName = baseNameMap.get(prevcp);
				String nextName = baseNameMap.get(nextcp);
				if (isRangePair(prevName, nextName)) {
					name = prevName;
				} else {
					return "UNDEFINED-" + toHexString(cp);
				}
			} catch (Exception e) {
				return "UNDEFINED-" + toHexString(cp);
			}
		}
		if (name.contains("CJK Ideograph")) return "CJK UNIFIED IDEOGRAPH-" + toHexString(cp);
		if (name.contains("Tangut Ideograph")) return "TANGUT IDEOGRAPH-" + toHexString(cp);
		if (name.contains("High Surrogate")) return "HIGH SURROGATE-" + toHexString(cp);
		if (name.contains("Private Use")) return "PRIVATE USE-" + toHexString(cp);
		return name.replaceAll("^<|, (Fir|La)st>$", "").toUpperCase() + "-" + toHexString(cp);
	}
	
	private static boolean isRangePair(String a, String b) {
		return (
			a.startsWith("<") && a.endsWith(", First>") &&
			b.startsWith("<") && b.endsWith(", Last>") &&
			a.substring(0, a.length() - 6).equals(b.substring(0, b.length() - 5))
		);
	}
	
	private static String toHexString(int cp) {
		String h = Integer.toHexString(cp).toUpperCase();
		if (h.length() < 4) h = ("0000" + h).substring(h.length());
		return h;
	}
}
