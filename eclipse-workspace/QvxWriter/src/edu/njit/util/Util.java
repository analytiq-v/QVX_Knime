package edu.njit.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Util {
	
	public static void checkNotNull(Object obj, String name) {
    	if (obj != null) {
    		System.out.println(name + " has a value"); 
    	}else {
    		throw new RuntimeException(name + "is null!");
    	}
    }
}