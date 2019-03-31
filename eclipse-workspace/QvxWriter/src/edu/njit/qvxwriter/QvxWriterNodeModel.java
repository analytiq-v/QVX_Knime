package edu.njit.qvxwriter;

import java.io.File;
import java.io.IOException;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;


/**
 * This is the model implementation of QvxWriter.
 * 
 *
 * @author 
 */
public class QvxWriterNodeModel extends NodeModel {
    
    // the logger instance
    private static final NodeLogger logger = NodeLogger
            .getLogger(QvxWriterNodeModel.class);

    private QvxWriterNodeSettings m_settings;
    
    // example value: the models count variable filled from the dialog 
    // and used in the models execution method. The default components of the
    // dialog work with "SettingsModels".
    /*private final SettingsModelIntegerBounded m_count =
        new SettingsModelIntegerBounded(QvxWriterNodeSettings.CFGKEY_COUNT,
                    QvxWriterNodeSettings.DEFAULT_COUNT,
                    Integer.MIN_VALUE, Integer.MAX_VALUE);
    What is the point of this object? It will likely be removed from this project.
    */
    /**
     * Constructor for the node model.
     */
    protected QvxWriterNodeModel() {
    
        // 1 incoming port and 0 outgoing ports
        super(1, 1);
        m_settings = new QvxWriterNodeSettings();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
    	
    	return writeQvxFile(inData[0]); 
    }
    	/*
    	System.out.println(inData[0].getSummary());
    	System.out.println(inData[0].getDataTableSpec());
    	DataTableSpec spec = inData[0].getDataTableSpec();
    	String[] columnNames = spec.getColumnNames();
    	for(String column : columnNames) {
    		DataColumnSpec columnSpec = spec.getColumnSpec(column);
    		System.out.println(column + "\t" + columnSpec.getType());
    	}
    	
    	System.out.println("Number of columns:" + spec.getNumColumns());
    	QvxWriter qvxWriter = new QvxWriter();
    	qvxWriter.writeQvxFile(inData[0], "C:\\Users\\Mehmet\\Documents\\KNIME\\products.qvx");
    	        
        // Dummy code, to prevent error
        DataColumnSpec[] allColSpecs = new DataColumnSpec[3];
        DataTableSpec outputSpec = new DataTableSpec(allColSpecs);
        BufferedDataContainer container = exec.createDataContainer(outputSpec);
        for (int i = 0; i < allColSpecs.length; i++) {
            RowKey key = new RowKey("Row " + i);
            DataCell[] cells = new DataCell[allColSpecs.length];
            for(int j = 0; j < allColSpecs.length; j++) {
            	cells[j] = new StringCell("Value");
            }           
            DataRow row = new DefaultRow(key, cells);
            container.addRowToTable(row);
            exec.checkCanceled();
            exec.setProgress(i / allColSpecs.length, "Adding row " + i);
        }
        container.close();
        BufferedDataTable out = container.getTable();
        System.out.println("Output row count: " + out.size());
        // Do not return anything meaningful, since the output is a qvx file, not a BufferedDataTable
        return new BufferedDataTable[]{out};    	
    }
    */
    protected BufferedDataTable[] writeQvxFile(final BufferedDataTable table) {
    	QvxWriter qvxWriter = new QvxWriter();
    	//qvxWriter.writeQvxFile(table, "C:\\Users\\Mehmet\\Documents\\KNIME\\products.qvx");
    	System.out.println("Write Qvx File finished executing");
    	return new BufferedDataTable[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        // TODO Code executed on reset.
        // Models build during execute are cleared here.
        // Also data handled in load/saveInternals will be erased here.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {
        
        // TODO: check if user settings are available, fit to the incoming
        // table structure, and the incoming types are feasible for the node
        // to execute. If the node can execute in its current state return
        // the spec of its output data table(s) (if you can, otherwise an array
        // with null elements), or throw an exception with a useful user message

        return new DataTableSpec[]{null};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {

        System.out.println("NodeModel: saveSettingsTo()");
        m_settings.saveSettingsTo(settings);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        
    	System.out.println("NodeModel: loadValidatedSettingsFrom()");
    	m_settings = new QvxWriterNodeSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
            
        // TODO check if the settings could be applied to our model
        // e.g. if the count is in a certain range (which is ensured by the
        // SettingsModel).
        // Do not actually set any values of any member variables.
    	
    	//TODO: Don't really think there is anything we need to validate
    	//File file = new File(settings.getString(CFGKEY_FILE_NAME));
    	//if (file.exists() &&)
    	System.out.println("NodeModel: validateSettings()");
    	/*if (settings.getBoolean(CFGKEY_IS_BIG_ENDIAN)) {
    		throw new InvalidSettingsException("Little Endian is expected!");
    	}*/
        //m_count.validateSettings(settings);
    	//settings.get

    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        
        // TODO load internal data. 
        // Everything handed to output ports is loaded automatically (data
        // returned by the execute method, models loaded in loadModelContent,
        // and user settings set through loadSettingsFrom - is all taken care 
        // of). Load here only the other internals that need to be restored
        // (e.g. data used by the views).
    	
    	System.out.println("NodeModel: loadInternals()");
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
       
        // TODO save internal models. 
        // Everything written to output ports is saved automatically (data
        // returned by the execute method, models saved in the saveModelContent,
        // and user settings saved through saveSettingsTo - is all taken care 
        // of). Save here only the other internals that need to be preserved
        // (e.g. data used by the views).
    	System.out.println("NodeModel: saveInternals()");

    }
    
    // Validation methods
    protected void validate() {
    	
    }

}

