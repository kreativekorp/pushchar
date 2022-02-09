package com.kreative.pushchar.main;

import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Scanner;

public class Options {
	public String defaultFontName = "SansSerif";
	public int defaultFontStyle = Font.PLAIN;
	public int defaultFontSize = 12;
	public TriggerWindow.Position pushPosition = TriggerWindow.Position.NORTHWEST;
	public TriggerWindow.Position searchPosition = TriggerWindow.Position.NORTHWEST;
	public TriggerWindow.Orientation orientation = TriggerWindow.Orientation.WEST_EAST;
	
	public void read() throws IOException {
		read(getPreferencesFile());
	}
	
	public void read(File file) throws IOException {
		Scanner in = new Scanner(file, "UTF-8");
		read(in);
		in.close();
	}
	
	public void read(Scanner in) {
		while (in.hasNextLine()) {
			String line = in.nextLine().trim();
			if (line.startsWith(";")) continue;
			int ofs = line.indexOf("=");
			if (ofs < 0) continue;
			String key = line.substring(0, ofs).trim();
			String value = line.substring(ofs + 1).trim();
			
			if (key.equalsIgnoreCase("FontName")) {
				defaultFontName = value;
			}
			if (key.equalsIgnoreCase("FontStyle")) {
				int i; try { i = Integer.parseInt(value); }
				catch (NumberFormatException e) { continue; }
				defaultFontStyle = i;
			}
			if (key.equalsIgnoreCase("FontSize")) {
				int i; try { i = Integer.parseInt(value); }
				catch (NumberFormatException e) { continue; }
				defaultFontSize = i;
			}
			if (key.equalsIgnoreCase("PushPosition")) {
				TriggerWindow.Position p;
				try { p = TriggerWindow.Position.valueOf(value); }
				catch (IllegalArgumentException e) { continue; }
				pushPosition = p;
			}
			if (key.equalsIgnoreCase("SearchPosition")) {
				TriggerWindow.Position p;
				try { p = TriggerWindow.Position.valueOf(value); }
				catch (IllegalArgumentException e) { continue; }
				searchPosition = p;
			}
			if (key.equalsIgnoreCase("Orientation")) {
				TriggerWindow.Orientation o;
				try { o = TriggerWindow.Orientation.valueOf(value); }
				catch (IllegalArgumentException e) { continue; }
				orientation = o;
			}
		}
	}
	
	public void write() throws IOException {
		write(getPreferencesFile());
	}
	
	public void write(File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
		PrintWriter pw = new PrintWriter(osw, true);
		write(pw);
		pw.flush();
		pw.close();
	}
	
	public void write(PrintWriter out) {
		out.println("FontName=" + defaultFontName);
		out.println("FontStyle=" + defaultFontStyle);
		out.println("FontSize=" + defaultFontSize);
		out.println("PushPosition=" + pushPosition);
		out.println("SearchPosition=" + searchPosition);
		out.println("Orientation=" + orientation);
	}
	
	private static File getPreferencesFile() {
		if (System.getProperty("os.name").toUpperCase().contains("MAC OS")) {
			File u = new File(System.getProperty("user.home"));
			File l = new File(u, "Library"); if (!l.exists()) l.mkdir();
			File p = new File(l, "Preferences"); if (!p.exists()) p.mkdir();
			return new File(p, "com.kreative.pushchar.ini");
		} else if (System.getProperty("os.name").toUpperCase().contains("WINDOWS")) {
			File u = new File(System.getProperty("user.home"));
			File a = new File(u, "Application Data"); if (!a.exists()) a.mkdir();
			File k = new File(a, "Kreative"); if (!k.exists()) k.mkdir();
			return new File(k, "PushChar.ini");
		} else {
			File u = new File(System.getProperty("user.home"));
			File c = new File(u, ".config"); if (!c.exists()) c.mkdir();
			return new File(c, "pushchar.ini");
		}
	}
}
