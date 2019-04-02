package edu.njit.qvxwriter;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

class LimitRowsPanel extends JPanel {

	private final JCheckBox rowLimitChecker;
	private final JSpinner rowLimitSpinner;
	
	LimitRowsPanel(){
		
		rowLimitChecker = new JCheckBox("Limit rows:");
		rowLimitSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
		
		add(rowLimitChecker);
		add(rowLimitSpinner);
	}
}
