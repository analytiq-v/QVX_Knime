package edu.njit.util;

public class Util {
	
	public static void checkNotNull(Object obj, String name) {
    	if (obj != null) {
    		System.out.println(name + " has a value"); 
    	}else {
    		throw new RuntimeException(name + "is null!");
    	}
    }
	
	public static String removeSuffix(String s, String suffix) {
		if (s.endsWith(suffix)) {
			return s.substring(0, s.lastIndexOf(suffix));
		}else {
			return s;
		}
	}
}
