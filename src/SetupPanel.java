import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SetupPanel extends JPanel {
	public SetupPanel() {
		this.setPreferredSize(new Dimension(400, 150));
		
		JLabel instructions = new JLabel("Please input your GitHub API token.");
		JTextField tokenField = new JTextField(25);
		JButton enterButton = new JButton("Save and Submit");
		enterButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					PrintWriter output = new PrintWriter(new FileOutputStream("token.txt", true));
		            output.println(tokenField.getText());
		            output.close();
				}
				catch (FileNotFoundException ex) {
					System.out.println("An error occurred, exiting program. Please manually add your token to a file named \"token.txt\" in the correct folder.");
				}
			}
		});
		
		this.add(instructions);
		this.add(tokenField);
		this.add(enterButton);
	}
}
