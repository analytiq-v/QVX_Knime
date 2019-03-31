package edu.njit.qvxwriter;

import edu.njit.qvxwriter.QvxWriterNodeSettings.Endianness;
import edu.njit.qvxwriter.QvxWriterNodeSettings.OverwritePolicy;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
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

import edu.njit.qvx.QvxTableHeader;
import edu.njit.qvxwriter.QvxWriter;

import static edu.njit.util.Util.removeSuffix;

/**
 * <code>NodeDialog</code> for the "QvxWriter" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author 
 */
public class QvxWriterNodeDialog extends NodeDialogPane {
		
	//settingsPanel and all of its sub-components
	private final JPanel settingsPanel;
	
	private final JPanel filesPanel;
	private final FilesHistoryPanel filesHistoryPanel;
	
	private final JPanel recordSeparatorPanel;
	private final JCheckBox recordSeparatorCheckBox;
	
	private final JPanel overwritePolicyPanel;
	private final JRadioButton overwritePolicy_abortButton;
	private final JRadioButton overwritePolicy_overwriteButton;
	
	private final JPanel endiannessPanel;
	private final JRadioButton endianness_bigEndianButton;
	private final JRadioButton endianness_littleEndianButton;
		
    protected QvxWriterNodeDialog() {
        super();
                
        // Settings panel
        settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        
        filesPanel = new JPanel();
        filesPanel.setBorder(new TitledBorder("Output Location"));
        filesHistoryPanel = new FilesHistoryPanel(
        		createFlowVariableModel("CFGKEY_FILE", FlowVariable.Type.STRING),
        		"History ID", LocationValidation.FileOutput, ".qvx");
        filesPanel.add(filesHistoryPanel);
                
        recordSeparatorPanel = new JPanel();
        recordSeparatorPanel.setBorder(new TitledBorder("Record Separator"));
        recordSeparatorPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        recordSeparatorCheckBox = new JCheckBox("Use Record Separator");
        recordSeparatorPanel.add(recordSeparatorCheckBox);
        
        overwritePolicy_abortButton = new JRadioButton();
        overwritePolicy_overwriteButton = new JRadioButton();
        overwritePolicyPanel = radioPanel("If file exists...",
        	new String[] {OverwritePolicy.ABORT.toString(), OverwritePolicy.OVERWRITE.toString()},
        	overwritePolicy_abortButton, overwritePolicy_overwriteButton
        );
        overwritePolicy_abortButton.setSelected(true);
        
        endianness_littleEndianButton = new JRadioButton();
        endianness_bigEndianButton = new JRadioButton();
        endiannessPanel = radioPanel("Endianness",
        	new String[] {Endianness.LITTLE_ENDIAN.toString(), Endianness.BIG_ENDIAN.toString()},
        	endianness_littleEndianButton, endianness_bigEndianButton
        );
        endianness_littleEndianButton.setSelected(true);
        
        settingsPanel.add(filesPanel);
        settingsPanel.add(recordSeparatorPanel);
        settingsPanel.add(overwritePolicyPanel);
        settingsPanel.add(endiannessPanel);
        
        addTab("Settings", settingsPanel);     
    }
    
	@Override
	protected void saveSettingsTo(NodeSettingsWO settings) throws InvalidSettingsException {
		System.out.println("NodeDialog: saveSettingsTo()");
		QvxWriterNodeSettings m_settings = new QvxWriterNodeSettings();
		
		//isBigEndian
		Boolean isBigEndian = null;
		if (endianness_littleEndianButton.isSelected()) {
			isBigEndian = false;
		}else if (endianness_bigEndianButton.isSelected()) {
			isBigEndian = true;
		}
		m_settings.setIsBigEndian(isBigEndian);
		
		//fileName
		String fileName = removeSuffix(filesHistoryPanel.getSelectedFile(), ".qvx") + ".qvx";
		m_settings.setFileName(fileName);
		
		//overwritePolicy
		OverwritePolicy overwritePolicy = null;
		if (overwritePolicy_abortButton.isSelected()) {
			overwritePolicy = OverwritePolicy.ABORT;
		}else if (overwritePolicy_overwriteButton.isSelected()) {
			overwritePolicy = OverwritePolicy.OVERWRITE;
		}
		m_settings.setOverwritePolicy(overwritePolicy);
		
		//usesRecordSeparator
		Boolean usesSeparatorByte = null;
		if (recordSeparatorCheckBox.isSelected()) {
			usesSeparatorByte = true;
		}else{
			usesSeparatorByte = false;
		}
		m_settings.setUsesSeparatorByte(usesSeparatorByte);
		
		m_settings.saveSettingsTo(settings);
	}
	
	@Override
	protected void loadSettingsFrom(final NodeSettingsRO settings,
            final DataTableSpec[] specs) throws NotConfigurableException {
		
		System.out.println("NodeDialog: loadSettingsFrom()");
		try {
			System.out.println("IsBigEndian" + 
					settings.getBoolean(QvxWriterNodeSettings.CFGKEY_IS_BIG_ENDIAN));
			System.out.println("File name " + settings.getString(QvxWriterNodeSettings.CFGKEY_FILE_NAME));
			System.out.println("Overwrite policy " +
					settings.getString(QvxWriterNodeSettings.CFGKEY_OVERWRITE_POLICY));
			System.out.println("Uses separator byte " +
					settings.getBoolean(QvxWriterNodeSettings.CFGKEY_USES_RECORD_SEPARATOR));
		
			String fileName = settings.getString(QvxWriterNodeSettings.CFGKEY_FILE_NAME);
			boolean usesRecordSeparator = settings.getBoolean(
					QvxWriterNodeSettings.CFGKEY_USES_RECORD_SEPARATOR);
			String overwritePolicy = settings.getString(
					QvxWriterNodeSettings.CFGKEY_OVERWRITE_POLICY);
			boolean isBigEndian = settings.getBoolean(QvxWriterNodeSettings.CFGKEY_IS_BIG_ENDIAN);
			
			//isBigEndian
			if (isBigEndian) {
				endianness_bigEndianButton.setSelected(true);
			}else {
				endianness_littleEndianButton.setSelected(true);
			}
			
			//fileName
			filesHistoryPanel.setSelectedFile(fileName);
			
			//overwritePolicy
			if (overwritePolicy.equals(OverwritePolicy.ABORT)){
				overwritePolicy_abortButton.setSelected(true);
			}else if (overwritePolicy.equals(OverwritePolicy.OVERWRITE)) {
				overwritePolicy_overwriteButton.setSelected(true);
			}
			
			//usesRecordSeparator
			recordSeparatorCheckBox.setSelected(usesRecordSeparator);
				
		} catch (InvalidSettingsException e) {
			e.printStackTrace();
		}
	}
	
	 
    public static JPanel radioPanel(String title, String[] buttonTexts, JRadioButton... radioButtons) {
		
		if (buttonTexts.length != radioButtons.length) {
			throw new RuntimeException("Number of button texts must match number of buttons");
		}
		
		JPanel panel = new JPanel();
		ButtonGroup buttonGroup = new ButtonGroup();
		
		panel.setBorder(new TitledBorder(title));
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		for(int i = 0; i < buttonTexts.length; i++) {
			radioButtons[i].setText(buttonTexts[i]);
			panel.add(radioButtons[i]);
			buttonGroup.add(radioButtons[i]);
		}
		return panel;
	}
}
