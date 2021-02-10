package com.kreative.pushchar.unilib;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.Scanner;

public class Encoding extends EncodingTable {
	private final String name;
	
	public Encoding(String name, InputStream in) {
		this.name = name;
		read(new Scanner(in));
	}
	
	public Encoding(String name, Scanner in) {
		this.name = name;
		read(in);
	}
	
	public Encoding(Charset cs) {
		this.name = cs.displayName();
		decode(cs);
	}
	
	public String getName() {
		return name;
	}
	
	private void read(Scanner in) {
		while (in.hasNextLine()) {
			String line = in.nextLine().trim();
			if (line.length() > 0 && line.charAt(0) != '#') {
				String[] parts = line.split("\\s+");
				if (parts.length >= 2) {
					try {
						byte[] key = parseBytes(parts[0]);
						String value = parseChars(parts[1]);
						setSequence(value, key);
					} catch (NumberFormatException nfe) {
						continue;
					}
				}
			}
		}
	}
	
	private void decode(Charset cs) {
		CharsetDecoder decoder = cs.newDecoder();
		decoder.onMalformedInput(CodingErrorAction.REPLACE);
		decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
		decoder.replaceWith("\uFFFF");
		decode(decoder, new byte[256], 0, new char[256]);
	}
	
	private void decode(CharsetDecoder decoder, byte[] in, int pos, char[] out) {
		for (int ch = 0; ch < 256; ch++) {
			in[pos] = (byte)ch;
			ByteBuffer inb = ByteBuffer.wrap(in, 0, pos + 1);
			CharBuffer outb = CharBuffer.wrap(out);
			decoder.reset();
			decoder.decode(inb, outb, false);
			// Do NOT finish the decode so we can distinguish
			// incomplete input from invalid input.
			if (outb.position() > 0) {
				String s = new String(out, 0, outb.position());
				if (s.contains("\uFFFF")) continue;
				setSequence(s, in, 0, pos + 1);
			} else {
				decode(decoder, in, pos + 1, out);
			}
		}
	}
	
	private static byte[] parseBytes(String s) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		String[] parts = s.split("[+]");
		for (String part : parts) {
			part = part.toUpperCase();
			if (part.startsWith("0X")) part = part.substring(2);
			if ((part.length() & 1) == 1) part = "0" + part;
			for (int i = 0, n = part.length(); i < n; i += 2) {
				String h = part.substring(i, i + 2);
				os.write(Integer.parseInt(h, 16));
			}
		}
		return os.toByteArray();
	}
	
	private static String parseChars(String s) {
		StringBuffer sb = new StringBuffer();
		String[] parts = s.split("[+]");
		for (String part : parts) {
			part = part.toUpperCase();
			if (part.startsWith("0X")) part = part.substring(2);
			sb.append(Character.toChars(Integer.parseInt(part, 16)));
		}
		return sb.toString();
	}
}
