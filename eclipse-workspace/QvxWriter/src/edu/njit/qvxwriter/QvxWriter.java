package edu.njit.qvxwriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.node.BufferedDataTable;

import edu.njit.qvx.FieldAttrType;
import edu.njit.qvx.FieldAttributes;
import edu.njit.qvx.QvxFieldExtent;
import edu.njit.qvx.QvxFieldType;
import edu.njit.qvx.QvxNullRepresentation;
import edu.njit.qvx.QvxTableHeader;
import edu.njit.qvx.QvxTableHeader.Fields.QvxFieldHeader;
import edu.njit.util.Util;

import static edu.njit.qvx.QvxNullRepresentation.QVX_NULL_FLAG_SUPPRESS_DATA;
import static edu.njit.qvx.QvxNullRepresentation.QVX_NULL_NEVER;
import static edu.njit.util.Util.combineByteArrays;
import static edu.njit.util.Util.removeSuffix;

public class QvxWriter {
	
	private BufferedDataTable table;
	private String[] fieldNames;
	private String[][] data;
	private String outFileName;
	private QvxWriterNodeSettings settings;

	private QvxTableHeader tableHeader;
	private int bufferIndex = 0;
	private FileOutputStream outputStream;
	
	private static final int BUFFER_SIZE = (int)Math.pow(2, 20); //1 MB of memory used
	private static final byte RECORD_SEPARATOR = 0x1E;
	private static final byte FILE_SEPARATOR = 0x1C;
	private static final byte NUL = 0x00;
		
	public void writeQvxFile(BufferedDataTable table, String outFileName, QvxWriterNodeSettings settings) {
		
		this.table = table;
		this.fieldNames = table.getSpec().getColumnNames();
		this.data = dataTableToArray(this.table);
		this.outFileName = outFileName;
		this.settings = settings;
		
		System.out.println(Arrays.toString(fieldNames));
		for(int i = 0; i < data.length; i++) {
			System.out.println(Arrays.toString(data[i]));
		}
		
		configureTableHeader();
		writeTableHeader();
		writeBody();
	}
	
	private void configureTableHeader() {
		tableHeader = new QvxTableHeader();
		
    	tableHeader.setMajorVersion(BigInteger.valueOf(1));
		tableHeader.setMinorVersion(BigInteger.valueOf(0));
		//Set the date //TODO: Data format for now is not completely correct; fix
		GregorianCalendar calendar = new GregorianCalendar();
				calendar.setTime(new Date());
				try {
					tableHeader.setCreateUtcTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar));
				}
				catch(DatatypeConfigurationException e) { e.printStackTrace(); }
				
    	tableHeader.setTableName(settings.getTableName());
    	tableHeader.setUsesSeparatorByte(settings.getUsesSeparatorByte());
    	tableHeader.setBlockSize(BigInteger.valueOf(1)); //TODO: 1 by default; value should be based on a node setting
    	
    	configureTableHeaderFields();
    }
	
	private void configureTableHeaderFields() {
				
		//FieldAttributes fieldAttributes = new FieldAttributes();
		//fieldAttributes.setFmt(FieldAttrType.DATE.toString());
		//fieldHeader.setFieldFormat(fieldAttributes);
		QvxTableHeader.Fields fields = new QvxTableHeader.Fields();
		for(int i = 0; i < fieldNames.length; i++) {
			
			/* Create a QvxFieldHeader for each field */
			QvxTableHeader.Fields.QvxFieldHeader qvxFieldHeader = new QvxTableHeader.Fields.QvxFieldHeader();
			
			qvxFieldHeader.setFieldName(fieldNames[i]);
			setFieldTypeAndByteWidth(qvxFieldHeader);
			qvxFieldHeader.setExtent(determineExtent(qvxFieldHeader));
			qvxFieldHeader.setNullRepresentation(QvxNullRepresentation.QVX_NULL_FLAG_SUPPRESS_DATA);
			
			qvxFieldHeader.setBigEndian(settings.getIsBigEndian());
			if (qvxFieldHeader.isBigEndian() == null) { //Uses little-endian by default
				qvxFieldHeader.setBigEndian(false);
			}
			
			setFieldAttributes(qvxFieldHeader, i);
			
			fields.getQvxFieldHeader().add(qvxFieldHeader);
		}
		tableHeader.setFields(fields);		
	}
	
	private void writeTableHeader() {
		try {
			//Marshal qvxTableHeader into a FileOutputStream, then write a null byte
			JAXBContext jaxbContext;
			jaxbContext = JAXBContext.newInstance(QvxTableHeader.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			outputStream = new FileOutputStream(outFileName);
			jaxbMarshaller.marshal(tableHeader, outputStream);
			outputStream.write(NUL); //Zero-byte separator between xml and qvx body
		}
		catch(IOException | JAXBException e) {
			e.printStackTrace();
		}
	}
	
	private void writeBody() {
		
		/* Write the "data" values to the body of the FileOutputStream
		 */
		
		//Read all rows of data
		byte[] buffer = new byte[BUFFER_SIZE];
		for(int i = 0; i < data.length; i++) {
			
			if (tableHeader.isUsesSeparatorByte()) {
				bufferIndex = insertInto(buffer, RECORD_SEPARATOR, bufferIndex);
			}
			
			for(int j = 0; j < data[i].length; j++) {
				
				byte[] byteValue = null;
				
				//"fieldHeader" is the fieldHeader for the column that is being accessed
				QvxTableHeader.Fields.QvxFieldHeader fieldHeader = tableHeader.getFields().getQvxFieldHeader().get(j);
				if (fieldHeader.getNullRepresentation().equals(QVX_NULL_FLAG_SUPPRESS_DATA)) {
					byte nullFlag = getNullFlagSuppressDataByte(fieldHeader, data[i][j]);
					if (nullFlag == 1) {
						byteValue = new byte[] {nullFlag};
					}else {
						byteValue = combineByteArrays(
							new byte[] {nullFlag}, convertToByteValue(fieldHeader, data[i][j]));
					}
				}else if (fieldHeader.getNullRepresentation().equals(QVX_NULL_NEVER)) {
					byteValue = convertToByteValue(fieldHeader, data[i][j]);
				}else {
					throw new RuntimeException("Unrecognized or unimplemented null representation: "
						+ fieldHeader.getNullRepresentation());
				}
				
				//Write "byteValue" to buffer
				int prevBufferIndex = bufferIndex; //Necessary for dealing with buffer overflow
				bufferIndex = insertInto(buffer, byteValue, bufferIndex);
				if (bufferIndex == -1) { //If there would have been buffer overflow
					// Write the entire buffer to the outputStream (excluding the value that could not be entered),
					// then insert this value at beginning of buffer
					try {
						outputStream.write(Arrays.copyOfRange(buffer, 0, prevBufferIndex));
					}catch(IOException e) {
						e.printStackTrace();
					}				
					bufferIndex = insertInto(buffer, byteValue, 0);
				}
				//TODO: Write a method that incorporates "insertInto", and writing to outfile immediately
				// if it detects overflow (ie. the code right above this comment; also, insertInto should
				//update bufferIndex automatically (rather than using bufferIndex = insertInto())
			}
		}
		try { //Write any characters that are still in the buffer to the outputStream
			outputStream.write(Arrays.copyOfRange(buffer, 0, bufferIndex));		
			if (tableHeader.isUsesSeparatorByte()) {
				bufferIndex = insertInto(buffer, FILE_SEPARATOR, bufferIndex);
			}
			outputStream.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

	//Helper methods --------------------------------------------------
	
	private byte[] convertToByteValue(QvxFieldHeader fieldHeader, String s) {
		
		int byteWidth = fieldHeader.getByteWidth().intValue();
		ByteBuffer byteBuffer = null;
		switch (fieldHeader.getType()) {
			case QVX_SIGNED_INTEGER:
			case QVX_UNSIGNED_INTEGER:
				byteBuffer = ByteBuffer.allocate(byteWidth);
				byteBuffer.order(fieldHeader.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
				if (byteWidth == 4) {
					byteBuffer.putInt(Integer.parseInt(s));
				}else if(byteWidth == 8) {
					byteBuffer.putLong(Long.parseLong(s));
				}
				return byteBuffer.array();
			case QVX_IEEE_REAL:
				byteBuffer = ByteBuffer.allocate(byteWidth);
				byteBuffer.order(fieldHeader.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
				if (byteWidth == 4) {
					byteBuffer.putFloat(Float.parseFloat(s));
				}else if(byteWidth == 8) {
					byteBuffer.putDouble(Double.parseDouble(s));
				}
				return byteBuffer.array();
			case QVX_TEXT:
				return stringToByteArray_zeroTerminated(fieldHeader, s);
			default:
				return null;
		}
	}
	
	private String[][] dataTableToArray(BufferedDataTable table){
		
		final int numRows = (int)table.size();
		final int numColumns = table.getSpec().getNumColumns();
		
		String[][] arr = new String[numRows][numColumns];
		CloseableRowIterator iterator = table.iterator();
		int rowIndex = 0;		
        while (iterator.hasNext()) {
        	DataRow row = iterator.next();
        	for(int columnIndex = 0; columnIndex < numColumns; columnIndex++) {        		
        		DataCell cell = row.getCell(columnIndex);
        		String value;
        		if (cell.isMissing()) { //Empty value
        			value = "";
        		}else {
        			value = cell.toString();
        		}
        		arr[rowIndex][columnIndex] = value;
        	}
        	rowIndex++;
        }
		return arr;
	}
	
	private QvxFieldExtent determineExtent(QvxFieldHeader fieldHeader) {
		
		if (fieldHeader.getType() == QvxFieldType.QVX_TEXT) {
			return QvxFieldExtent.QVX_ZERO_TERMINATED;
		}else {
			return QvxFieldExtent.QVX_FIX;
		}
	}
	
	private byte getNullFlagSuppressDataByte(QvxFieldHeader fieldHeader, String s) {
		
		byte nullFlag = (byte)0;
		if (!fieldHeader.getType().equals(QvxFieldType.QVX_TEXT)) {
			if (s.toString().equals("")) {
				nullFlag = (byte)1;
			}
		}
		return nullFlag;
	}
	
	private static int insertInto(byte[] target, byte value, int offset) {
		
		return insertInto(target, new byte[] {value}, offset);
	}
	
	private static int insertInto(byte[] target, byte[] values, int offset) {
		
		/* Copy all of the elements of "values" into "target", starting at target[startIndex]. Return
		 * the index of "target" where the next inserted value would go. If there would be buffer overflow, do not do any insertions and
		 * return -1.
		 */
		
		if (offset + values.length > target.length) {
			return -1;
		}
		int i;
		for(i = offset; i < offset + values.length; i++) {
			target[i] = values[i - offset];
		}
		return i;
	}
	
	private void setFieldAttributes(QvxFieldHeader fieldHeader, int columnIndex) {
		
		//TODO: Deal with "Interval" FieldAttrType
		FieldAttributes fieldAttributes = new FieldAttributes();
		FieldAttrType qvxAttr = FieldAttrType.fromValue(settings.getSelectedFieldAttrs()[columnIndex]);
		fieldAttributes.setType(qvxAttr);
		
		if(qvxAttr.equals(FieldAttrType.UNKNOWN)) { //There is no additional formatting needed for "UNKNOWN" FieldAttrType
			fieldHeader.setFieldFormat(fieldAttributes);
			return;
		}
		
		if(qvxAttr.equals(FieldAttrType.FIX) || qvxAttr.equals(FieldAttrType.REAL)) {
			fieldAttributes.setNDec(BigInteger.valueOf(settings.getSelectedNDecs()[columnIndex]));
		}
		
		String knimeType = table.getDataTableSpec().getColumnSpec(columnIndex).getType().getName();
		if (knimeType.equals("Local Date Time")) {
			fieldAttributes.setFmt("YYYY-MM-DD'T'HH:MM[:ss]");
		}else if (knimeType.equals("Local Date")) {
			fieldAttributes.setFmt("YYYY-MM-DD");
		}else if (knimeType.equals("Local Time")) {
			fieldAttributes.setFmt("HH:MM[:ss]");
		}
		
	fieldHeader.setFieldFormat(fieldAttributes);
	}

	private void setFieldTypeAndByteWidth(QvxFieldHeader fieldHeader) {
		
		String fieldName = fieldHeader.getFieldName();
		DataColumnSpec columnSpec = table.getSpec().getColumnSpec(fieldName);
		String type = columnSpec.getType().getName();
		if (type.equals("Number (integer)")) {
			int lowerBound;
			int upperBound;
			
			try {
				lowerBound = Integer.parseInt(columnSpec.getDomain().getLowerBound().toString());
				upperBound = Integer.parseInt(columnSpec.getDomain().getUpperBound().toString());
			}catch(NumberFormatException e) {
				throw new RuntimeException("support for longs not added (uncertain about how KNIME"
						+ "works with long values);" + e);
			}
			
			if (lowerBound < 0) {
				fieldHeader.setType(QvxFieldType.QVX_SIGNED_INTEGER);
			}else {
				fieldHeader.setType(QvxFieldType.QVX_UNSIGNED_INTEGER);
			}
			fieldHeader.setByteWidth(BigInteger.valueOf(4));
		}else if (type.equals("Number (double)")) {
			fieldHeader.setType(QvxFieldType.QVX_IEEE_REAL);
			fieldHeader.setByteWidth(BigInteger.valueOf(8));	
		}else if (type.equals("String") || type.equals("Local Date Time") || type.equals("Local Date") || type.equals("Local Time")) {
			fieldHeader.setType(QvxFieldType.QVX_TEXT);
			fieldHeader.setByteWidth(BigInteger.valueOf(0));
		}else {
			throw new RuntimeException("Coding error in QvxWriter.java: Unrecognized KNIME type: " + type);
		}
	}
	
	private byte[] stringToByteArray_zeroTerminated(QvxFieldHeader fieldHeader, String s) {
		
		Integer codePage = null;
		if(fieldHeader.getCodePage() != null) {
			codePage = fieldHeader.getCodePage().intValue();
		}
		
		if (codePage == null) {
			byte[] bytes = new byte[s.length() + 1];
			for(int i = 0; i < s.length(); i++) {
				bytes[i] = (byte)s.charAt(i);
			}
			bytes[bytes.length-1] = (byte)0; //Zero-terminated byte
			return bytes;
		}else if (codePage == 1020 || codePage == 1021) { //UTF-16
			//TODO
			throw new IllegalStateException("UTF-16 is not supported");
		}
		throw new IllegalStateException("Code page " + codePage + " is not supported");
	}
}
