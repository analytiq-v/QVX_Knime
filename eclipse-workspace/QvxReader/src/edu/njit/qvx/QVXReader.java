package edu.njit.qvx;

import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;

import edu.njit.qvx.QvxTableHeader.Fields.QvxFieldHeader;

public class QVXReader {
	private String filepath = null;
	private QvxTableHeader qvxTableHeader = null;
	private int numOfColumns;
	private BufferedDataTable[] qvxTableData = null;
	private QvxBinaryReader qvxBinaryReader = null;
	
	public QVXReader(String filepath, final ExecutionContext exec) throws JAXBException, IOException  {
		this.filepath = filepath;
	    
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
}
