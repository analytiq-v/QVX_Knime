package edu.njit.util;

import java.awt.FlowLayout;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

public class Component {

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
