package edu.njit.qvxreader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import org.knime.base.node.io.filereader.ColProperty;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.util.CheckUtils;
import org.knime.core.util.FileUtil;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import edu.njit.qvx.QVXReader;


/**
 * This is the model implementation of Qvx.
 * Qvx Node
 *
 * @author Monica
 */
public class QvxReaderNodeModel extends NodeModel {
    
    private static final NodeLogger logger = NodeLogger.getLogger(QvxReaderNodeModel.class);
    
	public static final String DEFAULT_PATH = "./";

	public static final String CFGKEY_FILE_PATH = "FilePath";

    private final SettingsModelString filepath = new SettingsModelString(CFGKEY_FILE_PATH, DEFAULT_PATH);
                
    private Vector<ColProperty> columnProperties;

    private int numOfColumns;


    protected QvxReaderNodeModel() {
            super(0, 1);
    }

    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
        QvxFileReaderNodeSettings settings = new QvxFileReaderNodeSettings();

        CheckUtils.checkSourceFile(filepath.getStringValue());
        URL url = FileUtil.toURL(filepath.getStringValue());

        settings.setDataFileLocationAndUpdateTableName(url);
        settings.setQvxReader(new QVXReader(filepath.getStringValue(), exec));
        return settings.getQvxReader().getTableData();
    }

    @Override
    protected void reset() {
    }

    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {
        return new DataTableSpec[]{null};
    }


    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        filepath.saveSettingsTo(settings);

    }

    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        filepath.loadSettingsFrom(settings);

    }

    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        filepath.validateSettings(settings);

    }
    
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
    }
    
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
    }

}

