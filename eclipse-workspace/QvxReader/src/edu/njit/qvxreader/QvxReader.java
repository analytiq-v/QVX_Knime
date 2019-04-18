package edu.njit.qvxreader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;

import edu.njit.qvx.QvxFieldExtent;
import edu.njit.qvx.QvxNullRepresentation;
import edu.njit.qvx.QvxQvSpecialFlag;
import edu.njit.qvx.QvxTableHeader;
import edu.njit.qvx.QvxTableHeader.Fields.QvxFieldHeader;

public class QvxReader {

	private final byte FS_BYTE = 0x1C;
	private final byte RS_BYTE = 0x1E;
	
	private QvxTableHeader qvxTableHeader;
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
		
		System.out.println("reading from qvx table header");
		readQvxTableHeader();
		//printBody();
		//return null;
		readBody();
		
		System.out.println(Arrays.toString(fieldNames));
		for(int i = 0; i < data.size(); i++) {
			System.out.println(Arrays.toString(data.get(i)));
		}
		
		return new BufferedDataTable[] {dataToDataTable()};
	}
	
	private BufferedDataTable dataToDataTable() {
		
		int numRows = data.size();
		int numCols = data.get(0).length;
		
		DataType[] dataTypes = new DataType[fieldNames.length];
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
			if (dataClass.equals(java.lang.Integer.class)){
				dataTypes[i] = IntCell.TYPE;
			}else if(dataClass.equals(java.lang.Double.class)) {
				dataTypes[i] = DoubleCell.TYPE;
			}else if(dataClass.equals(java.lang.String.class)) {
				dataTypes[i] = StringCell.TYPE;
			}else {
				throw new RuntimeException("Unknown data type");
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
		    	}else {
					throw new RuntimeException("Unknown data type");
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
			List<QvxTableHeader.Fields.QvxFieldHeader> fieldHeaders =
					qvxTableHeader.getFields().getQvxFieldHeader();
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
					}else if (nullFlag == 1) {
						System.out.println("null value found");
						row[i] = null;
						//This significies "null"; a field value is not used
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
				//TODO
				return null;
			case QVX_IEEE_REAL:
				if (byteWidth == 4) {
					return ByteBuffer.wrap(readBytesFromBuffer(4)).order(byteOrder).getFloat();
				}else if(byteWidth == 8) {
					return ByteBuffer.wrap(readBytesFromBuffer(8)).order(byteOrder).getDouble();
				}
			case QVX_PACKED_BCD:
				//TODO
				return null;
			case QVX_UNSIGNED_INTEGER:
			case QVX_SIGNED_INTEGER:
				if (byteWidth == 1) {
					// TODO
				}else if(byteWidth == 2) {
					return ByteBuffer.wrap(readBytesFromBuffer(2)).order(byteOrder).getShort();
				}else if(byteWidth == 4) {
					return ByteBuffer.wrap(readBytesFromBuffer(4)).order(byteOrder).getInt();
				}else if(byteWidth == 8) {
					return ByteBuffer.wrap(readBytesFromBuffer(8)).order(byteOrder).getLong();
				}
			case QVX_TEXT:
				return bufferToString_zeroTerminated();
			case QVX_QV_DUAL:
				if(fieldHeader.getExtent() == QvxFieldExtent.QVX_QV_SPECIAL) {
					return readQvDualBytes();
				}else {
					throw new RuntimeException("Fields of type QVX_QV_DUAL must use field extent" +
							"QVX_QV_SPECIAL");
				}
			default:
				throw new RuntimeException("Unrecognized field type of " + fieldHeader.getType());
		}
	}
	
	private Object readQvDualBytes() {
		
		byte flag = readBytesFromBuffer(1)[0];
		if (flag == QvxQvSpecialFlag.QVX_QV_SPECIAL_NULL.getValue()) {
			throw new RuntimeException("Coding error: Unimplemented QvxQvSpecialFlag: " + flag);
		}else if(flag == QvxQvSpecialFlag.QVX_QV_SPECIAL_INT.getValue()) {
			throw new RuntimeException("Coding error: Unimplemented QvxQvSpecialFlag: " + flag);
		}else if(flag == QvxQvSpecialFlag.QVX_QV_SPECIAL_DOUBLE.getValue()){
			throw new RuntimeException("Coding error: Unimplemented QvxQvSpecialFlag: " + flag);
		}else if(flag == QvxQvSpecialFlag.QVX_QV_SPECIAL_STRING.getValue()) {
			return bufferToString_zeroTerminated();
		}else if(flag == QvxQvSpecialFlag.QVX_QV_SPECIAL_INT_AND_STRING.getValue()) {
			throw new RuntimeException("Coding error: Unimplemented QvxQvSpecialFlag: " + flag);
		}else if(flag == QvxQvSpecialFlag.QVX_QV_SPECIAL_DOUBLE_AND_STRING.getValue()) {
			readBytesFromBuffer(8); //Skip the "double" part of this value; I am unsure what this
			//double is even used for (I do not actually see it in the data table anywhere)
			int oldBufferIndex = bufferIndex;
			try {
				return Double.parseDouble(bufferToString_zeroTerminated());
			}catch(NumberFormatException e) { //If it is not a number
				bufferIndex = oldBufferIndex;
				return bufferToString_zeroTerminated();
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
}