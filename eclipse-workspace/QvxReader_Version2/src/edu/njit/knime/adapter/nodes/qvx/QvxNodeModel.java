package edu.njit.knime.adapter.nodes.qvx;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import org.knime.base.node.io.filereader.ColProperty;
import org.knime.base.node.io.filereader.FileAnalyzer;
import org.knime.base.node.io.filereader.FileReaderExecutionMonitor;
import org.knime.base.node.io.filereader.FileReaderNodeSettings;
import org.knime.base.node.io.filereader.FileTable;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.util.CheckUtils;
import org.knime.core.node.workflow.NodeProgress;
import org.knime.core.node.workflow.NodeProgressEvent;
import org.knime.core.node.workflow.NodeProgressListener;
import org.knime.core.util.FileUtil;
import org.knime.core.util.tokenizer.SettingsStatus;

import edu.njit.knime.adapter.qvx.QVXReader;
import edu.njit.knime.adapter.qvx.QvxTableHeader;

import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;


/**
 * This is the model implementation of Qvx.
 * Qvx Node
 *
 * @author Monica
 */
public class QvxNodeModel extends NodeModel {
    
    // the logger instance
    private static final NodeLogger logger = NodeLogger
            .getLogger(QvxNodeModel.class);
    
	public static final String CFGKEY_FILE_NAME = "qvx3.xml";
	public static final String DEFAULT_PATH = "/Users/bsangam/eclipse-workspace/test";

	public static final String CFGKEY_FILE_PATH = "FilePath";

    private final SettingsModelString m_string =
            new SettingsModelString(CFGKEY_FILE_PATH,
        			DEFAULT_PATH);
                
    // a vector storing properties for each column. The size might not be
    // related to the actual number of columns.
    private Vector<ColProperty> m_columnProperties;

    // the number of columns
    private int m_numOfColumns;


      /**
     * Constructor for the node model.
     */
    protected QvxNodeModel() {
            super(0, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {

    	System.out.println("Creating file table");
        QvxFileTable fTable = createFileTable(exec);
        try {
        	System.out.println("Creating buffered data table");
            BufferedDataTable table = exec.createBufferedDataTable(fTable, exec.createSubExecutionContext(0.0));
            return new BufferedDataTable[] {table};
        } finally {
            // fix AP-6127
            fTable.dispose();
        }
    }

    protected QvxFileTable createFileTable(final ExecutionContext exec) throws Exception {
        // prepare the settings for the file analyzer
        QvxFileReaderNodeSettings settings = new QvxFileReaderNodeSettings();

        CheckUtils.checkSourceFile(m_string.getStringValue());
        URL url = FileUtil.toURL(m_string.getStringValue());
        //settings.setDataFileLocationAndUpdateTableName(url);

        System.out.println("Creating QVXReader object");
        QVXReader qvxReader = new QVXReader(m_string.getStringValue());
        System.out.println("Getting table header");
        QvxTableHeader qvxTableHeader = qvxReader.getTableHeader();


        // the number of columns
        m_numOfColumns = qvxReader.getNumColumns();
        
        System.out.println("Getting column properties");
        m_columnProperties = qvxReader.getColumnProperties();

        System.out.println("Creating Execution Monitor");
        //final ExecutionMonitor analyseExec = exec.createSubProgress(0.5);
        System.out.println("Creating Execution Context");
        final ExecutionContext readExec = exec.createSubExecutionContext(0.5);
        exec.setMessage("Analyzing file");

        //SettingsStatus status = settings.getStatusOfSettings();
        //if (status.getNumOfErrors() > 0) {
       //     throw new IllegalStateException(status.getErrorMessage(0));
        //}
        System.out.println("Creating tableSpec");
        final DataTableSpec tableSpec = createDataTableSpec();
        if (tableSpec == null) {
            final SettingsStatus status2 = settings.getStatusOfSettings(true, null);
            if (status2.getNumOfErrors() > 0) {
                throw new IllegalStateException(status2.getErrorMessage(0));
            } else {
                throw new IllegalStateException("Unknown error during file analysis.");
            }
        }
        exec.setMessage("Buffering file");
        System.out.println("Returing new QvxFileTable");
        return new QvxFileTable(tableSpec, settings, readExec);
    }

    


	public DataTableSpec createDataTableSpec() {


        // collect the ColumnSpecs for each column
        Vector<DataColumnSpec> cSpec = new Vector<DataColumnSpec>();
        for (int c = 0; c < m_numOfColumns; c++) {
            ColProperty cProp = m_columnProperties.get(c);
            if (!cProp.getSkipThisColumn()) {
                cSpec.add(cProp.getColumnSpec());
            }
        }

        return new DataTableSpec(m_string.getStringValue(),
                cSpec.toArray(new DataColumnSpec[cSpec.size()]));
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
        //read the xml schema
    	
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

        // TODO save user settings to the config object.
        
        m_string.saveSettingsTo(settings);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
            
        // TODO load (valid) settings from the config object.
        // It can be safely assumed that the settings are valided by the 
        // method below.
        
        m_string.loadSettingsFrom(settings);

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

        m_string.validateSettings(settings);

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

    }

}

