package edu.njit.qvx;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigInteger;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.MissingCell;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.date.DateAndTimeCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;

import edu.njit.qvx.FieldAttrType;
import edu.njit.qvx.FieldAttributes;
import edu.njit.qvx.QvxFieldExtent;
import edu.njit.qvx.QvxFieldType;
import edu.njit.qvx.QvxNullRepresentation;
import edu.njit.qvx.QvxTableHeader;
import edu.njit.qvx.QvxTableHeader.Fields.QvxFieldHeader;

import static edu.njit.qvx.FieldAttrType.DATE;
import static edu.njit.qvx.FieldAttrType.FIX;
import static edu.njit.qvx.FieldAttrType.INTERVAL;
import static edu.njit.qvx.FieldAttrType.MONEY;
import static edu.njit.qvx.FieldAttrType.REAL;
import static edu.njit.qvx.FieldAttrType.TIME;
import static edu.njit.qvx.FieldAttrType.TIMESTAMP;
import static edu.njit.qvx.QvxFieldType.QVX_QV_DUAL;
import static edu.njit.qvx.QvxFieldType.QVX_SIGNED_INTEGER;
import static edu.njit.qvx.QvxFieldType.QVX_UNSIGNED_INTEGER;
import static edu.njit.qvx.QvxQvSpecialFlag.QVX_QV_SPECIAL_DOUBLE;
import static edu.njit.qvx.QvxQvSpecialFlag.QVX_QV_SPECIAL_DOUBLE_AND_STRING;
import static edu.njit.qvx.QvxQvSpecialFlag.QVX_QV_SPECIAL_INT;
import static edu.njit.qvx.QvxQvSpecialFlag.QVX_QV_SPECIAL_INT_AND_STRING;
import static edu.njit.qvx.QvxQvSpecialFlag.QVX_QV_SPECIAL_NULL;
import static edu.njit.qvx.QvxQvSpecialFlag.QVX_QV_SPECIAL_STRING;
import static edu.njit.qvxreader.Util.getDateFromQvxReal;
import static edu.njit.qvxreader.Util.getDateFromString;
import static edu.njit.qvxreader.Util.getTimeFromString;
import static edu.njit.qvxreader.Util.objectToString;

public class QvxBinaryReader {

	private final byte FS_BYTE = 0x1C;
	private final byte RS_BYTE = 0x1E;
	private final int WEB_STREAM_MAX_SIZE = 16384;
	
	private QvxTableHeader qvxTableHeader;
	private List<QvxFieldHeader> fieldHeaders;
	private String inFileName;
	private InputStream inputStream;
	private ExecutionContext exec;
	private String[] fieldNames;
	private String xmlString;
	private byte[] buffer;
	private int bufferSize;
	private int bufferIndex = 0;
	private int zeroByteIndex;
	private List<Object[]> data = new ArrayList<>();
	
	/*For each field, this value is true if it should be stored in KNIME as a date,
	  false if it should be stored as time or some other format, and null if neither date nor time */
	private Boolean fieldUsesDate[];
	
	QvxBinaryReader(){
		
	}
	
	BufferedDataTable[] readQvx(String inFileName, ExecutionContext exec) {
		
		this.inFileName = inFileName;
		this.exec = exec;
		
		readQvxTableHeader();
		readBody();
		
		fieldUsesDate = new Boolean[data.size()];
		
		return new BufferedDataTable[] {dataToDataTable()};
	}
	
	private boolean attemptConversionToDateOrTime(int column) {
		
		return attemptConvertColumnToDate(column) || attemptConvertColumnToTime(column);
	}
		
	private boolean attemptConvertColumnToDate(int column) {
		
		/* If every non-null item in data[:,column] can be converted into a Calendar date, do the
		 * conversions and return true. Otherwise, return false, without doing any of the conversions.
		 */
		
		Calendar[] calendars = new Calendar[data.size()];
		for(int i = 0; i < data.size(); i++) {
			Object dataPt = data.get(i)[column];
			if (dataPt == null) { //Ignore empty dates
				calendars[i] = null;
				continue;
			}
			
			String s = null;
			if (dataPt.getClass() == java.lang.String.class) {
				s = (String)dataPt;
				calendars[i] = getDateFromString(s);
			}else{
				return false;
			}
			
			if (calendars[i] == null) {
				return false;
			}
		}	
		
		/*If the program gets to this point, it means that all data points could be successfully converted
		 * to Calendars. Therefore, assign all of the data points a Calendar value
		 */
		for(int i = 0; i < data.size(); i++) {
			data.get(i)[column] = calendars[i];
		}
		
		fieldUsesDate[column] = true;
		return true;
	}
		
	private boolean attemptConvertColumnToTime(int column) {
		
		/* If every non-null item in data[:,column] can be converted into a Calendar time, do the
		 * conversions and return true. Otherwise, return false, without doing any of the conversions.
		 */
		
		Calendar[] calendars = new Calendar[data.size()];
		for(int i = 0; i < data.size(); i++) {
			Object dataPt = data.get(i)[column];
			if (dataPt == null) { //Ignore empty dates
				calendars[i] = null;
				continue;
			}
			
			String s = null;
			if (dataPt.getClass() == java.lang.String.class) {
				s = (String)dataPt;
				calendars[i] = getTimeFromString(s);
			}else{
				return false;
			}
			
			if (calendars[i] == null) {
				return false;
			}
		}
		
		/*If the program gets to this point, it means that all data points could be successfully converted
		 * to Calendars. Therefore, assign all of the data points a Calendar value
		 */
		for(int i = 0; i < data.size(); i++) {
			data.get(i)[column] = calendars[i];
		}
		
		fieldUsesDate[column] = false;
		return true;
	}
	
	private BufferedDataTable dataToDataTable() {
		
		//Convert object array (which was read from qvx file) into a KNIME data table
		
		int numRows = data.size();
		int numCols = data.get(0).length;
		
		DataType[] dataTypes = new DataType[fieldNames.length];
		
		//Cache the FieldAttrType for each field
		FieldAttrType[] fieldAttrTypes = new FieldAttrType[fieldHeaders.size()];
		for(int i = 0; i < fieldHeaders.size(); i++) {
			FieldAttributes fieldFormat = fieldHeaders.get(i).getFieldFormat();
			if (fieldFormat != null) {
				fieldAttrTypes[i] = fieldFormat.getType();
			}
		}
		
		for(int i = 0; i < fieldNames.length; i++) {
			
			//Get the first non-null value in data
			int j = 0;
			Object dataPt;
			try {
				while((dataPt = data.get(j)[i]) == null) {
					j += 1;
				}
			}catch(IndexOutOfBoundsException e) {
				throw new IndexOutOfBoundsException("All values in column \"" + fieldNames[i] + "\" are" +
						" null");
			}
			
			//Get the type of dataPt and create the appropriate cell type
			Class dataClass = dataPt.getClass();
			if(dataClass.equals(java.lang.Double.class) || usesFixedPointDecimals(fieldHeaders.get(i))) {
				dataTypes[i] = isIntegerColumn(i) ? IntCell.TYPE : DoubleCell.TYPE;
			}else if (usesFixedPointDecimals(fieldHeaders.get(i))) {
				dataTypes[i] = DoubleCell.TYPE;
			}else if(dataClass.equals(java.lang.Integer.class)){
				dataTypes[i] = IntCell.TYPE;
			}else if(dataClass.equals(java.lang.String.class)) {
				dataTypes[i] = attemptConversionToDateOrTime(i) ? DateAndTimeCell.TYPE : StringCell.TYPE;
			}else if(dataClass.equals(java.util.GregorianCalendar.class)){
				dataTypes[i] = DateAndTimeCell.TYPE;
			}else {
				throw new RuntimeException("Unknown data type: " + dataClass);
			}
		}
		
		//Start creating the KNIME data by iterating through the data
		DataColumnSpec[] columnSpecs = DataTableSpec.createColumnSpecs(fieldNames, dataTypes);
		DataTableSpec spec = new DataTableSpec(columnSpecs);
		BufferedDataContainer buf = exec.createDataContainer(spec);
		for (int i = 0; i < numRows; i++) {
		    DataCell[] cells = new DataCell[numCols];
		    for (int j = 0; j < numCols; j++) {
		    	Object dataPt = data.get(i)[j];
		    	if (dataPt == null) {
		    		cells[j] = new MissingCell("");
		    		continue;
		    	}

		    	if (dataTypes[j].equals(IntCell.TYPE)) {
		    		cells[j] = new IntCell((int)dataPt);
		    	}else if(dataTypes[j].equals(DoubleCell.TYPE)) {
		    		cells[j] = new DoubleCell((double)dataPt);
		    	}else if(dataTypes[j].equals(StringCell.TYPE)) {
		    		cells[j] = new StringCell(objectToString(dataPt));
		    	}else if(dataTypes[j].equals(DateAndTimeCell.TYPE)) {
		    		Calendar cal = (Calendar)dataPt;
		    		cells[j] = getCorrectDateAndTimeCell(cal, fieldAttrTypes[j], j);
				}else {
					throw new RuntimeException("Unknown data type: " + dataTypes[j]);

				}
		    }
		    DataRow row = new DefaultRow("Row_" + i, cells);
		    buf.addRowToTable(row);
		}
		buf.close();
		BufferedDataTable table = buf.getTable();

		return table;
	}
	
	private boolean isIntegerColumn(int column) {
		
		/* If every non-null item in data[:,column] can be converted into a an integer value, do the
		 * conversions and return true. Otherwise, return false, without doing any of the conversions.
		 */
		
		for(int i = 0; i < data.size(); i++) {
			try {
				Object obj = data.get(i)[column];
				if (obj == null) {
					continue;
				}
				
				double dValue = (double)obj;
				int iValue = (int)dValue;
				if (dValue != iValue) {
					return false;
				}
			}catch(ClassCastException e) {
				return false;
			}
		}
		
		//Cast all of the double values to integers
		for(int i = 0; i < data.size(); i++) {
			Object obj = data.get(i)[column];
			if (obj == null) {
				continue;
			}
			
			double dValue = (double)obj;
			int iValue = (int)dValue;
			data.get(i)[column] = iValue;
		}
		
		return true;
	}
	
	private void readQvxTableHeader() {
		
		/* Reads the entire "inFileName", creates QvxTableHeader object, and stores the location of the
		 * zero-byte
		 */
		
		// Read the entire "inFileName" into a buffer, from either the local file system or internet
		inputStream = null;
		try {
			
			try { //Try attempting to read file from local file system
				inputStream = new FileInputStream(inFileName);
				
				//Set bufferSize
				File f = new File(inFileName);
				bufferSize = (int)f.length();
				if (bufferSize < f.length()) {
					// If bufferSize is not appropriate; theoretically, should never happen
					inputStream.close();
					throw new RuntimeException("Expected file size does not match actual size");
				}
				
				//Read "inFileName" into "buffer"
				buffer = new byte[bufferSize];
				int bytesRead = inputStream.read(buffer);
				if (bytesRead != bufferSize || inputStream.read() != -1) {
					// If bufferSize is not appropriate; theoretically, should never happen
					inputStream.close();
					throw new RuntimeException("Expected file size does not match actual size");
				}
			}catch(FileNotFoundException e) {
				//Try finding the file on the internet
				try{
					if (inFileName.startsWith("http")) {
						inputStream = new BufferedInputStream(new URL(inFileName).openStream());
						
						/*Keep reading bytes from inputStream until the inputStream ends; store these bytes
						in "bytes", then add every item in bytes to a large "buffer" array. This simulates
						the act of reading the entire file into one buffer with a single read statement.*/
						
						List<byte[]> bytes = new ArrayList<byte[]>();
						int totalLength = 0;
						int numRead = 0;
						int previousRead = 0;
						byte[] currBuffer = new byte[WEB_STREAM_MAX_SIZE];
						while ((numRead = inputStream.read(currBuffer)) != -1) {
							previousRead = numRead;
							bytes.add(currBuffer.clone());
							totalLength += numRead;
						}
						bytes.set(bytes.size()-1, Arrays.copyOfRange(
							bytes.get(bytes.size()-1), 0, previousRead));		
						bufferSize = totalLength;
						buffer = new byte[totalLength];
						
						int idx = 0;
						for(int i = 0; i < bytes.size(); i++) {
							currBuffer = bytes.get(i);
							for (int j = 0; j < currBuffer.length; j++) {
								buffer[idx++] = currBuffer[j];
							}
						}						
					}else {
						throw new FileNotFoundException("File not found: " + inFileName);
					}
				}catch(FileNotFoundException e2) {
					throw new FileNotFoundException("File not found: " + inFileName);
				}
			}
			
			// Extract the xml portion of buffer
			zeroByteIndex = indexOf(buffer, (byte)0);
			xmlString = byteArrayToString(Arrays.copyOfRange(buffer, 0, zeroByteIndex));
			
			// Unmarshal "xmlString" into a QvxTableHeader object
			JAXBContext jaxbContext;
			jaxbContext = JAXBContext.newInstance( QvxTableHeader.class );
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			qvxTableHeader = (QvxTableHeader) jaxbUnmarshaller.unmarshal(
					new StreamSource(new StringReader(xmlString)));
			
			//Add field names to "data"
			fieldHeaders = qvxTableHeader.getFields().getQvxFieldHeader();
			fieldNames = new String[fieldHeaders.size()];
			for(int i = 0; i < fieldHeaders.size(); i++) {
				fieldNames[i] = fieldHeaders.get(i).getFieldName();
			}	
			inputStream.close();
		}
		catch(IOException e) { e.printStackTrace(); }
		catch(JAXBException e) { e.printStackTrace(); }
	}
	
	private void readBody() {
		
		/* Reads the body of the qvx file and populates "data"
		 */
				
		boolean usesSeparatorByte = qvxTableHeader.isUsesSeparatorByte();
		int endOfRecords = usesSeparatorByte ? buffer.length - 1 : buffer.length;
		
		int numFields = qvxTableHeader.getFields().getQvxFieldHeader().size();
		bufferIndex = zeroByteIndex + 1; //Starting index of the body
		
		//Keep reading until the end of the body is reached
		while(bufferIndex < endOfRecords) {
			
			Object[] row = new Object[numFields];
			
			//Check for record separator byte if required
			if (usesSeparatorByte) {
				if (readBytesFromBuffer(1)[0] != RS_BYTE) {
					throw new RuntimeException("Record separator byte is expected");
				}
			}
			
			//Read each field in the row
			for(int i = 0; i < numFields; i++) {
				QvxFieldHeader fieldHeader = qvxTableHeader.getFields().getQvxFieldHeader().get(i);
				QvxFieldType fieldType = fieldHeader.getType();
				if (!fieldType.equals(QVX_QV_DUAL)) {
					QvxNullRepresentation nullRepresentation = fieldHeader.getNullRepresentation();
					switch(nullRepresentation) {
					
					case QVX_NULL_FLAG_SUPPRESS_DATA:
						byte nullFlag = readBytesFromBuffer(1)[0];
						if (nullFlag == 0) {
							row[i] = readValueFromBuffer(fieldHeader);
						}else if (nullFlag == 1) { //Null flag of 1 means a field value is not used
							row[i] = null;
						}else {
							throw new RuntimeException("Unrecognized QVX_NULL_FLAG_SUPPRESS_DATA flag: " +
									nullFlag);
						}
						break;
					case QVX_NULL_NEVER:
						row[i] = readValueFromBuffer(fieldHeader);
						break;
					default:
						throw new RuntimeException("Unrecognized null representation: " + nullRepresentation);
					}
				} else { //QVX_QV_DUAL is used; readValueFromBuffer deals with the QvxQvSpecialFlag
					row[i] = readValueFromBuffer(fieldHeader);
				}
			}
			data.add(row);
		}
		
		//Check for file separator byte if required
		if (usesSeparatorByte) {
			if (readBytesFromBuffer(1)[0] != FS_BYTE) {
				throw new RuntimeException("File separator byte is expected");
			}
		}
	}
	
	private Object readValueFromBuffer(QvxFieldHeader fieldHeader) {
		
		int byteWidth = fieldHeader.getByteWidth().intValue();
		ByteOrder byteOrder = fieldHeader.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
		
		switch (fieldHeader.getType()) {
			case QVX_BLOB:
				throw new RuntimeException(
					"Problem with input file: Qvx Reader does not support QvxFieldType: QVX_BLOB");
			case QVX_IEEE_REAL:
				if (byteWidth == 4) {
					Float f = ByteBuffer.wrap(readBytesFromBuffer(4)).order(byteOrder).getFloat();
					return (float)applyFieldAttr(f, fieldHeader);
				}else if(byteWidth == 8) {
					Double d = ByteBuffer.wrap(readBytesFromBuffer(8)).order(byteOrder).getDouble();
					return applyFieldAttr(d, fieldHeader);
				}
			case QVX_PACKED_BCD:
				throw new RuntimeException(	
					"Problem with input file: Qvx Reader does not support QvxFieldType: QVX_PACKED_BCD");
			case QVX_UNSIGNED_INTEGER:
			case QVX_SIGNED_INTEGER:
				if(byteWidth == 2) {
					short s = ByteBuffer.wrap(readBytesFromBuffer(2)).order(byteOrder).getShort();
					return (short)applyFieldAttr(s, fieldHeader);
				}else if(byteWidth == 4) {
					int i = ByteBuffer.wrap(readBytesFromBuffer(4)).order(byteOrder).getInt();
					return applyFieldAttr(i, fieldHeader);
				}else if(byteWidth == 8) {
					long l = ByteBuffer.wrap(readBytesFromBuffer(8)).order(byteOrder).getLong();
					return (long)applyFieldAttr(l, fieldHeader);
				}
			case QVX_TEXT:
				return bufferToString_zeroTerminated();
			case QVX_QV_DUAL:
				if(fieldHeader.getExtent() == QvxFieldExtent.QVX_QV_SPECIAL) {
					return readQvDualBytes(fieldHeader);
				}else {
					throw new RuntimeException("Fields of type QVX_QV_DUAL must use field extent" +
							"QVX_QV_SPECIAL");
				}
			default:
				throw new RuntimeException("Unrecognized field type of " + fieldHeader.getType());
		}
	}
	
	private Object readQvDualBytes(QvxFieldHeader fieldHeader) {
		
		byte flag = readBytesFromBuffer(1)[0];
		if (flag == QVX_QV_SPECIAL_NULL.getValue()) {
			return null;
			
		}else if(flag == QVX_QV_SPECIAL_INT.getValue()) {
			throw new RuntimeException(
				"Problem with input file: Qvx Reader does not support QvxSpecialFlag: " +
					QVX_QV_SPECIAL_INT.getValue());
			
		}else if(flag == QVX_QV_SPECIAL_DOUBLE.getValue()){
			ByteOrder byteOrder = fieldHeader.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
			Object value = ByteBuffer.wrap(readBytesFromBuffer(8)).order(byteOrder).getDouble();
			return applyFieldAttr(value, fieldHeader);
			
		}else if(flag == QVX_QV_SPECIAL_STRING.getValue()) {
			return applyFieldAttr(bufferToString_zeroTerminated(), fieldHeader);
			
		}else if(flag == QVX_QV_SPECIAL_INT_AND_STRING.getValue()) {
			throw new RuntimeException(
					"Problem with input file: Qvx Reader does not support QvxSpecialFlag: " +
						QVX_QV_SPECIAL_INT_AND_STRING.getValue());
			
		}else if(flag == QVX_QV_SPECIAL_DOUBLE_AND_STRING.getValue()) {
			readBytesFromBuffer(8); //Skip the "double" part of this value; it is not used
			int oldBufferIndex = bufferIndex;
			try {
				return applyFieldAttr(Double.parseDouble(bufferToString_zeroTerminated()), fieldHeader);
			}catch(NumberFormatException e) { //If it is not a number
				bufferIndex = oldBufferIndex;
				return applyFieldAttr(bufferToString_zeroTerminated(), fieldHeader);
			}
		}
		
		throw new RuntimeException("Unknown QvxQvSpecialFlag: " + flag);
	}
	
	//Helper methods ------------------------------------
	
	private String byteArrayToString(byte[] bytes) {
		
		/* Converts each byte in bytes into a character, then concatenates all of these characters
		 */
		
		String s = "";
		for(int i = 0; i < bytes.length; i++) {
			s += (char)bytes[i];
		}
		return s;
	}
	
	private int indexOf(byte[] bytes, byte search) {
		
		/* Returns the index of the first occurrence of "search" in bytes
		 * */
		
		for(int i = 0; i < bytes.length; i++) {
			if (bytes[i] == search) {
				return i;
			}
		}
		return -1;
	}
	
	private byte[] readBytesFromBuffer(int n) {
		
		// Read n bytes from buffer, and set bufferIndex to the index of the next data in the buffer
		byte[] bytes = Arrays.copyOfRange(buffer, bufferIndex, bufferIndex + n);
		bufferIndex += n;
		
		return bytes;
	}
	
	private String bufferToString_zeroTerminated() {
		
		String s = "";
		byte b;
		while ((b = buffer[bufferIndex++]) != 0 ) {
			s += (char)b;
		}
		
		return s;
	}
	
	private Object applyFieldAttr(Object data, QvxFieldHeader fieldHeader) {
				
		Object returnVal = null;
		QvxFieldType type = fieldHeader.getType();
		
		//If there is no formatting for this field, return the original data
		if (fieldHeader.getFieldFormat() == null || fieldHeader.getFieldFormat().getType() == null) {
			returnVal = data;
		}else {	
			FieldAttrType fieldAttrType = fieldHeader.getFieldFormat().getType();			
			if (fieldAttrType == FIX || fieldAttrType == REAL) {
				
				try {
					data = (double)data;
				}catch(ClassCastException e) {
					//Type of REAL/FIX/MONEY does not necessarily mean data is a number
					return data;
				}
				
				int nDec = 0; //nDec is 0 if not specified
				if (fieldHeader.getFieldFormat().getNDec() != null) {
					nDec = fieldHeader.getFieldFormat().getNDec().intValue();
				}
				returnVal = Math.round((double)data * Math.pow(10, nDec))/Math.pow(10, nDec);
				
			}else if (fieldAttrType == MONEY) {
				
				try {
					data = (double)data;
				}catch(ClassCastException e) {
					//Type of REAL/FIX/MONEY does not necessarily mean data is a number
					return data;
				}
				returnVal = Math.round((double)data * 100.0) / 100.0;
			}else if (fieldAttrType == DATE || fieldAttrType == INTERVAL || fieldAttrType == TIME
					|| fieldAttrType == TIMESTAMP ) {
				
				if (type == QVX_QV_DUAL) { 
					Calendar cal = null;
					try {
						//It is expected that value is a double that represents days since 1900
						cal = getDateFromQvxReal((double)data);
					}catch(ClassCastException e1) {
						try {
							cal = getDateFromString((String)data);
						}catch(ClassCastException e2) {
							return null;
						}
					}catch(Exception e) {
						e.printStackTrace();
					}
					
					return cal;
				}else {
					System.out.println("WARNING: Unimplemented QvxFieldType-FieldAttrType combination: " +
							type + ", " + fieldAttrType + "; This field will not be stored as a " +
							fieldAttrType);
					return data;
				}
			}else {
				returnVal = data;
			}
		}
		
		//Apply fixPointDecimals if it has a non-zero integer value
		QvxFieldType fieldType = fieldHeader.getType();
		if (fieldType == QVX_SIGNED_INTEGER || fieldType == QVX_UNSIGNED_INTEGER
			|| fieldType == QVX_QV_DUAL)
		{
			if (usesFixedPointDecimals(fieldHeader)) {
				int fixPointDecimals = fieldHeader.getFixPointDecimals().intValue();
				if (returnVal.getClass().equals(java.lang.Integer.class)) {
					double dReturnVal = Double.parseDouble(Integer.toString((int)returnVal));
					return dReturnVal / Math.pow(10.0, fixPointDecimals);
				}else if (returnVal.getClass().equals(java.lang.Double.class)) {
					return (double)returnVal / Math.pow(10.0, fixPointDecimals);
				}
			}
		}
		return returnVal;
	}
	
	private boolean usesFixedPointDecimals(QvxFieldHeader fieldHeader) {
		BigInteger fixPointDecimals = fieldHeader.getFixPointDecimals();
		return fixPointDecimals != null && fixPointDecimals.intValue() != 0;
	}
	
	private DataCell getCorrectDateAndTimeCell(
		Calendar cal, FieldAttrType fieldAttrType, int column)
	{
		
		if (fieldAttrType.equals(DATE) || (fieldUsesDate[column] != null && fieldUsesDate[column])) {
			if (cal.get(Calendar.YEAR) < 1900) { // Dates less than 1900 are not supported by this class
				return new MissingCell("");
			}else {
				return new DateAndTimeCell(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
					cal.get(Calendar.DAY_OF_MONTH));
			}
		}else if (fieldAttrType.equals(TIMESTAMP)) {
			if (cal.get(Calendar.YEAR) < 1900) { // Dates less than 1900 are not supported by this class
				return new DateAndTimeCell(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),
					cal.get(Calendar.SECOND), cal.get(Calendar.MILLISECOND));
			}else {
				return new DateAndTimeCell(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
					cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),
					cal.get(Calendar.SECOND), cal.get(Calendar.MILLISECOND));
			}
		}else if (fieldAttrType.equals(INTERVAL) || fieldAttrType.equals(TIME)
					|| (fieldUsesDate[column] != null  && !fieldUsesDate[column])) {
			return new DateAndTimeCell(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),
					cal.get(Calendar.SECOND), cal.get(Calendar.MILLISECOND));
		}else {
			throw new RuntimeException("Unimplemented field attribute type: " + fieldAttrType);
		}
	}
}
