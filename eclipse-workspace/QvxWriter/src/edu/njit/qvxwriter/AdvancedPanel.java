package edu.njit.qvxwriter;

import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;

import edu.njit.qvxwriter.QvxWriterNodeSettings.Endianness;

import static edu.njit.util.Component.radioPanel;

class AdvancedPanel extends JPanel {

	private final JPanel recordSeparatorPanel;
	private final JCheckBox recordSeparatorCheckBox;
	
	private final JPanel endiannessPanel;
	private final JRadioButton endianness_bigEndianButton;
	private final JRadioButton endianness_littleEndianButton;
	
	public AdvancedPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		recordSeparatorPanel = new JPanel();
        recordSeparatorPanel.setBorder(new TitledBorder("Record Separator"));
        recordSeparatorPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        recordSeparatorCheckBox = new JCheckBox("Use Record Separator");
        recordSeparatorPanel.add(recordSeparatorCheckBox);
        
        endianness_littleEndianButton = new JRadioButton();
        endianness_bigEndianButton = new JRadioButton();
        endiannessPanel = radioPanel("Endianness",
        	new String[] {Endianness.LITTLE_ENDIAN.toString(), Endianness.BIG_ENDIAN.toString()},
        	endianness_littleEndianButton, endianness_bigEndianButton
        );
        endianness_littleEndianButton.setSelected(true);
        
        add(recordSeparatorPanel);
        add(endiannessPanel);
	}
	
	void saveSettingsInto(final QvxWriterNodeSettings settings) {
		
		//isBigEndian
		Boolean isBigEndian = null;
		if (endianness_littleEndianButton.isSelected()) {
			isBigEndian = false;
		}else if (endianness_bigEndianButton.isSelected()) {
			isBigEndian = true;
		}
		settings.setIsBigEndian(isBigEndian);
		
		//usesRecordSeparator
		Boolean usesSeparatorByte = null;
		if (recordSeparatorCheckBox.isSelected()) {
			usesSeparatorByte = true;
		}else{
			usesSeparatorByte = false;
		}
		settings.setUsesSeparatorByte(usesSeparatorByte);
	}
	
	void loadValuesIntoPanel(final NodeSettingsRO settings) throws InvalidSettingsException {
		
		boolean isBigEndian = settings.getBoolean(QvxWriterNodeSettings.CFGKEY_IS_BIG_ENDIAN);
		
		boolean usesRecordSeparator = settings.getBoolean(
				QvxWriterNodeSettings.CFGKEY_USES_RECORD_SEPARATOR);
		
		//isBigEndian
		if (isBigEndian) {
			endianness_bigEndianButton.setSelected(true);
		}else {
			endianness_littleEndianButton.setSelected(true);
		}
		
		//usesRecordSeparator
		recordSeparatorCheckBox.setSelected(usesRecordSeparator);	
	}
}
