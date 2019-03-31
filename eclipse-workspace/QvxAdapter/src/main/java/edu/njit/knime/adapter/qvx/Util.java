package main.java.edu.njit.knime.adapter.qvx;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Util {

public static String[][] csvTo2DArray(String fileName) {
		
		BufferedReader inFile = null;
		try {
			inFile = new BufferedReader(new FileReader(fileName));
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		List<String[]> rows = new ArrayList<String[]>();
		int numFields = 0;
		try {
			//Read the first row of the csv and determine how many fields there are
			String[] fieldNames = inFile.readLine().split(",");
			numFields = fieldNames.length;
			rows.add(fieldNames);
			
			//Read all other lines from the csv
			String currLine = "";
			while ((currLine = inFile.readLine()) != null) {
				String[] values = currLine.split(",");
				if (values.length != numFields) {
					throw new IOException("Number of columns is not the same for each row");
				}
				rows.add(values);
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		//Convert "rows" to 2D array
		String[][] returnVal = new String[rows.size()][numFields];
		for(int i = 0; i < rows.size(); i++) {
			String[] row = rows.get(i);
			for(int j = 0; j < row.length; j++) {
				returnVal[i][j] = row[j];
			}
		}		
		return returnVal;
	}
}
