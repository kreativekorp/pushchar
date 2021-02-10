package com.kreative.pushchar.test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Scanner;

public class Profile {
	public static void main(String[] args) throws Exception {
		Method main = Class.forName(args[0]).getMethod("main", String[].class);
		Object a = Arrays.asList(args).subList(1, args.length).toArray(new String[args.length-1]);
		Scanner scan = new Scanner(System.in);
		boolean first = true;
		outer: while (true) {
			System.out.println(first ? "Start?" : "Again?");
			inner: while (true) {
				if (!scan.hasNextLine()) break outer;
				String s = scan.nextLine().trim().toLowerCase();
				if (s.startsWith("y")) break inner;
				if (s.startsWith("n")) break outer;
			}
			long time = -System.currentTimeMillis();
			main.invoke(null, a);
			time += System.currentTimeMillis();
			System.out.println(time + " ms");
			first = false;
		}
		scan.close();
	}
}
