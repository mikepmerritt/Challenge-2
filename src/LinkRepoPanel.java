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

public class LinkRepoPanel extends JPanel {
	public LinkRepoPanel(LinkRepoWindow linkRepoWindow, MainWindow mainWindow) {
		this.setPreferredSize(new Dimension(400, 250));

		JLabel filePathInstructions = new JLabel("Please input the file path of your Github repository");
		JTextField fileField = new JTextField(25);
		JButton enterButton = new JButton("Save and Submit");
		enterButton.addActionListener(new ActionListener() {
			// on click, the button should writes the user's specified repo file path to a file
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					PrintWriter output = new PrintWriter(new FileOutputStream("repo.txt", true));
					output.println(fileField.getText());
					output.close();
				} catch (FileNotFoundException ex) {
					System.out.println(
							"An error occurred, exiting program. Please manually add your repo to a file named \"repo.txt\" in the correct folder.");
				}
				linkRepoWindow.setVisibility(false);
			}
		});

		this.add(filePathInstructions);
		this.add(fileField);
		this.add(enterButton);
	}

}
