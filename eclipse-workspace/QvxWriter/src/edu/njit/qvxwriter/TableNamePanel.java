package edu.njit.qvxwriter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;

import static edu.njit.qvxwriter.QvxWriterNodeSettings.CFGKEY_CUSTOM_TABLE_NAME;
import static edu.njit.qvxwriter.QvxWriterNodeSettings.CFGKEY_DEFAULT_TABLE_NAME;
import static edu.njit.qvxwriter.QvxWriterNodeSettings.CFGKEY_USE_DEFAULT_TABLE_NAME;

class TableNamePanel extends JPanel {

	private final JTextField tableNameField;
	private final JRadioButton defaultButton;
	private final JRadioButton customButton;
	
	private final Color UNEDITABLE_COLOR = new Color(200, 200, 200);
	
	private String defaultName = "";
	private String customName = "";
	
	TableNamePanel(){
		
		setLayout(new GridBagLayout());
        setBorder(new TitledBorder("Table Name"));
        
        GridBagConstraints gbc = new GridBagConstraints();
                
        tableNameField = new JTextField();
        tableNameField.setEditable(false);
        tableNameField.setPreferredSize(new Dimension(85, 25));
        tableNameField.setBackground(UNEDITABLE_COLOR);
        
        defaultButton = new JRadioButton("Default name");
        defaultButton.setSelected(true);
        
        customButton = new JRadioButton("Custom name");
        
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(defaultButton);
		buttonGroup.add(customButton);
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets = new Insets(5, 5, 5, 5);	
		
		gbc.weightx = 0.2;
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(tableNameField, gbc);
		
		gbc.gridy += 1;
		add(defaultButton, gbc);
		
		gbc.gridy += 1;
		add(customButton, gbc);
		
		//Invisible panel, in order to fill the empty space
		gbc.weightx = 1;
		gbc.gridx = 1;
		gbc.gridy = 0;
		add(new JPanel(), gbc);
		
		addListeners();
	}
	
	void addListeners() {
		defaultButton.addItemListener(new ItemListener() {
			@Override
            public void itemStateChanged(final ItemEvent e) {
				if (defaultButton.isSelected()) {
					customName = tableNameField.getText();
					tableNameField.setText(defaultName);
					tableNameField.setEditable(false);
					tableNameField.setBackground(UNEDITABLE_COLOR);
				}else {
					defaultName = tableNameField.getText();
					tableNameField.setText(customName);
					tableNameField.setEditable(true);
					tableNameField.setBackground(Color.WHITE);
				}
			}
		});
	}
	
	void setDefaultName(String name) {
		defaultName = name;
		if (defaultButton.isSelected()) {
			tableNameField.setText(name);
		}
	}
	
	void saveSettingsInto(final QvxWriterNodeSettings settings) {
		
		if (defaultButton.isSelected()) {
			defaultName = tableNameField.getText();
			settings.setTableName(defaultName);
		}else {
			customName = tableNameField.getText();
			settings.setTableName(customName);
		}
		settings.setCustomTableName(customName);
		settings.setDefaultTableName(defaultName);
		settings.setUseDefaultTableName(defaultButton.isSelected());
	}
	
	void loadValuesIntoPanel(final NodeSettingsRO settings) throws InvalidSettingsException {
		
		customName = settings.getString(CFGKEY_CUSTOM_TABLE_NAME);
		defaultName = settings.getString(CFGKEY_DEFAULT_TABLE_NAME);
		boolean useDefaultTableName = settings.getBoolean(CFGKEY_USE_DEFAULT_TABLE_NAME);
		
		customButton.setSelected(!useDefaultTableName);
		defaultButton.setSelected(useDefaultTableName);
		if (useDefaultTableName) {
			tableNameField.setText(defaultName);
			tableNameField.setEditable(false);
			tableNameField.setBackground(UNEDITABLE_COLOR);
		}else {
			tableNameField.setText(customName);
			tableNameField.setEditable(true);
			tableNameField.setBackground(Color.WHITE);
		}
	}
}
