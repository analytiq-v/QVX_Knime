package edu.njit.qvxreader;

import javax.swing.JPanel;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.util.FilesHistoryPanel;
import org.knime.core.node.util.FilesHistoryPanel.LocationValidation;
import org.knime.core.node.workflow.FlowVariable;

import edu.njit.qvxreader.QvxReader;
import edu.njit.util.Util;

/**
 * <code>NodeDialog</code> for the "QvxReader" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author 
 */
public class QvxReaderNodeDialog extends NodeDialogPane {

    /**
     * New pane for configuring QvxReader node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
	private final FilesHistoryPanel filePanel;
	
    protected QvxReaderNodeDialog() {
        
        filePanel = 
        	new FilesHistoryPanel(createFlowVariableModel(QvxReaderConfig.CFG_URL, FlowVariable.Type.STRING),
                    "qvx_read", LocationValidation.FileInput, ".qvx");
        addTab("Settings", settingsLayout());
        
        
        /* Test of QvxReader class */
        /*Object[][] results = QvxReader.readFromQvx(Util.PROJECT_DIR + "products.qvx");
        for(int i = 0; i < results.length; i++) {
        	for(int j = 0; j < results.length; j++) {
        		System.out.print(results[i][j]);
        		System.out.print(" ");
        	}
        	System.out.println();
        }*/
        
        /*
        addDialogComponent(new DialogComponentNumber(
                new SettingsModelIntegerBounded(
                    QvxReaderNodeModel.CFGKEY_COUNT,
                    QvxReaderNodeModel.DEFAULT_COUNT,
                    Integer.MIN_VALUE, Integer.MAX_VALUE),
                    "Counter:", 1, 5));
                    */
                    
    }

    protected JPanel settingsLayout() {
    	JPanel jpanel = new JPanel();
    	jpanel.add(filePanel);
    	return jpanel;
    }
    
	@Override
	protected void saveSettingsTo(NodeSettingsWO settings) throws InvalidSettingsException {
		// TODO Auto-generated method stub
	}
	
	/** {@inheritDoc} */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings,
            final DataTableSpec[] specs) throws NotConfigurableException {
        /*CSVReaderConfig config = new CSVReaderConfig();
        config.loadSettingsInDialog(settings);
        m_filePanel.updateHistory();
        m_filePanel.setSelectedFile(config.getLocation());
        m_filePanel.setConnectTimeout(config.getConnectTimeout());
        m_colDelimiterField.setText(escape(config.getColDelimiter()));
        m_rowDelimiterField.setText(escape(config.getRowDelimiter()));
        m_quoteStringField.setText(config.getQuoteString());
        m_commentStartField.setText(config.getCommentStart());
        m_hasColHeaderChecker.setSelected(config.hasColHeader());
        m_hasRowHeaderChecker.setSelected(config.hasRowHeader());
        m_supportShortLinesChecker.setSelected(config.isSupportShortLines());
        int skipFirstLinesCount = config.getSkipFirstLinesCount();
        if (skipFirstLinesCount > 0) {
            m_skipFirstLinesChecker.setSelected(true);
            m_skipFirstLinesSpinner.setValue(skipFirstLinesCount);
        } else {
            m_skipFirstLinesChecker.setSelected(false);
            m_skipFirstLinesSpinner.setValue(1);
        }
        long limitRowsCount = config.getLimitRowsCount();
        if (limitRowsCount >= 0) { // 0 is allowed -- will only read header
            m_limitRowsChecker.setSelected(true);
            m_limitRowsSpinner.setValue(limitRowsCount);
        } else {
            m_limitRowsChecker.setSelected(false);
            m_limitRowsSpinner.setValue(50);
        }
        int limitAnalysisCount = config.getLimitAnalysisCount();
        if (limitAnalysisCount >= 0) { // 0 is allowed -- will only read header
            m_limitAnalysisChecker.setSelected(true);
            m_limitAnalysisSpinner.setValue(limitAnalysisCount);
        } else {
            m_limitAnalysisChecker.setSelected(false);
            m_limitAnalysisSpinner.setValue(50);
        }
        m_encodingPanel.loadSettings(getEncodingSettings(config));
        */
    }
}

