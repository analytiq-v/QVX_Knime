package main.java.edu.njit.knime.adapter.qvx;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public class QVXWriter {
	
	public void process (QvxTableHeader qvxTableHeader, String filepath) throws FileNotFoundException, JAXBException {
		
		JAXBContext jaxbContext;
		jaxbContext = JAXBContext.newInstance( QvxTableHeader.class );
		Marshaller jaxbMarshaller   = jaxbContext.createMarshaller();
		OutputStream os = new FileOutputStream( filepath  );
		jaxbMarshaller.marshal( qvxTableHeader, os );
		System.out.println(jaxbMarshaller.toString());
	}
	
	public static void main(String[] args) {
		QvxTableHeader qvxTableHeader = new QvxTableHeader();
		
		QVXReader qvxReader = new QVXReader();
		try {
			 qvxTableHeader = qvxReader.process("qvx1.xml");
		} catch (FileNotFoundException | JAXBException e) {
			e.printStackTrace();
		}
		 
		QVXWriter qvxWriter = new QVXWriter();
		try {
			qvxWriter.process(qvxTableHeader, "qvx3.xml");
		} catch (FileNotFoundException | JAXBException e) {
			e.printStackTrace();
		}
		
		//read the file
		try {
			 qvxTableHeader = qvxReader.process("qvx3.xml");
		} catch (FileNotFoundException | JAXBException e) {
			e.printStackTrace();
		}
		 
		 qvxReader.displayQvxData(qvxReader.extractQvxData(qvxTableHeader));
	}
}
