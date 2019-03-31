package practiceAndTesting;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
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

	static ButtonGroup rsGroup;
	static ButtonGroup overwritePolicyGroup;
	static ButtonGroup endiannessGroup;
	
	static enum OverwritePolicyOption {
		ABORT("Abort"),
		OVERWRITE("Overwrite");
		
		public final String name;
		
		private OverwritePolicyOption(String _name) {
			name = _name;
		}
		
		public String toString() {
			return name;
		}		
	}
	
	public static void main(String[] argv) {
		
		GridBagConstraints c = new GridBagConstraints();
		
		JFrame frame = new JFrame();
		frame.setSize(700, 700);
		frame.setLocation(1000, 200);
		JPanel mainPanel = new JPanel();
		mainPanel.setBorder(new TitledBorder("Main Panel"));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		JPanel rsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		rsPanel.setBorder(new TitledBorder("RS"));		JCheckBox rsCheckBox = new JCheckBox("Use Record Separator", false);
		rsPanel.add(rsCheckBox);
		
		/*JPanel overwritePolicyPanel = new JPanel();
		overwritePolicyPanel.setBorder(new TitledBorder("Overwrite Policy"));
		*/
		
		final JRadioButton bigEndianButton = new JRadioButton();
		final JRadioButton littleEndianButton = new JRadioButton();
		JPanel endiannessPanel =
			radioPanel("Endianness",
					new String[] {"Little-Endian", "Big-Endian"},
					littleEndianButton, bigEndianButton
			);
		mainPanel.add(rsPanel);
		mainPanel.add(endiannessPanel);
		mainPanel.setVisible(true);

		if (littleEndianButton == null) {
			System.out.println("Little Endian button is null for some reason...");
		}
		if (bigEndianButton == null) {
			System.out.println("Big Endian button is null for some reason...");
		}
		
		frame.setContentPane(mainPanel);
		frame.setVisible(true);
	}
	
	public static JPanel radioPanel(String title, String[] buttonTexts, JRadioButton... radioButtons) {
		
		if (buttonTexts.length != radioButtons.length) {
			throw new RuntimeException("Numbero of button texts must match number of buttons");
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
