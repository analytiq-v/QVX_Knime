/* Author monica*/
package edu.njit.knime.adapter.qvx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.IOUtils;
import org.knime.base.node.io.filereader.ColProperty;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnDomain;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataType;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.BooleanCell;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.LongCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.def.TimestampCell;
import org.knime.core.data.def.IntCell;

import edu.njit.knime.adapter.nodes.qvx.QvxDataCellFactory;
import edu.njit.knime.adapter.qvx.QvxTableHeader.Fields.QvxFieldHeader;

public class QVXReader {
	private String filepath = null;
	private QvxTableHeader qvxTableHeader = null;
    private Vector<ColProperty> columnProperties;
    private int numOfColumns;
    
    private List<List<String>> qvxTableDataText = null;
    private DataRow [] qvxTableData = null;
	    
	public QVXReader(String filepath) throws JAXBException, IOException  {
		this.filepath = filepath;
		//Read the qvx Header

		JAXBContext context = JAXBContext.newInstance( QvxTableHeader.class );
		Unmarshaller jaxbUnmarshaller = null;
	    Reader reader = null;
	    try {
	        reader = new BufferedReader(new FileReader(filepath));
	        XMLInputFactory xif = XMLInputFactory.newInstance();
	        XMLEventReader xmlEventReader = xif.createXMLEventReader(reader);
			jaxbUnmarshaller = context.createUnmarshaller();
	        qvxTableHeader = (QvxTableHeader) jaxbUnmarshaller.unmarshal(new PartialXmlEventReader(xmlEventReader));
	        
	        

	    } catch (XMLStreamException e) {
			e.printStackTrace();
		} finally {
	        IOUtils.closeQuietly(reader);
	    }
	    
	    // convert Customer.qvx to Customer.csv using REST API and running the LOAD/STORE Script
	    // Use the QlikSenseAdapter to run script
	    
		    
	}
	
	public DataRow[] getTableData() {
		return qvxTableData;
	}
	
	
	private DataRow[] getTableData(Vector<ColProperty> columnProperties, List<List<String>> qvxTableDataText) {
		QvxDataCellFactory cellFactory = new QvxDataCellFactory();
		DataRow [] tableData = new DataRow[qvxTableDataText.size()-1];
		
		int rowIndex = 0;
		for(Object rowDataText :qvxTableDataText.toArray()) {
			int columnIndex = 0;
			DataCell [] rowData = new DataCell[columnProperties.size()];
			String rowHeader = null;

	        rowHeader = "Row" + rowIndex;
	        
			for(String colDataText :(List<String>)rowDataText) {
				
	        	DataType type = columnProperties.get(columnIndex).getColumnSpec().getType();

    			System.out.println("[ " + columnIndex + "] = " + type);

	        	if(type.equals(IntCell.TYPE)) {
	        			rowData[columnIndex] = cellFactory.createDataCellOfType(IntCell.TYPE, colDataText);
	        	}
	        	else	            	
	            if(type.equals(DoubleCell.TYPE)) {
	        			rowData[columnIndex] = cellFactory.createDataCellOfType(DoubleCell.TYPE, colDataText);
	        	}
	        	else
	            if(type.equals(LongCell.TYPE)) {
	        			rowData[columnIndex] = cellFactory.createDataCellOfType(LongCell.TYPE, colDataText);
	        	}
	        	else
	             	if(type.equals(BooleanCell.TYPE)) {
	        			rowData[columnIndex] = cellFactory.createDataCellOfType(BooleanCell.TYPE, colDataText);
	        	}
	        	else
	             	if(type.equals(TimestampCell.TYPE)) {
	        			rowData[columnIndex] = cellFactory.createDataCellOfType(TimestampCell.TYPE, colDataText);
	        	}
	        	else
	                   	
	         	if(type.equals(StringCell.TYPE)) {
	         			rowData[columnIndex] = cellFactory.createDataCellOfType(StringCell.TYPE, colDataText);
	        	}
	        	columnIndex++;
			}
			if(rowIndex>0)
				tableData[rowIndex-1] = new DefaultRow(rowHeader, rowData);;
			rowIndex++;
		}
		
		return tableData;
	}
	
	
	public QvxTableHeader getTableHeader() {		
		return qvxTableHeader;
	}
	
	public Vector<ColProperty> getColumnProperties() throws IOException {
		QvxTableHeader.Fields fields =  qvxTableHeader.getFields(); 
		List<QvxFieldHeader> qvxFieldHeaderList = fields.getQvxFieldHeader();
		numOfColumns = 0;
		
		for(QvxFieldHeader qvxFieldHeader:qvxFieldHeaderList) {
			numOfColumns++;
		}

        
		int index = 0;
		ColProperty columnProperty = null;
		columnProperties = new Vector<ColProperty>();
		
		for(QvxFieldHeader qvxFieldHeader:qvxFieldHeaderList) {
			
			columnProperty = new ColProperty();
			DataColumnSpecCreator dataColumnSpecCreator = new DataColumnSpecCreator(qvxFieldHeader.getFieldName(), convertQvxToNodeDataType(qvxFieldHeader.getFieldFormat().getType()));
			DataColumnSpec dataColumSpec = dataColumnSpecCreator.createSpec();
			columnProperty.setColumnSpec(dataColumSpec);
			columnProperties.add(columnProperty);
		}

		//read the csv file for data		
	    qvxTableDataText = getTableDataText(filepath.replaceAll(".qvx", ".csv"));
	    qvxTableData = getTableData(columnProperties, qvxTableDataText);

		return columnProperties;		
	}

	private DataType convertQvxToNodeDataType(FieldAttrType 	qvxFieldAttrType) {
		
		switch(qvxFieldAttrType) {
		

				case REAL:
					return DoubleCell.TYPE;
					
				case INTEGER:
					return IntCell.TYPE;
					
				case ASCII:
					return StringCell.TYPE;
				
				//case DATE:
				case TIMESTAMP:
				//case TIME:
					return TimestampCell.TYPE;

				case MONEY:
				case UNKNOWN:
					return StringCell.TYPE;
					//return BooleanCell.TYPE;

		}
		return null;
	}

	private DataColumnDomain convertQvxToNodeDataDomain(QvxFieldHeader qvxFieldHeader) {
		
		return null;
	}

	public int getNumColumns() {
		QvxTableHeader.Fields fields =  qvxTableHeader.getFields(); 
		List<QvxFieldHeader> qvxFieldHeaderList = fields.getQvxFieldHeader();
		numOfColumns = 0;
		
		for(QvxFieldHeader qvxFieldHeader:qvxFieldHeaderList) {
			numOfColumns++;
		}

		return numOfColumns;		
	}
	
	
	public  String[][] displayQvxData(QvxTableHeader qvxTableHeader) {
		QvxTableHeader.Fields fields =  qvxTableHeader.getFields(); 
		List<QvxFieldHeader> qvxFieldHeaderList = fields.getQvxFieldHeader();
		String[][] objs = new String[qvxFieldHeaderList.size()][4];
		int index = 0;
		for(QvxFieldHeader qvxFieldHeader:qvxFieldHeaderList) {
			objs[index][0] = qvxFieldHeader.getFieldName();
			objs[index][1] = qvxFieldHeader.getType().toString();
			objs[index][2] = qvxFieldHeader.getFixPointDecimals().toString();
			objs[index][3] = qvxFieldHeader.getFieldFormat().toString();
			index++;
		}

		return objs;		
	}
	
	public  String[][] extractQvxData(QvxTableHeader qvxTableHeader) {
		QvxTableHeader.Fields fields =  qvxTableHeader.getFields(); 
		List<QvxFieldHeader> qvxFieldHeaderList = fields.getQvxFieldHeader();
		String[][] objs = new String[qvxFieldHeaderList.size()][4];
		int index = 0;
		
		for(QvxFieldHeader qvxFieldHeader:qvxFieldHeaderList) {
			objs[index][0] = "";
			objs[index][1] = "";
			objs[index][2] = "";
			objs[index][3] = "";
			
			if(qvxFieldHeader.getFieldName()!=null)
				objs[index][0] = qvxFieldHeader.getFieldName();
			if(qvxFieldHeader.getType()!=null)
				objs[index][1] = qvxFieldHeader.getType().toString();
			if(qvxFieldHeader.getFixPointDecimals()!=null)
				objs[index][2] = qvxFieldHeader.getFixPointDecimals().toString();
			if(qvxFieldHeader.getFieldFormat()!=null)
				objs[index][3] = qvxFieldHeader.getFieldFormat().toString();
			index++;
		}

		return objs;		
	}
	
	public  String[][] displayQvxData(String[][] objs) {
		for(int index = 0; index < objs.length; index++) {
			System.out.print(objs[index][0]) ;
			System.out.print(", ");
			System.out.print(objs[index][1]) ;
			System.out.print(", ");
			System.out.print(objs[index][2]) ;
			System.out.print(", ");
			System.out.println(objs[index][3]) ;
		}

		return objs;		
	}
	
    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DEFAULT_QUOTE = '"';

    
    public static List<String> parseLine(String cvsLine, char separators, char customQuote) {

        List<String> result = new ArrayList<>();

        //if empty, return!
        if (cvsLine == null && cvsLine.isEmpty()) {
            return result;
        }

        if (customQuote == ' ') {
            customQuote = DEFAULT_QUOTE;
        }

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuffer curVal = new StringBuffer();
        boolean inQuotes = false;
        boolean startCollectChar = false;
        boolean doubleQuotesInColumn = false;

        char[] chars = cvsLine.toCharArray();

        for (char ch : chars) {

            if (inQuotes) {
                startCollectChar = true;
                if (ch == customQuote) {
                    inQuotes = false;
                    doubleQuotesInColumn = false;
                } else {

                    //Fixed : allow "" in custom quote enclosed
                    if (ch == '\"') {
                        if (!doubleQuotesInColumn) {
                            curVal.append(ch);
                            doubleQuotesInColumn = true;
                        }
                    } else {
                        curVal.append(ch);
                    }

                }
            } else {
                if (ch == customQuote) {

                    inQuotes = true;

                    //Fixed : allow "" in empty quote enclosed
                    if (chars[0] != '"' && customQuote == '\"') {
                        curVal.append('"');
                    }

                    //double quotes in column will hit this!
                    if (startCollectChar) {
                        curVal.append('"');
                    }

                } else if (ch == separators) {

                    result.add(curVal.toString());

                    curVal = new StringBuffer();
                    startCollectChar = false;

                } else if (ch == '\r') {
                    //ignore LF characters
                    continue;
                } else if (ch == '\n') {
                    //the end, break!
                    break;
                } else {
                    curVal.append(ch);
                }
            }

        }

        result.add(curVal.toString());

        return result;
    }
    
    public static List<String> parseLine(String cvsLine, char separators) {
        return parseLine(cvsLine, separators, DEFAULT_QUOTE);
    }

    public static List<String> parseLine(String cvsLine) {
        return parseLine(cvsLine, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
    }

    private List<List<String>> getTableDataText (String filepath) throws IOException {
	

        Scanner scanner = new Scanner(new File(filepath));
        
        List<String> line = null;
        List<List<String>> data = new ArrayList<List<String>>();
        
        while (scanner.hasNext()) {
            line = parseLine(scanner.nextLine());
            System.out.println(" [Date_of_acquisition= " + line.get(0) + ", Cust_ID= " + line.get(1) + " , ZIP=" + line.get(2) + " , LAT=" + line.get(3)+ " , LNG=" + line.get(4)+ " ,Acq_channel=" + line.get(5)+"]");
            data.add(line);
        }
        scanner.close();

		
		
		return data;
	}
	
	 public static void main(String[] args) {
		 QVXReader qvxReader;
		 QvxTableHeader qvxTableHeader = null;
		 List<List<String>>  qvxData = null;
		try {
			qvxReader = new QVXReader("Customer.qvx");
			qvxTableHeader = qvxReader.getTableHeader();		 
			qvxReader.displayQvxData(qvxReader.extractQvxData(qvxTableHeader));
			
			if(qvxReader.getTableData()!=null)
			for(DataRow rowData: qvxReader.getTableData()) {
				for(DataCell dataCell:rowData) {
					System.out.println(dataCell.toString());
				}
				
			}
			
		} catch (FileNotFoundException | JAXBException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		 


	 }
}
