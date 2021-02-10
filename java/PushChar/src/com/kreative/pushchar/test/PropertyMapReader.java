package com.kreative.pushchar.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;
import com.kreative.pushchar.unilib.PropertyMap;

public class PropertyMapReader extends PropertyMap {
	/** Automatically delegates to the appropriate read method based on file name. */
	public void readUnidata(File file) throws FileNotFoundException {
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				if (!child.getName().startsWith(".")) {
					readUnidata(child);
				}
			}
		} else {
			readUnidata(file.getName(), new Scanner(file, "UTF-8"));
		}
	}
	
	/** Automatically delegates to the appropriate read method based on file name. */
	public void readUnidata(String name, InputStream in) {
		readUnidata(name, new Scanner(in, "UTF-8"));
	}
	
	/** Automatically delegates to the appropriate read method based on file name. */
	public void readUnidata(String name, Scanner scan) {
		name = name.toLowerCase().replaceAll("\\.txt$", "");
		if (name.equals("unicodedata")) {
			readValues(
				scan,
				"Name",
				"General_Category",
				"Canonical_Combining_Class",
				"Bidi_Class",
				"Decomposition_Type+Mapping",
				"Numeric_Type+Value",
				null,
				null,
				"Bidi_Mirrored",
				"Unicode_1_Name",
				"ISO_Comment",
				"Simple_Uppercase_Mapping",
				"Simple_Lowercase_Mapping",
				"Simple_Titlecase_Mapping"
			);
		} else if (name.equals("arabicshaping")) {
			readValues(scan, null, "Joining_Type", "Joining_Group");
		} else if (name.equals("bidibrackets")) {
			readValues(scan, "Bidi_Paired_Bracket", "Bidi_Paired_Bracket_Type");
		} else if (name.equals("bidimirroring")) {
			readValues(scan, "Bidi_Mirroring_Glyph");
		} else if (name.equals("blocks")) {
			readValues(scan, "Block");
		} else if (name.equals("compositionexclusions")) {
			readValues(scan, "Composition_Exclusion");
		} else if (name.equals("derivedage")) {
			readValues(scan, "Age");
		} else if (name.equals("eastasianwidth")) {
			readValues(scan, "East_Asian_Width");
		} else if (name.equals("emoji-data")) {
			readPropList(scan);
		} else if (name.equals("equivalentunifiedideograph")) {
			readValues(scan, "Equivalent_Unified_Ideograph");
		} else if (name.equals("graphemebreakproperty")) {
			readValues(scan, "Grapheme_Cluster_Break");
		} else if (name.equals("hangulsyllabletype")) {
			readValues(scan, "Hangul_Syllable_Type");
		} else if (name.equals("indicpositionalcategory")) {
			readValues(scan, "Indic_Positional_Category");
		} else if (name.equals("indicsyllabiccategory")) {
			readValues(scan, "Indic_Syllabic_Category");
		} else if (name.equals("jamo")) {
			readValues(scan, "Jamo_Short_Name");
		} else if (name.equals("linebreak")) {
			readValues(scan, "Line_Break");
		} else if (name.equals("namealiases")) {
			readValues(scan, "Name_Alias");
		} else if (name.equals("nushusources")) {
			readUnihan(scan);
		} else if (name.equals("proplist")) {
			readPropList(scan);
		} else if (name.equals("scriptextensions")) {
			readValues(scan, "Script_Extensions");
		} else if (name.equals("scripts")) {
			readValues(scan, "Script");
		} else if (name.equals("sentencebreakproperty")) {
			readValues(scan, "Sentence_Break");
		} else if (name.equals("specialcasing")) {
			readValues(scan, "Lowercase_Mapping", "Titlecase_Mapping", "Uppercase_Mapping");
		} else if (name.equals("tangutsources")) {
			readUnihan(scan);
		} else if (name.startsWith("unihan_")) {
			readUnihan(scan);
		} else if (name.equals("verticalorientation")) {
			readValues(scan, "Vertical_Orientation");
		} else if (name.equals("wordbreakproperty")) {
			readValues(scan, "Word_Break");
		}
	}
	
	/** Read method for most non-Unihan UCD property files. */
	public void readValues(InputStream in, String... properties) {
		readValues(new Scanner(in, "UTF-8"), properties);
	}
	
	/** Read method for most non-Unihan UCD property files. */
	public void readValues(Scanner scan, String... properties) {
		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			int o = line.indexOf('#');
			if (o >= 0) line = line.substring(0, o);
			line = line.trim();
			if (line.length() == 0) continue;
			String[] fields = line.split(";");
			String[] range = fields[0].split("[.]+");
			try {
				int fcp = (range.length > 0) ? Integer.parseInt(range[0].trim(), 16) : -1;
				int lcp = (range.length > 1) ? Integer.parseInt(range[1].trim(), 16) : fcp;
				for (int p = 0, f = 1; p < properties.length; p++, f++) {
					if (properties[p] == null) {
						continue;
					} else if (properties[p].equals("Decomposition_Type+Mapping")) {
						// Hack for UnicodeData.txt field 5 which specifies TWO properties in ONE.
						if (fields.length > f && (fields[f] = fields[f].trim()).length() > 0) {
							if (fields[f].startsWith("<")) {
								int g = fields[f].lastIndexOf('>');
								String type = fields[f].substring(0, g + 1);
								String mapping = fields[f].substring(g + 1).trim();
								for (int cp = fcp; cp <= lcp; cp++) {
									put("Decomposition_Type", cp, type);
									put("Decomposition_Mapping", cp, mapping);
								}
							} else {
								for (int cp = fcp; cp <= lcp; cp++) {
									put("Decomposition_Mapping", cp, fields[f]);
								}
							}
						}
					} else if (properties[p].equals("Numeric_Type+Value")) {
						// Hack for UnicodeData.txt fields 6, 7, 8 which specify TWO properties in THREE!
						for (String type : Arrays.asList("Decimal", "Digit", "Numeric")) {
							if (fields.length > f && (fields[f] = fields[f].trim()).length() > 0) {
								for (int cp = fcp; cp <= lcp; cp++) {
									put("Numeric_Type", cp, type);
									put("Numeric_Value", cp, fields[f]);
								}
								break;
							}
							f++;
						}
						f = p + 1;
					} else if (properties[p].equals("Composition_Exclusion")) {
						// Hack for CompositionExclusions.txt which lists only code points and no values.
						for (int cp = fcp; cp <= lcp; cp++) {
							put(properties[p], cp, "Y");
						}
					} else if (properties[p].equals("Jamo_Short_Name")) {
						// Hack for Jamo.txt wherein the empty string is a valid property value.
						String value = (fields.length > f) ? fields[f].trim() : "";
						for (int cp = fcp; cp <= lcp; cp++) {
							put(properties[p], cp, value);
						}
					} else if (properties[p].equals("Name_Alias")) {
						// Hack for NameAliases.txt which has multiple values and specifies ONE property in TWO!?
						if (fields.length > f && (fields[f] = fields[f].trim()).length() > 0) {
							String value = fields[f++];
							if (fields.length > f && (fields[f] = fields[f].trim()).length() > 0) {
								value += ";" + fields[f];
								for (int cp = fcp; cp <= lcp; cp++) {
									String prev = get(properties[p], cp);
									if (prev == null) put(properties[p], cp, value);
									else put(properties[p], cp, prev + "\n" + value);
								}
							}
						}
						f = p + 1;
					} else if (properties[p].equals("Lowercase_Mapping")
					       ||  properties[p].equals("Titlecase_Mapping")
					       ||  properties[p].equals("Uppercase_Mapping")) {
						// Hack for SpecialCasing.txt which has multiple values and is way too... weird.
						if (fields.length > f && (fields[f] = fields[f].trim()).length() > 0) {
							String value = fields[f];
							if (fields.length > 4 && (fields[4] = fields[4].trim()).length() > 0) {
								value += "; " + fields[4];
							}
							for (int cp = fcp; cp <= lcp; cp++) {
								String prev = get(properties[p], cp);
								if (prev == null) put(properties[p], cp, value);
								else put(properties[p], cp, prev + "\n" + value);
							}
						}
					} else {
						if (fields.length > f && (fields[f] = fields[f].trim()).length() > 0) {
							for (int cp = fcp; cp <= lcp; cp++) {
								put(properties[p], cp, fields[f]);
							}
						}
					}
				}
			} catch (NumberFormatException nfe) {
				continue;
			}
		}
	}
	
	/** Read method for PropList.txt and emoji-data.txt. */
	public void readPropList(InputStream in) {
		readPropList(new Scanner(in, "UTF-8"));
	}
	
	/** Read method for PropList.txt and emoji-data.txt. */
	public void readPropList(Scanner scan) {
		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			int o = line.indexOf('#');
			if (o >= 0) line = line.substring(0, o);
			line = line.trim();
			if (line.length() == 0) continue;
			String[] fields = line.split(";");
			if (fields.length < 2) continue;
			String[] range = fields[0].split("[.]+");
			String prop = fields[1].trim();
			try {
				int fcp = (range.length > 0) ? Integer.parseInt(range[0].trim(), 16) : -1;
				int lcp = (range.length > 1) ? Integer.parseInt(range[1].trim(), 16) : fcp;
				for (int cp = fcp; cp <= lcp; cp++) put(cp, prop, "Y");
			} catch (NumberFormatException nfe) {
				continue;
			}
		}
	}
	
	/** Read method for NushuSources.txt, TangutSources.txt, and all Unihan property files. */
	public void readUnihan(InputStream in) {
		readUnihan(new Scanner(in, "UTF-8"));
	}
	
	/** Read method for NushuSources.txt, TangutSources.txt, and all Unihan property files. */
	public void readUnihan(Scanner scan) {
		while (scan.hasNextLine()) {
			String line = scan.nextLine().trim();
			if (line.length() == 0 || line.startsWith("#")) continue;
			String[] fields = line.split("\\s+", 3);
			if (fields.length < 3) continue;
			String cp = fields[0].replaceAll("^([Uu][+]|[0][Xx])", "");
			try { put(Integer.parseInt(cp, 16), fields[1], fields[2]); }
			catch (NumberFormatException nfe) { continue; }
		}
	}
}
