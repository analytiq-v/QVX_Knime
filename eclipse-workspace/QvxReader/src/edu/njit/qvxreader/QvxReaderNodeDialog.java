package edu.njit.qvxreader;

import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.util.FilesHistoryPanel;
import org.knime.core.node.util.FilesHistoryPanel.LocationValidation;
import org.knime.core.node.workflow.FlowVariable;

import edu.njit.qvxreader.QvxReaderNodeSettings;

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

	
	private final JPanel settingsPanel;
	
	private final JPanel filesPanel;
	private final FilesHistoryPanel filesHistoryPanel;
    /**
     * New pane for configuring QvxReader node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected QvxReaderNodeDialog() {
    
    	super();
    	
    	// Settings panel
        settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        
        filesPanel = new JPanel();
        filesPanel.setBorder(new TitledBorder("Input Location"));
        filesHistoryPanel = new FilesHistoryPanel(
        		createFlowVariableModel("CFGKEY_FILE", FlowVariable.Type.STRING),
        		"History ID", LocationValidation.FileInput, ".qvx");
        filesHistoryPanel.setDialogType(JFileChooser.OPEN_DIALOG);
        filesPanel.add(filesHistoryPanel);
        
        settingsPanel.add(filesPanel);
        
        addTab("Settings", settingsPanel);
    }

	@Override
	protected void saveSettingsTo(NodeSettingsWO settings) throws InvalidSettingsException {
		
		System.out.println("NodeDialog: saveSettingsTo()");
		QvxReaderNodeSettings m_settings = new QvxReaderNodeSettings();
	
		//fileName
		String fileName = filesHistoryPanel.getSelectedFile();
		m_settings.setFileName(fileName);
		
		m_settings.saveSettingsTo(settings);
	}
	
	@Override
	protected void loadSettingsFrom(final NodeSettingsRO settings,
            final DataTableSpec[] specs) throws NotConfigurableException {
		
		System.out.println("NodeDialog: loadSettingsFrom()");
		try {	
			String fileName = settings.getString(QvxReaderNodeSettings.CFGKEY_FILE_NAME);
			
			//fileName
			filesHistoryPanel.setSelectedFile(fileName);
			
		} catch (InvalidSettingsException e) {
			e.printStackTrace();
		}
	}
}

