package practiceAndTesting;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.njit.util.Util;

public class Miscellaneous {

	
	
	public static void main(String[] argv) {
		Util.getDateFromQvxReal(5);
	}
	
	
	
	public static void fieldAttrTypeTest() {
		int c;
	}
	
	public static void regexTest() {
		String pattern = "[0-9]{1,4}[-/]([0-2][0-9])|([3][0-1])-/][0-9]{1,2}";
		Pattern r = Pattern.compile(pattern);
		
		String[] lines = {"2019-2-03", "212-09-09", "532-2-09"};
		for(String line : lines) {
			Matcher m = r.matcher(line);
			System.out.println(m.find());
		}
	}
}
