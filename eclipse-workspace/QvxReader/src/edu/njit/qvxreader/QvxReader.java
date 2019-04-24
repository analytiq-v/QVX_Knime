package edu.njit.qvxreader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
import edu.njit.qvx.QvxQvSpecialFlag;
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
import static edu.njit.util.Util.getDateFromQvxReal;

public class QvxReader {

	private final byte FS_BYTE = 0x1C;
	private final byte RS_BYTE = 0x1E;
	
	private QvxTableHeader qvxTableHeader;
	private List<QvxFieldHeader> fieldHeaders;
	private String inFileName;
	private ExecutionContext exec;
	private String[] fieldNames;
	private String xmlString;
	private byte[] buffer;
	private int bufferSize;
	private int bufferIndex = 0;
	private int zeroByteIndex;
	private List<Object[]> data = new ArrayList<>();
		
	QvxReader(){
		
	}
	
	BufferedDataTable[] readQvx(QvxReaderNodeSettings settings, ExecutionContext exec) {
		
		this.inFileName = settings.getFileName();
		this.exec = exec;
		
		readQvxTableHeader();
		readBody();
		
		System.out.println(Arrays.toString(fieldNames));
		for(int i = 0; i < data.size(); i++) {
			System.out.println(Arrays.toString(data.get(i)));
		}
		
		return new BufferedDataTable[] {dataToDataTable()};
	}
	
	private BufferedDataTable dataToDataTable() {
		
		System.out.println("Converting to data table...");
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
				dataTypes[i] = DoubleCell.TYPE;
			}else if(dataClass.equals(java.lang.Integer.class)){
				dataTypes[i] = IntCell.TYPE;
			}else if(dataClass.equals(java.lang.String.class)) {
				dataTypes[i] = StringCell.TYPE;
			}else if(dataClass.equals(java.util.GregorianCalendar.class)){
				//Check if it is date/date-time
				dataTypes[i] = DateAndTimeCell.TYPE;
			}else {
				throw new RuntimeException("Unknown data type: " + dataClass);
			}
		}
		
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
		    	Class dataClass = dataPt.getClass();
		    	if (dataClass.equals(java.lang.Integer.class)) {
		    		cells[j] = new IntCell((int)dataPt);
		    	}else if(dataClass.equals(java.lang.Double.class)) {
		    		cells[j] = new DoubleCell((double)dataPt);
		    	}else if(dataClass.equals(java.lang.String.class)) {
		    		cells[j] = new StringCell((String)dataPt);
		    	}else if(dataClass.equals(java.util.GregorianCalendar.class)){
		    		Calendar cal = (Calendar)dataPt;
		    		System.out.println("Calendar: " + cal.getTime());
		    		cells[j] = getCorrectDateAndTimeCell(cal, fieldAttrTypes[j]);
				}else {
					throw new RuntimeException("Unknown data type: " + dataClass);

				}
		    }
		    DataRow row = new DefaultRow("RowKey_" + i, cells);
		    buf.addRowToTable(row);
		}
		buf.close();
		BufferedDataTable table = buf.getTable();

		return table;
	}
	
	private void readQvxTableHeader() {
		
		/* Reads the entire "inFileName", creates QvxTableHeader object, and stores the location of the
		 * zero-byte
		 */
		
		System.out.println("reading from qvx table header...");

		FileInputStream inputStream = null;
		try {
			// Read the entire "inFileName" into a byte[] buffer
			
			//Set bufferSize
			File f = new File(inFileName);
			bufferSize = (int)f.length();
			if (bufferSize < f.length()) { //The case when narrowing conversion causes loss of data
				throw new RuntimeException("File too big");
			}
			
			//Read "inFileName" into "buffer"
			inputStream = new FileInputStream(inFileName);
			buffer = new byte[bufferSize];
			int bytesRead = inputStream.read(buffer);
			if (bytesRead != bufferSize || inputStream.read() != -1) { // If bufferSize is not appropriate; theoretically, should never happen
				inputStream.close();
				throw new RuntimeException("Buffer size is either too big or too small");
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
		catch(FileNotFoundException e) { e.printStackTrace(); }
		catch(IOException e) { e.printStackTrace(); }
		catch(JAXBException e) { e.printStackTrace(); }
	}
	
	private void readBody() {
		
		/* Reads the body of the qvx file and populates "data"
		 */
		
		System.out.println("reading from body...");
		
		boolean usesSeparatorByte = qvxTableHeader.isUsesSeparatorByte();
		int endOfRecords = usesSeparatorByte ? buffer.length - 1 : buffer.length;
		
		int numFields = qvxTableHeader.getFields().getQvxFieldHeader().size();
		bufferIndex = zeroByteIndex + 1; //Starting index of the body
		while(bufferIndex < endOfRecords) { //Keep reading until the end of the body is reached
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
				QvxNullRepresentation nullRepresentation = fieldHeader.getNullRepresentation();
				switch(nullRepresentation) {
				
				case QVX_NULL_FLAG_SUPPRESS_DATA:
					byte nullFlag = readBytesFromBuffer(1)[0];
					if (nullFlag == 0) {
						row[i] = readValueFromBuffer(fieldHeader);
						//System.out.println("value: " + row[i]);
					}else if (nullFlag == 1) { //Null flag of 1 means a field value is not used
						System.out.println("null value found");
						row[i] = null;
					}else {
						throw new RuntimeException("Unrecognized QVX_NULL_FLAG_SUPPRESS_DATA flag: " +
								nullFlag);
					}
					break;
				case QVX_NULL_NEVER:
					row[i] = readValueFromBuffer(fieldHeader);
					System.out.println("value: " + row[i]);
					break;
				default:
					throw new RuntimeException("Unrecognized null representation: " + nullRepresentation);
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
		
		System.out.println("reading value from buffer...");
		
		int byteWidth = fieldHeader.getByteWidth().intValue();
		ByteOrder byteOrder = fieldHeader.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
		
		switch (fieldHeader.getType()) {
			case QVX_BLOB:
				//TODO
				return null;
			case QVX_IEEE_REAL:
				if (byteWidth == 4) {
					Float f = ByteBuffer.wrap(readBytesFromBuffer(4)).order(byteOrder).getFloat();
					return (float)applyFieldAttr(f, fieldHeader);
				}else if(byteWidth == 8) {
					Double d = ByteBuffer.wrap(readBytesFromBuffer(8)).order(byteOrder).getDouble();
					return applyFieldAttr(d, fieldHeader);
				}
			case QVX_PACKED_BCD:
				//TODO
				return null;
			case QVX_UNSIGNED_INTEGER:
			case QVX_SIGNED_INTEGER:
				if (byteWidth == 1) {
					// TODO
				}else if(byteWidth == 2) {
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
		if (flag == QvxQvSpecialFlag.QVX_QV_SPECIAL_NULL.getValue()) {
			throw new RuntimeException("Coding error: Unimplemented QvxQvSpecialFlag: " + flag);
			
		}else if(flag == QvxQvSpecialFlag.QVX_QV_SPECIAL_INT.getValue()) {
			throw new RuntimeException("Coding error: Unimplemented QvxQvSpecialFlag: " + flag);
			
		}else if(flag == QvxQvSpecialFlag.QVX_QV_SPECIAL_DOUBLE.getValue()){
			ByteOrder byteOrder = fieldHeader.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
			Object value = ByteBuffer.wrap(readBytesFromBuffer(8)).order(byteOrder).getDouble();
			System.out.println("qvx_qv_special_double: original value: " + value);
			return applyFieldAttr(value, fieldHeader);
			
		}else if(flag == QvxQvSpecialFlag.QVX_QV_SPECIAL_STRING.getValue()) {
			return applyFieldAttr(bufferToString_zeroTerminated(), fieldHeader);
			
		}else if(flag == QvxQvSpecialFlag.QVX_QV_SPECIAL_INT_AND_STRING.getValue()) {
			throw new RuntimeException("Coding error: Unimplemented QvxQvSpecialFlag: " + flag);
			
		}else if(flag == QvxQvSpecialFlag.QVX_QV_SPECIAL_DOUBLE_AND_STRING.getValue()) {
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
		
		//System.out.println("applying field formatting...");
		
		Object returnVal = null;
		//Debugging code
		QvxFieldType type = fieldHeader.getType();
		System.out.print("Type: " + type);
		
		//If there is no formatting for this field, return the original data
		if (fieldHeader.getFieldFormat() == null || fieldHeader.getFieldFormat().getType() == null) {
			System.out.println(", Format: None");
			returnVal = data;
		}else {	
			FieldAttrType fieldAttrType = fieldHeader.getFieldFormat().getType();
			System.out.println(", Format: " + fieldAttrType);
			
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
					System.out.println("Qvx Dual date: " + data);
					Calendar cal = getDateFromQvxReal((double)data);
					System.out.println("Calendar: " + cal.getTime());
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
		if (fieldType == QVX_SIGNED_INTEGER || fieldType == QVX_UNSIGNED_INTEGER) {
			if (usesFixedPointDecimals(fieldHeader)) {
				if (returnVal.getClass().equals(java.lang.Integer.class)) {
					int fixPointDecimals = fieldHeader.getFixPointDecimals().intValue();
					System.out.println("integer with fixed pt found");
					double dReturnVal = Double.parseDouble(Integer.toString((int)returnVal));
					System.out.println("converted to double");
					return dReturnVal / Math.pow(10.0, fixPointDecimals);
				}
			}
		}
		return returnVal;
	}
	
	private boolean usesFixedPointDecimals(QvxFieldHeader fieldHeader) {
		BigInteger fixPointDecimals = fieldHeader.getFixPointDecimals();
		return fixPointDecimals != null && fixPointDecimals.intValue() != 0;
	}
	
	private DateAndTimeCell getCorrectDateAndTimeCell(Calendar cal, FieldAttrType fieldAttrType) {
		
		switch (fieldAttrType) {
		case DATE:
			System.out.println("Type is date");
			return new DateAndTimeCell(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
					cal.get(Calendar.DAY_OF_MONTH));
		case TIMESTAMP:
			System.out.println("Type is timestamp");
			return new DateAndTimeCell(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
					cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE),
					cal.get(Calendar.SECOND));
		case INTERVAL:
			System.out.println("Type is interval");
			return new DateAndTimeCell(cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE),
					cal.get(Calendar.SECOND), cal.get(Calendar.MILLISECOND));
		default:
			throw new RuntimeException("Unimplemented field attribute type: " + fieldAttrType);
		}
	}
}
