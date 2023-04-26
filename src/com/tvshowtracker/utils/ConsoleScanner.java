package com.tvshowtracker.utils;

import java.io.Closeable;
import java.io.IOException;
import java.util.Scanner;

public class ConsoleScanner implements Closeable{
	private static Scanner input = new Scanner(System.in);
	
	public static int getInt() {
		int enteredInt = input.nextInt();
		input.nextLine();
		return enteredInt;
	}
	
	public static String getString() {
		return input.nextLine();
	}

	@Override
	public void close() throws IOException {
		if (input != null) {
			input.close();
		}
	}
}
