package edu.njit.qvxwriter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.math.BigInteger;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;

import edu.njit.qvx.FieldAttrType;
import edu.njit.qvx.FieldAttributes;
import edu.njit.qvx.QvxFieldType;

import static edu.njit.qvxwriter.QvxWriterNodeSettings.CFGKEY_SELECTED_FIELD_ATTRS;
import static edu.njit.qvxwriter.QvxWriterNodeSettings.CFGKEY_SELECTED_N_DECS;

class FieldAttrPanel extends JPanel {

	private JLabel columnNameHeader;
	private JLabel fieldAttrHeader;
	private JLabel nDecHeader;
	private JLabel[] columnNameFields;
	private JComboBox[] attributeSelects;
	private JSpinner[] nDecSpinners;
				
	FieldAttrPanel() {
		//dfsdaf//TODO: Resolve "settings not saving" problem
	}
	
	void saveSettingsInto(final QvxWriterNodeSettings settings) {
		
		String[] selectedFieldAttrs = new String[attributeSelects.length];
		for(int i = 0; i < selectedFieldAttrs.length; i++) {
			selectedFieldAttrs[i] = (String)attributeSelects[i].getSelectedItem();
		}
		System.out.println("saveSettingsInto: selectedFieldAttr: " + Arrays.toString(selectedFieldAttrs));
		
		int[] selectedNDecs = new int[nDecSpinners.length];
		for(int i = 0; i < selectedNDecs.length; i++) {
			selectedNDecs[i] = (int)nDecSpinners[i].getValue();
		}
		System.out.println("saveSettingsInto: selectedNDecs: " + Arrays.toString(selectedNDecs));
		
		settings.setSelectedFieldAttrs(selectedFieldAttrs);
		settings.setSelectedNDecs(selectedNDecs);
	}
	
	void loadValuesIntoPanel(final NodeSettingsRO settings, DataTableSpec spec) throws InvalidSettingsException {

		int numColumns = spec.getNumColumns();
		
		setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        double COLUMN_NAME_WEIGHT_X = 0.7;
        double FIELD_ATTR_WEIGHT_X = 0.7;
        double N_DEC_WEIGHT_X = 0.2;
        double FILLER_WEIGHT_X = 0.2; //Fill up the remaining space

		//Clear the panel
		removeAll();

		//Add the column headers
		columnNameHeader = new JLabel("Column Name");
		fieldAttrHeader = new JLabel("Field Attribute");
		nDecHeader = new JLabel("# Decimals");
				
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(10, 30, 0, 0);
		
		gbc.weightx = COLUMN_NAME_WEIGHT_X;
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(columnNameHeader, gbc);
		
		gbc.weightx = FIELD_ATTR_WEIGHT_X;
		gbc.gridx += 1;
		add(fieldAttrHeader, gbc);
		
		gbc.weightx = N_DEC_WEIGHT_X;
		gbc.gridx += 1;
		add(nDecHeader, gbc);
		
		gbc.weightx = FILLER_WEIGHT_X;
		gbc.gridx += 1;
		add(new JPanel(), gbc);
		
		//Add the rows
		String[] selectedFieldAttrs = settings.getStringArray(CFGKEY_SELECTED_FIELD_ATTRS);
		int[] nDecs = settings.getIntArray(CFGKEY_SELECTED_N_DECS);
		
		columnNameFields = new JLabel[numColumns];
		attributeSelects = new JComboBox[numColumns];
		nDecSpinners = new JSpinner[numColumns];		
					
		for(int i = 0; i < numColumns; i++) {
			DataColumnSpec columnSpec = spec.getColumnSpec(i);
			
			columnNameFields[i] = new JLabel(columnSpec.getName());
			attributeSelects[i] = new JComboBox(getAllowedAttrTypes(columnSpec));
			nDecSpinners[i] = new JSpinner(new SpinnerNumberModel(nDecs != null ? nDecs[i] : 0, 0, Integer.MAX_VALUE, 1));

			if (selectedFieldAttrs == null) {
				System.out.println("selectedFieldAttrs is null******");
				attributeSelects[i].setSelectedItem(getDefaultAttrType(columnSpec));	
			}else {
				System.out.println("selected field Attr!!!!" + selectedFieldAttrs[i]);
				attributeSelects[i].setSelectedItem(selectedFieldAttrs[i]);
			}
			
			gbc.weightx = COLUMN_NAME_WEIGHT_X;
			gbc.gridy += 1;
			gbc.gridx = 0;
			add(columnNameFields[i], gbc);
			
			gbc.weightx = FIELD_ATTR_WEIGHT_X;
			gbc.gridx += 1;
			add(attributeSelects[i], gbc);
			
			gbc.weightx = N_DEC_WEIGHT_X;
			gbc.gridx += 1;
			nDecSpinners[i].setPreferredSize(new Dimension(45, 30));
			add(nDecSpinners[i], gbc);
			
			gbc.weightx = FILLER_WEIGHT_X; //Fill the remaining space
			gbc.gridx += 1;
			add(new JPanel(), gbc);
		}
	}
	
	String[] getAllowedAttrTypes(DataColumnSpec spec) {
		
		//The allowed FieldAttrTypes are different for each KNIME DataType; this method gets the allowed FieldAttrTypes for spec.getType()
		
		String type = spec.getType().getName();
		if (type.equals("Number (integer)")) {
			return new String[] {
					FieldAttrType.INTEGER.value(),
					FieldAttrType.REAL.value(),
					FieldAttrType.FIX.value(),
					FieldAttrType.MONEY.value(),
					FieldAttrType.UNKNOWN.value()
			};
		}else if (type.equals("Number (double)")) {
			return new String[] {
					FieldAttrType.UNKNOWN.value(),
					FieldAttrType.FIX.value(),
					FieldAttrType.REAL.value(),
					FieldAttrType.MONEY.value()
			};
		}else if (type.equals("String")) {
			return new String[] {
					FieldAttrType.ASCII.value(),
					FieldAttrType.INTERVAL.value(),
					FieldAttrType.UNKNOWN.value()
			};
		}else if (type.equals("Local Date Time")) {
			return new String[] {
					FieldAttrType.TIMESTAMP.value(),
					FieldAttrType.UNKNOWN.value()
			};
		}else if (type.equals("Local Date")) {
			return new String[] {
					FieldAttrType.DATE.value(),
					FieldAttrType.UNKNOWN.value()
			};
		}else if (type.equals("Local Time")) {
			return new String[] {
					FieldAttrType.TIME.value(),
					FieldAttrType.UNKNOWN.value()
			};
		}else {
			throw new RuntimeException("Coding error in FieldAttrPanel.java: Unrecognized KNIME type: " + type);
		}
	}
	
	String getDefaultAttrType(DataColumnSpec spec) {

		//Get the default FieldAttrType of spec.getType()
		
		String type = spec.getType().getName();
		if (type.equals("Number (integer)")) {
			return FieldAttrType.INTEGER.value();
		}else if (type.equals("Number (double)")) {
			return FieldAttrType.UNKNOWN.value();
		}else if (type.equals("String")) {
			return FieldAttrType.ASCII.value();
		}else if (type.equals("Local Date Time")) {
			return FieldAttrType.TIMESTAMP.value();
		}else if (type.equals("Local Date")) {
			return FieldAttrType.DATE.value();
		}else if (type.equals("Local Time")) {
			return FieldAttrType.TIME.value();
		}else {
			throw new RuntimeException("Coding error in FieldAttrPanel.java: Unrecognized KNIME type: " + type);
		}
	}
}