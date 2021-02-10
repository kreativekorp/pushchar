package com.kreative.pushchar.unilib;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class PropertyMap implements Cloneable {
	private final Map<String,Map<Integer,String>> byProperty;
	private final Map<Integer,Map<String,String>> byCodePoint;
	
	public PropertyMap() {
		this.byProperty = new HashMap<String,Map<Integer,String>>();
		this.byCodePoint = new HashMap<Integer,Map<String,String>>();
	}
	
	public PropertyMap(PropertyMap original) {
		this.byProperty = new HashMap<String,Map<Integer,String>>();
		this.byCodePoint = new HashMap<Integer,Map<String,String>>();
		for (Map.Entry<String,Map<Integer,String>> e : original.byProperty.entrySet()) {
			this.byProperty.put(e.getKey(), new HashMap<Integer,String>(e.getValue()));
		}
		for (Map.Entry<Integer,Map<String,String>> e : original.byCodePoint.entrySet()) {
			this.byCodePoint.put(e.getKey(), new HashMap<String,String>(e.getValue()));
		}
	}
	
	@Override
	public PropertyMap clone() {
		return new PropertyMap(this);
	}
	
	public SortedSet<Integer> codePoints() {
		return Collections.unmodifiableSortedSet(new TreeSet<Integer>(byCodePoint.keySet()));
	}
	
	public SortedMap<Integer,String> get(String property) {
		Map<Integer,String> m = byProperty.get(property);
		if (m == null) return null;
		return Collections.unmodifiableSortedMap(new TreeMap<Integer,String>(m));
	}
	
	public SortedMap<String,String> get(int codePoint) {
		Map<String,String> m = byCodePoint.get(codePoint);
		if (m == null) return null;
		return Collections.unmodifiableSortedMap(new TreeMap<String,String>(m));
	}
	
	public String get(String property, int codePoint) {
		Map<Integer,String> m = byProperty.get(property);
		if (m == null) return null;
		return m.get(codePoint);
	}
	
	public String get(int codePoint, String property) {
		Map<String,String> m = byCodePoint.get(codePoint);
		if (m == null) return null;
		return m.get(property);
	}
	
	public SortedSet<String> properties() {
		return Collections.unmodifiableSortedSet(new TreeSet<String>(byProperty.keySet()));
	}
	
	public void put(String property, int codePoint, String value) {
		Map<Integer,String> cm = byProperty.get(property);
		if (cm == null) byProperty.put(property, cm = new HashMap<Integer,String>());
		cm.put(codePoint, value);
		Map<String,String> pm = byCodePoint.get(codePoint);
		if (pm == null) byCodePoint.put(codePoint, pm = new HashMap<String,String>());
		pm.put(property, value);
	}
	
	public void put(int codePoint, String property, String value) {
		Map<Integer,String> cm = byProperty.get(property);
		if (cm == null) byProperty.put(property, cm = new HashMap<Integer,String>());
		cm.put(codePoint, value);
		Map<String,String> pm = byCodePoint.get(codePoint);
		if (pm == null) byCodePoint.put(codePoint, pm = new HashMap<String,String>());
		pm.put(property, value);
	}
	
	public void putAll(String property, Map<Integer,String> values) {
		Map<Integer,String> cm = byProperty.get(property);
		if (cm == null) byProperty.put(property, cm = new HashMap<Integer,String>());
		cm.putAll(values);
		for (Map.Entry<Integer,String> e : values.entrySet()) {
			Map<String,String> pm = byCodePoint.get(e.getKey());
			if (pm == null) byCodePoint.put(e.getKey(), pm = new HashMap<String,String>());
			pm.put(property, e.getValue());
		}
	}
	
	public void putAll(int codePoint, Map<String,String> values) {
		for (Map.Entry<String,String> e : values.entrySet()) {
			Map<Integer,String> cm = byProperty.get(e.getKey());
			if (cm == null) byProperty.put(e.getKey(), cm = new HashMap<Integer,String>());
			cm.put(codePoint, e.getValue());
		}
		Map<String,String> pm = byCodePoint.get(codePoint);
		if (pm == null) byCodePoint.put(codePoint, pm = new HashMap<String,String>());
		pm.putAll(values);
	}
	
	public void putAllByProperty(Map<String, ? extends Map<Integer,String>> map) {
		for (Map.Entry<String, ? extends Map<Integer,String>> e : map.entrySet()) {
			putAll(e.getKey(), e.getValue());
		}
	}
	
	public void putAllByCodePoint(Map<Integer, ? extends Map<String,String>> map) {
		for (Map.Entry<Integer, ? extends Map<String,String>> e : map.entrySet()) {
			putAll(e.getKey(), e.getValue());
		}
	}
}
