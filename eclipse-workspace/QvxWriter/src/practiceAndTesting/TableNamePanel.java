package practiceAndTesting;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


class TableNamePanel extends JPanel {

	private final JTextField tableNameField;
	private final JRadioButton defaultButton;
	private final JRadioButton customButton;
	
	private final Color uneditableColor = new Color(255, 0, 0);
	
	private String defaultName = "";
	private String customName = "";
	
	TableNamePanel(){
        //setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setLayout(new GridBagLayout());
        setBorder(new TitledBorder("Table Name"));
        
        GridBagConstraints gbc = new GridBagConstraints();
                
        tableNameField = new JTextField();
        tableNameField.setEditable(false);
        tableNameField.setPreferredSize(new Dimension(100, 25));
        
        defaultButton = new JRadioButton("Default name");
        defaultButton.setSelected(true);
        
        customButton = new JRadioButton("Custom name");
        
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(defaultButton);
		buttonGroup.add(customButton);
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		//gbc.anchor = GridBagConstraints.WEST;
		//gbc.weightx = 0.5;
		//gbc.weighty = 1;
		
		//gbc.insets = new Insets(0,0,0,0);
		//gbc.gridwidth = 4;
		gbc.weightx = 0.5;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(tableNameField, gbc);
		
		//gbc.gridy += 1;
		gbc.weightx = 0.5;
		gbc.gridx = 0;
		gbc.gridy = 1;
		add(defaultButton, gbc);
		
		gbc.weightx = 0.5;
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		add(customButton, gbc);
		
		//Filler
		gbc.weightx = 0.5;
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
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
				}else {
					defaultName = tableNameField.getText();
					tableNameField.setText(customName);
					tableNameField.setEditable(true);
					if (defaultName.contains("\\")) {
						System.out.println("Possilby VALID");
					}else {
						System.out.println("INVALID");
					}
				}
			}
		});
	}
	
	public void setDefaultName(String name) {
		defaultName = name;
		if (defaultButton.isSelected()) {
			tableNameField.setText(name);
		}
	}
}
