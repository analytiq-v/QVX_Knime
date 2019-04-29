package edu.njit.knime.adapter.nodes.qvx;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.knime.core.data.def.TimestampCell;

public class Util {
	
	public static Calendar nullCalendar = Calendar.getInstance(TimeZone.getTimeZone("EDT"));
	public static SimpleDateFormat dateFormat = new SimpleDateFormat("MM dd yyyy");
	public static long SECONDS_PER_DAY = 86400;
	public static long MILLISECONDS_PER_DAY = 86400000;
	static Date EPOCH;
	static Date START_DATE;
	static {
		nullCalendar.set(Calendar.MONTH, 0);
		nullCalendar.set(Calendar.DAY_OF_MONTH, 1);
		nullCalendar.set(Calendar.YEAR, 0);
		nullCalendar.set(Calendar.HOUR, 0);
		nullCalendar.set(Calendar.MINUTE, 0);
		nullCalendar.set(Calendar.SECOND, 0);
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
	
	public static Calendar getDateFromString(String dateTimeString) {
		
		System.out.println("Getting date from string: " + dateTimeString);
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("EDT"));
		String[] dateTimeSplit = dateTimeString.split("");
		
		String date = null;
		String time = null;
		Integer ampm = null;
		
		for(int i = 0; i < dateTimeSplit.length; i++) {
			String part = dateTimeSplit[i];
			if (part.contains("/")) { //Date
				date = part;
			}else if(part.contains(":")){ //Time
				time = part;
			}else if(part.toLowerCase().equals("AM")) {
				ampm = Calendar.AM;
			}else if(part.toUpperCase().equals("PM")) {
				ampm = Calendar.PM;
			}else {
				return (Calendar)nullCalendar.clone();
			}
		}
		
		//Valid date formats include MM/DD/YYYY and YYYY/MM/DD		
		Integer month = null;
		Integer dayOfMonth = null;
		Integer year = null;
		if (date != null) {
			String[] dateParts = date.split("/");
			for (int i = 0; i < dateParts.length; i++) {
				String s = dateParts[i].replace(" ", "");
				if (dateParts[i].length() == 1 || dateParts[i].length() == 2) {
					if (month == null) {
						month = Integer.parseInt(dateParts[i]);
					}else if (dayOfMonth == null){
						dayOfMonth = Integer.parseInt(dateParts[i]);
					}else if (year == null) {
						year = Integer.parseInt(dateParts[i]);
					}else {
						return (Calendar)nullCalendar.clone();
					}
				}else {
					year = Integer.parseInt(dateParts[i]);
				}
			}
		}
		
		if (month == null || dayOfMonth == null || year == null) {
			return (Calendar)nullCalendar.clone();
		}else {
			cal.set(Calendar.MONTH, month);
			cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			cal.set(Calendar.YEAR, year);
		}
		
		//Time
		if (time != null) {
			String[] timeParts = time.split(":");
			Integer hours = Integer.parseInt(timeParts[0]);
			Integer minutes = Integer.parseInt(timeParts[1]);
			Integer seconds = 0;
			try {
				seconds = Integer.parseInt(timeParts[2]);
			}catch(IndexOutOfBoundsException e) { //If only minutes and hours are specified
				seconds = 0;
			}
			
			cal.set(Calendar.HOUR, hours);
			cal.set(Calendar.MINUTE, minutes);
			cal.set(Calendar.SECOND, seconds);
		}
		
		//AM / PM
		if (ampm != null) {
			cal.set(Calendar.AM_PM, ampm);
		}
		
		System.out.println("VALID Calendar");
		return cal;
	}
	
	public static Calendar getDateFromQvxReal(double daysSince) {
		
		long fullDaysSince = Math.round(daysSince);
		double partialDaysSince = daysSince - fullDaysSince;
		long remainingSeconds = Math.round(partialDaysSince*SECONDS_PER_DAY);
		
		long dateInMilliseconds = (START_DATE.getTime()-EPOCH.getTime()) +
				(long)(fullDaysSince*MILLISECONDS_PER_DAY) +
				(long)(remainingSeconds*1000);
		
		Date date = new Date(dateInMilliseconds);
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("EDT"));
		cal.setTime(date);
		//cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)-1);
		System.out.println("AM or PM?\t" + cal.get(Calendar.AM_PM));
		
		if (cal.get(Calendar.AM_PM) == Calendar.PM) {
			//Note: AM/PM value not supported by "DateTime" cell
		}
		
		return resolveDateOffset(cal);
	}
	
	public static String objectToString(Object obj) {
		
		if (obj.getClass().equals(java.lang.String.class)) {
			return (String)obj;
		}else if (obj.getClass().equals(java.lang.Double.class)){
			/*//Double refers to a date; convert the date to string form
			Calendar cal = getDateFromQvxReal((double)obj);
			String month = Integer.toString(cal.get(Calendar.MONTH));
			String dayOfMonth = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
			String year = Integer.toString(cal.get(Calendar.YEAR));
			return month + "/" + dayOfMonth + "/" + year;*/
			return Double.toString((double)obj);
		}else {
			return "";
		}
	}
	
	public static String removeSuffix(String s, String suffix) {
		if (s.endsWith(suffix)) {
			return s.substring(0, s.lastIndexOf(suffix));
		}else {
			return s;
		}
	}
	
	public static Calendar resolveDateOffset(Calendar cal) {
		
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int dayOfMonth= cal.get(Calendar.DAY_OF_MONTH);
		
		if (year <= 1900 && ((month == 0) || (month == 1 && dayOfMonth < 28))) {
			//If the date is before February 28, 1900, the calendar will be off by one day
			cal.set(Calendar.DAY_OF_MONTH, dayOfMonth + 1);
		}
		
		return cal;
	}
	
	public static String toTitleCase(String s) {
		/* Return a copy of s with the first letter capitalized */
		
		if (s.length() == 0) {
			return s;
		}
		
		return ("" + s.charAt(0)).toUpperCase() + s.substring(1, s.length());
	}
}
