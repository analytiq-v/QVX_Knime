package main.java.edu.njit.knime.adapter.qvx;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import main.java.edu.njit.knime.adapter.qvx.QvxTableHeader.Fields.QvxFieldHeader;

public class QVXReader {
	
	public QvxTableHeader process (String filepath) throws FileNotFoundException, JAXBException {
		
		JAXBContext jaxbContext;	
		jaxbContext = JAXBContext.newInstance( QvxTableHeader.class );
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		InputStream is = new FileInputStream(filepath );
		QvxTableHeader qvxTableHeader1 = (QvxTableHeader) jaxbUnmarshaller.unmarshal(is);
		System.out.println(qvxTableHeader1.getTableName());
		return qvxTableHeader1;

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
		 QVXReader qvxReader = new QVXReader();
		 QvxTableHeader qvxTableHeader = null;
		 try {
			 qvxTableHeader = qvxReader.process("qvx1.xml");
		} catch (FileNotFoundException | JAXBException e) {
			e.printStackTrace();
		}
		 
		 qvxReader.displayQvxData(qvxReader.extractQvxData(qvxTableHeader));

	 }
}
