package edu.njit.knime.adapter.nodes.qvx;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Util {
	
	public static SimpleDateFormat dateFormat = new SimpleDateFormat("MM dd yyyy");
	public static long SECONDS_PER_DAY = 86400;
	public static long MILLISECONDS_PER_DAY = 86400000;
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
	
	public static Calendar getDateFromQvxReal(double daysSince) {
		
		long fullDaysSince = Math.round(daysSince);
		double partialDaysSince = daysSince - fullDaysSince;
		long remainingSeconds = Math.round(partialDaysSince*SECONDS_PER_DAY);
		
		long dateInMilliseconds = (START_DATE.getTime()-EPOCH.getTime()) +
				(long)(fullDaysSince*MILLISECONDS_PER_DAY) +
				(long)(remainingSeconds*1000);
		Date date = new Date(dateInMilliseconds);
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("EDT"));
		calendar.setTime(date);
		return calendar;
	}
	
	public static String removeSuffix(String s, String suffix) {
		if (s.endsWith(suffix)) {
			return s.substring(0, s.lastIndexOf(suffix));
		}else {
			return s;
		}
	}
	
	public static String toTitleCase(String s) {
		/* Return a copy of s with the first letter capitalized */
		
		if (s.length() == 0) {
			return s;
		}
		
		return ("" + s.charAt(0)).toUpperCase() + s.substring(1, s.length());
	}
}

