package com.kreative.pushchar.test;

import java.util.Scanner;
import com.kreative.pushchar.main.NameDatabase;
import com.kreative.pushchar.main.NameDatabase.NameEntry;

public class NameLookup {
	public static void main(String[] args) throws InterruptedException {
		System.out.println("Building database...");
		NameDatabase db = new NameDatabase();
		db.join();
		System.out.println("Ready.");
		if (args.length > 0) {
			for (String arg : args) {
				System.out.println(arg);
				for (NameEntry e : db.find(arg)) printEntry(e);
			}
		} else {
			Scanner scan = new Scanner(System.in);
			while (scan.hasNextLine()) {
				String line = scan.nextLine();
				for (NameEntry e : db.find(line)) printEntry(e);
				System.out.println("Ready.");
			}
			scan.close();
		}
	}
	
	private static void printEntry(NameEntry e) {
		String h = Integer.toHexString(e.codePoint).toUpperCase();
		if (h.length() < 4) h = ("0000" + h).substring(h.length());
		double sd = Math.rint(e.searchDistance * 1000) / 1000;
		String font = e.shortestFontName();
		if (e.fonts.size() > 1) font += " (+" + (e.fonts.size()-1) + " more)";
		System.out.println(
			"\t" + h +
			"\t" + e.name +
			"\t" + sd +
			((font != null) ? ("\t" + font) : "")
		);
	}
}
