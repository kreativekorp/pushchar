package com.kreative.pushchar.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

import com.kreative.pushchar.unilib.BlockList;

public class BlockListReader extends BlockList {
	public void read(File file) throws FileNotFoundException {
		read(new Scanner(file, "UTF-8"));
	}
	
	public void read(InputStream in) {
		read(new Scanner(in, "UTF-8"));
	}
	
	public void read(Scanner scan) {
		while (scan.hasNextLine()) {
			String line = scan.nextLine().trim();
			if (line.length() == 0 || line.startsWith("#")) continue;
			String[] fields = line.split(";");
			if (fields.length < 2) continue;
			String[] range = fields[0].split("[.]+");
			if (range.length < 2) continue;
			try {
				int fcp = Integer.parseInt(range[0].trim(), 16);
				int lcp = Integer.parseInt(range[1].trim(), 16);
				String name = fields[1].trim();
				put(fcp, lcp, name);
			} catch (NumberFormatException nfe) {
				continue;
			}
		}
	}
}
