package edu.njit.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Util {
	
	public static SimpleDateFormat dateFormat = new SimpleDateFormat("MM dd yyyy");
	public static long MILLISECONDS_PER_DAY = 86400000;
	public static double SECONDS_PER_DAY = 86400;
	static Date EPOCH;
	static Date START_DATE;
	static {
		dateFormat.setTimeZone(TimeZone.getTimeZone("EDT"));
		try {
			EPOCH = dateFormat.parse("1 1 1970");
			START_DATE = dateFormat.parse("12 30 1899");
		}catch(ParseException e) {
			e.printStackTrace();
		}
	}
	
	public static void checkNotNull(Object obj, String name) {
		
    	if (obj != null) {
    		System.out.println(name + " has a value"); 
    	}else {
    		throw new RuntimeException(name + " is null!");
    	}
    }
	
	public static byte[] combineByteArrays(byte[] a, byte[] b) {
		
		byte[] returnValue = new byte[a.length + b.length];
		for(int i = 0; i < a.length; i++) {
			returnValue[i] = a[i];
		}
		for(int i = 0; i < b.length; i++) {
			returnValue[a.length + i] = b[i];
		}
		return returnValue;
	}
	
	public static double dateToDaysSince(String day) {
		
		String[] parts = day.split("-");
		int year = Integer.parseInt(parts[0]);
		int month = Integer.parseInt(parts[1])-1;
		int dayOfMonth= Integer.parseInt(parts[2]);
		
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("EDT"));
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		cal.set(Calendar.YEAR, year);
				
		return (cal.getTimeInMillis() + EPOCH.getTime() - START_DATE.getTime())/MILLISECONDS_PER_DAY;
	}
	
	public static double timeToDaysSince(String time) {
				
		String[] parts = time.split(":");
		double hours = Double.parseDouble(parts[0]);
		double minutes = Double.parseDouble(parts[1]);
		
		double seconds = 0;
		try {
			seconds = Double.parseDouble(parts[2]);
		}catch(IndexOutOfBoundsException e) { //If only minutes and hours are specified
			seconds = 0;
		}
		
		return (hours*3600 + minutes*60 + seconds)/SECONDS_PER_DAY;
	}
		
	public static String removeSuffix(String s, String suffix) {
		if (s.endsWith(suffix)) {
			return s.substring(0, s.lastIndexOf(suffix));
		}else {
			return s;
		}
	}
	
	public static String toTitleCase(String s) {
		// Return a copy of s with the first letter capitalized
		
		if (s.length() == 0) {
			return s;
		}
		
		return ("" + s.charAt(0)).toUpperCase() + s.substring(1, s.length());
	}
}
