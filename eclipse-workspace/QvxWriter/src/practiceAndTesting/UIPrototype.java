package practiceAndTesting;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.EnumSet;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import javafx.scene.control.RadioButton;

public class UIPrototype {

	static ButtonGroup nameOptions;
	static JRadioButton defaultButton;
	static JRadioButton customButton;
	static TableNamePanel tableNamePanel;
	
	public static void main(String[] argv) {
				
		JFrame frame = new JFrame();
		
		frame.setPreferredSize(new Dimension(700, 700));
		frame.setLocation(1000, 200);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setPreferredSize(new Dimension(1000,1000));

		TableNamePanel panelA = new TableNamePanel();
		TableNamePanel panelB = new TableNamePanel();
		panelA.setPreferredSize(new Dimension(400, 400));
		panelB.setPreferredSize(new Dimension(600, 400));
		
		mainPanel.add(panelA);
		mainPanel.add(panelB);
		mainPanel.setVisible(true);
		
		frame.setContentPane(mainPanel);
		frame.pack();
		frame.setVisible(true);
	}
}
