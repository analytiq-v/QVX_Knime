package practiceAndTesting;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.njit.util.Util;

public class Miscellaneous {

	
	
	public static void main(String[] argv) {
		System.out.println(regexTest(new String[] {"a2000/02/02"}));
		System.out.println(regexTest(new String[] {"200-02-02"}));
		System.out.println(regexTest(new String[] {"20-02-2000454"}));
	}
	
	public static Calendar[] regexTest(Object[] objs) {
		
		/* If each item in objs can be converted into a Calendar date, return a list of all these
		 * Calendar dates. Otherwise, return null to signify that conversion failed (meaning that
		 * objs should be kept in the same form it was in before this method was called.
		 */
		
		String dateSeps = "-/";
		String formatA = "^[0-9]{1,2}[" + dateSeps + "][0-9]{1,2}[" + dateSeps + "][0-9]{4}$";
		String formatB = "^[0-9]{4}[" + dateSeps + "][0-9]{1,2}[" + dateSeps + "][0-9]{1,2}$";
		
		Pattern patternA = Pattern.compile(formatA);
		Pattern patternB = Pattern.compile(formatB);
		
		Matcher matcherA = null;
		Matcher matcherB = null;
		
		Calendar[] calendars = new Calendar[objs.length];
		for(int i = 0; i < objs.length; i++) {
			if (objs[i] == null) {
				continue;
			}
			
			String s = null;
			try {
				s = (String)objs[i];
			}catch(ClassCastException e) {
				return null;
			}
			
			matcherA = patternA.matcher(s);
			matcherB = patternB.matcher(s);
			boolean matchA = matcherA.find();
			boolean matchB = matcherB.find();
			
			if (matchA || matchB) {
				int month = 0;
				int dayOfMonth = 0;
				int year = 0;
				if (matchA) {
					String sep = "" + s.charAt(2);
					String[] dateParts = s.split(sep);
					month = Integer.parseInt(dateParts[0]);
					dayOfMonth = Integer.parseInt(dateParts[1]);
					year = Integer.parseInt(dateParts[2]);
				}else if (matchB) {
					String sep = "" + s.charAt(2);
					String[] dateParts = s.split(sep);
					year = Integer.parseInt(dateParts[0]);
					month = Integer.parseInt(dateParts[1]);
					dayOfMonth = Integer.parseInt(dateParts[2]);
				}
				
				if(dayOfMonth > 31 || month > 12) {
					return null;
				}
				
				calendars[i] = Calendar.getInstance(TimeZone.getTimeZone("EDT"));
				calendars[i].set(Calendar.DAY_OF_MONTH, dayOfMonth);
				calendars[i].set(Calendar.MONTH, month);
				calendars[i].set(Calendar.YEAR, year);
			}else {
				return null;
			}
		}
		return calendars;
	}
}