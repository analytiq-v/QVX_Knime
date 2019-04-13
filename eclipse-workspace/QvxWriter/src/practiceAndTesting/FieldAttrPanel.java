package practiceAndTesting;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

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
import edu.njit.qvxwriter.QvxWriterNodeSettings;

class FieldAttrPanel extends JPanel {

	private JLabel columnNameHeader;
	private JLabel fieldAttrHeader;
	private JLabel nDecHeader;
	private JLabel[] columnNameFields;
	private JComboBox[] attributeSelects;
	private JSpinner[] nDecSpinners;
				
	FieldAttrPanel() {
		
		int numColumns = 6;

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
		nDecHeader = new JLabel("nDec");
				
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
		nDecHeader.setPreferredSize(new Dimension(30, 30));
		add(nDecHeader, gbc);
		
		gbc.weightx = FILLER_WEIGHT_X;
		gbc.gridx += 1;
		add(new JPanel(), gbc);
		
		//Add the rows
		String[] fieldAttrTypes = null;
		int[] nDecs = null;
		
		columnNameFields = new JLabel[numColumns];
		attributeSelects = new JComboBox[numColumns];
		nDecSpinners = new JSpinner[numColumns];
					
		for(int i = 0; i < numColumns; i++) {
			
			columnNameFields[i] = new JLabel("Name ...");
			attributeSelects[i] = new JComboBox(new String[] {"4", "4"});
			nDecSpinners[i] = new JSpinner(new SpinnerNumberModel(nDecs != null ? nDecs[i] : 0, 0, Integer.MAX_VALUE, 1));

			if (fieldAttrTypes == null) {
				attributeSelects[i].setSelectedItem(new String[] {"4", "5"});	
			}else {
				attributeSelects[i].setSelectedItem(FieldAttrType.fromValue(fieldAttrTypes[i]));
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
			nDecSpinners[i].setPreferredSize(new Dimension(30, 30));
			add(nDecSpinners[i], gbc);
			
			gbc.weightx = FILLER_WEIGHT_X; //Fill the remaining space
			gbc.gridx += 1;
			add(new JPanel(), gbc);
		}
	}
	
	String[] getAllowedAttrTypes(DataColumnSpec spec) {
		return new String[] {"test 1", "test 2"};
	}
	
	String getDefaultAttrType(DataColumnSpec spec) {
		return "default";
	}
}
