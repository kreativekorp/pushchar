package com.kreative.pushchar.test;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import com.kreative.pushchar.unilib.Encoding;

public class CheckCharsets {
	public static void main(String[] args) {
		List<String> exceptions = Arrays.asList(args);
		for (Charset cs : Charset.availableCharsets().values()) {
			System.out.print(cs.displayName() + ": ");
			if (exceptions.contains(cs.displayName())) {
				System.out.println("SKIPPED");
			} else try {
				long time = -System.currentTimeMillis();
				new Encoding(cs);
				time += System.currentTimeMillis();
				System.out.println((time < 1000) ? "OK" : ("LARGE (" + time + "ms)"));
			} catch (Throwable t) {
				System.out.println("ERROR: " + t);
			}
		}
	}
}
