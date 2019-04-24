package edu.njit.qvxwriter;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;

import edu.njit.qvxwriter.QvxWriterNodeSettings.Endianness;

class AdvancedPanel extends JPanel {

	private final JPanel recordSeparatorPanel;
	private final JCheckBox recordSeparatorCheckBox;
	private final JLabel recordSeparatorDescription;
	
	private final JPanel endiannessPanel;
	private final JRadioButton bigEndianButton;
	private final JRadioButton littleEndianButton;
	private final JLabel endiannessDescription;
	
	public AdvancedPanel() {
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		//Record separator panel
		recordSeparatorPanel = new JPanel();
		recordSeparatorPanel.setLayout(new GridBagLayout());
        recordSeparatorPanel.setBorder(new TitledBorder("Record Separator"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        
        recordSeparatorDescription = new JLabel(
            	"<html>Specifies if a record separator byte should be written before each record.</html>");
        
        recordSeparatorCheckBox = new JCheckBox("Use Record Separator");   
        
        
        recordSeparatorPanel.add(recordSeparatorDescription, gbc);
        
        gbc.gridy += 1;
        recordSeparatorPanel.add(recordSeparatorCheckBox, gbc);
        
        //Endianness panel
        endiannessPanel = new JPanel();
        endiannessPanel.setBorder(new TitledBorder("Endianness"));
    	endiannessPanel.setLayout(new GridBagLayout());
        littleEndianButton = new JRadioButton();
        bigEndianButton = new JRadioButton();
        littleEndianButton.setText(Endianness.LITTLE_ENDIAN.toString());
        bigEndianButton.setText(Endianness.BIG_ENDIAN.toString());
        littleEndianButton.setSelected(true);
        ButtonGroup group = new ButtonGroup();
        group.add(littleEndianButton);
        group.add(bigEndianButton);
        
        endiannessDescription = new JLabel(
        	"<html>Specifies the byte-order of the numerical data values within the qvx file.</html>");
        
        gbc = new GridBagConstraints();
        
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.weightx = 0.15;
        gbc.weighty = 0.3;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        endiannessPanel.add(littleEndianButton, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridx += 1;
        endiannessPanel.add(bigEndianButton, gbc);
        
        JPanel filler = new JPanel();
        gbc.weightx = 0.7;
        gbc.gridwidth = 1;
        gbc.gridx += 1;
        endiannessPanel.add(filler, gbc);
        
        gbc.weightx = 1;
        gbc.weighty = 0.2;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        endiannessPanel.add(endiannessDescription, gbc);
        
        add(recordSeparatorPanel);
        add(endiannessPanel);
	}
	
	void saveSettingsInto(final QvxWriterNodeSettings settings) {
		
		//isBigEndian
		Boolean isBigEndian = null;
		if (littleEndianButton.isSelected()) {
			isBigEndian = false;
		}else if (bigEndianButton.isSelected()) {
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
		
		System.out.println("AdvancedPanel.loadValuesIntoPanel()");
		
		boolean isBigEndian = false;
		try {
			isBigEndian = settings.getBoolean(QvxWriterNodeSettings.CFGKEY_IS_BIG_ENDIAN);
		}catch(InvalidSettingsException e) {	
		}
		
		boolean usesRecordSeparator = false;
		try {
			usesRecordSeparator = settings.getBoolean(QvxWriterNodeSettings.CFGKEY_USES_RECORD_SEPARATOR);
		}catch(InvalidSettingsException e) {
		}
		
		//isBigEndian
		if (isBigEndian) {
			bigEndianButton.setSelected(true);
		}else {
			littleEndianButton.setSelected(true);
		}
		
		//usesRecordSeparator
		recordSeparatorCheckBox.setSelected(usesRecordSeparator);
		
		System.out.println("AdvancedPanel done loading values");
	}
}
