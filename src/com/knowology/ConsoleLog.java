package com.knowology;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ConsoleLog {
	public static void ConsoleDebug(String format, Object... args){
		//System.out.printf(">>[%s] DEBUG: ", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		System.out.printf(">>[%s] D: ", new SimpleDateFormat("mm:ss").format(new Date()));
		System.out.printf(format, args);
		System.out.println();
	}
	
	public static void ConsoleError(String format, Object... args){
		//System.out.printf(">>[%s] ERROR: ", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		System.out.printf(">>[%s] E: ", new SimpleDateFormat("mm:ss").format(new Date()));		
		System.out.printf(format, args);
		System.out.println();
	}
}
