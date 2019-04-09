package edu.njit.knime.adapter.qvx;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.IOUtils;
import org.knime.base.node.io.filereader.ColProperty;
import org.knime.core.data.DataColumnDomain;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataType;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.BooleanCell;
import org.knime.core.data.def.ComplexNumberCell;
import org.knime.core.data.def.LongCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.def.TimestampCell;
import org.knime.core.data.def.IntCell;

import edu.njit.knime.adapter.qvx.QvxTableHeader.Fields.QvxFieldHeader;

public class QVXReader {
	private String filepath = null;
	private QvxTableHeader qvxTableHeader = null;
    private Vector<ColProperty> m_columnProperties;
    private int m_numOfColumns;
	    
	public QVXReader(String filepath) throws JAXBException, FileNotFoundException  {
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
	}
	
	public QvxTableHeader getTableHeader() {		
		return qvxTableHeader;
	}
	
	public Vector<ColProperty> getColumnProperties() {
		QvxTableHeader.Fields fields =  qvxTableHeader.getFields(); 
		List<QvxFieldHeader> qvxFieldHeaderList = fields.getQvxFieldHeader();
		m_numOfColumns = 0;
		
		for(QvxFieldHeader qvxFieldHeader:qvxFieldHeaderList) {
			m_numOfColumns++;
		}

        
		int index = 0;
		ColProperty m_columnProperty = null;
		Vector<ColProperty> m_columnProperties = new Vector<ColProperty>();
		
		for(QvxFieldHeader qvxFieldHeader:qvxFieldHeaderList) {
			
			m_columnProperty = new ColProperty();
			//DataColumnSpec dataColumSpec = new DataColumnSpec(qvxFieldHeader.getFieldName(), null, convertQvxToNodeDataType(qvxFieldHeader), convertQvxToNodeDataDomain(qvxFieldHeader), null, null, null, null, null);
			DataColumnSpecCreator dataColumnSpecCreator = new DataColumnSpecCreator(qvxFieldHeader.getFieldName(), convertQvxToNodeDataType(qvxFieldHeader.getFieldFormat().getType()));
			DataColumnSpec dataColumSpec = dataColumnSpecCreator.createSpec();
			m_columnProperty.setColumnSpec(dataColumSpec);
			m_columnProperties.add(m_columnProperty);
		}

		return m_columnProperties;		
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
		m_numOfColumns = 0;
		
		for(QvxFieldHeader qvxFieldHeader:qvxFieldHeaderList) {
			m_numOfColumns++;
		}

		return m_numOfColumns;		
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

    private List<List<String>> getTableData (String filepath) throws IOException {
	

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
	

}
