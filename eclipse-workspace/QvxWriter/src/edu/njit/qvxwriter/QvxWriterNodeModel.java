package edu.njit.qvxwriter;

import edu.njit.qvxwriter.QvxWriterNodeSettings.OverwritePolicy;

import java.io.File;
import java.io.IOException;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import static edu.njit.qvxwriter.QvxWriterNodeSettings.CFGKEY_FILE_NAME;
import static edu.njit.qvxwriter.QvxWriterNodeSettings.CFGKEY_OVERWRITE_POLICY;

/**
 * This is the model implementation of QvxWriter.
 * 
 *
 * @author 
 */
public class QvxWriterNodeModel extends NodeModel {
    
    private QvxWriterNodeSettings m_settings;
    
    /**
     * Constructor for the node model.
     */
    protected QvxWriterNodeModel() {
    
        // 1 incoming port and 0 outgoing ports
        super(1, 0);
        m_settings = new QvxWriterNodeSettings();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
    	
    	writeQvxFile(inData[0]);
    	return null;
    }
    
    protected void writeQvxFile(final BufferedDataTable table) {
    	
    	QvxWriter qvxWriter = new QvxWriter();
    	String outFileName = m_settings.getFileName();
    	qvxWriter.writeQvxFile(table, outFileName, m_settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
    	
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {

        return new DataTableSpec[]{null};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {

        m_settings.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        
    	m_settings = new QvxWriterNodeSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
            
    	validateFileName(settings);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
           	
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
    	
    }
    
    protected void validateFileName(NodeSettingsRO settings) throws InvalidSettingsException {
    	
    	String fileName = settings.getString(CFGKEY_FILE_NAME);
    	String overwritePolicy = settings.getString(CFGKEY_OVERWRITE_POLICY);
    	
    	File file = new File(fileName);
    	if (file.isDirectory()) {
    		throw new InvalidSettingsException("The provided file name is a directory");
    	}
    	if(file.exists()) {
    		if(overwritePolicy.equals(OverwritePolicy.ABORT.toString())) {
    			throw new InvalidSettingsException("File already exists");
    		}
    	}
    	if(!fileName.endsWith(".qvx")) {
    		throw new InvalidSettingsException("Invalid file extension: \".qvx\" expected");
    	}
    }
}

