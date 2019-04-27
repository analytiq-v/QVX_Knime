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
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.data.def.IntCell;

import edu.njit.knime.adapter.nodes.qvx.QvxDataCellFactory;
import edu.njit.knime.adapter.qvx.QvxTableHeader.Fields.QvxFieldHeader;

public class QVXReader {
	private String filepath = null;
	private QvxTableHeader qvxTableHeader = null;
     private int numOfColumns;
    private BufferedDataTable[] qvxTableData = null;
    private QvxBinaryReader qvxBinaryReader = null;
	
	public QVXReader(String filepath, final ExecutionContext exec) throws JAXBException, IOException  {
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
	    
	    qvxBinaryReader = new QvxBinaryReader();
	    qvxTableData = qvxBinaryReader.readQvx(filepath, exec);
	    
	}
	
	public BufferedDataTable[] getTableData() {
		return qvxTableData;
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
	
		
	 public static void main(String[] args) {

	 }
}
