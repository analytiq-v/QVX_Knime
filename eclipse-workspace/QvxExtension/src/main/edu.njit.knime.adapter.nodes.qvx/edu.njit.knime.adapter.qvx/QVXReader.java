package edu.njit.knime.adapter.qvx;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.knime.base.node.io.filereader.ColProperty;
import org.knime.core.data.DataColumnDomain;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataType;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.def.TimestampCell;

import edu.njit.knime.adapter.qvx.QvxTableHeader.Fields.QvxFieldHeader;

@SuppressWarnings("deprecation")
public class QVXReader {
	@SuppressWarnings("unused")
	private String filepath = "";
	private QvxTableHeader qvxTableHeader = null;
    @SuppressWarnings("unused")
	private Vector<ColProperty> m_columnProperties;
    private int m_numOfColumns;
	
	public QVXReader(String filepath) throws JAXBException, FileNotFoundException  {
		JAXBContext jaxbContext;	
		jaxbContext = JAXBContext.newInstance( QvxTableHeader.class );
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		InputStream is = new FileInputStream(filepath );
		qvxTableHeader = (QvxTableHeader) jaxbUnmarshaller.unmarshal(is);
		System.out.println(qvxTableHeader.getTableName());
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

	@SuppressWarnings("deprecation")
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
		case DATE:
			break;
		case FIX:
			break;
		case INTERVAL:
			break;
		case TIME:
			break;
		default:
			break;

		}
		return null;
	}

	@SuppressWarnings("unused")
	private DataColumnDomain convertQvxToNodeDataDomain(QvxFieldHeader qvxFieldHeader) {
		
		return null;
	}

	public int getNumColumns() {
		QvxTableHeader.Fields fields =  qvxTableHeader.getFields(); 
		List<QvxFieldHeader> qvxFieldHeaderList = fields.getQvxFieldHeader();
		m_numOfColumns = 0;
		
		for(@SuppressWarnings("unused") QvxFieldHeader qvxFieldHeader:qvxFieldHeaderList) {
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
	
	 public static void main(String[] args) {
		 QVXReader qvxReader;
		 QvxTableHeader qvxTableHeader = null;
		try {
			qvxReader = new QVXReader("customer.qvx");
			qvxTableHeader = qvxReader.getTableHeader();		 
			qvxReader.displayQvxData(qvxReader.extractQvxData(qvxTableHeader));
		} catch (FileNotFoundException | JAXBException e) {
			e.printStackTrace();
		}
		 


	 }
}
