import java.awt.Color;
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

import git.tools.client.GitSubprocessClient;

public class LinkRepoPanel extends JPanel {
	
	private JLabel filePathInstructions;
	private JButton enterButton;
	private JTextField fileField;
	public LinkRepoPanel(LinkRepoWindow linkRepoWindow, MainWindow mainWindow) {
		this.setPreferredSize(new Dimension(400, 250));

		filePathInstructions = new JLabel("Please input the file path of your Github repository");
		fileField = new JTextField(25);
		enterButton = new JButton("Save and Submit");
		enterButton.addActionListener(new ActionListener() {
			// on click, the button should writes the user's specified repo file path to a
			// file, or say that it failed on the main screen and write nothing
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					GitSubprocessClient pathCheck = new GitSubprocessClient(fileField.getText());
					pathCheck.runGitCommand("status");
					PrintWriter output = new PrintWriter(new FileOutputStream("repo.txt", true));
					output.println(fileField.getText());
					output.close();
					mainWindow.setLinkFailLabel(false);
				} catch (FileNotFoundException ex) {
					System.out.println(
							"An error occurred, exiting program. Please manually add your repo to a file named \"repo.txt\" in the correct folder.");
				} catch (RuntimeException exc) {
					// bad path, so nothing gets written
					mainWindow.setLinkFailLabel(true);
				}
				linkRepoWindow.setVisibility(false);
			}
		});

		this.add(filePathInstructions);
		this.add(fileField);
		this.add(enterButton);
	}

	// changes the theme based on what has been passed through 
	public void updateTheme(boolean theme) {
		if (!theme) {
			Color defaultButtonColor = new JButton().getBackground();
			
			this.setBackground(Color.white);
			filePathInstructions.setForeground(Color.black);
			enterButton.setBackground(defaultButtonColor);
			enterButton.setForeground(Color.darkGray);
			fileField.setBackground(Color.white);
			fileField.setForeground(Color.black);
			
		} else {
			this.setBackground(Color.DARK_GRAY);
			filePathInstructions.setForeground(Color.white);
			enterButton.setBackground(Color.gray);
			enterButton.setForeground(Color.white);
			fileField.setBackground(Color.lightGray);
			fileField.setForeground(Color.darkGray);
		}
	}

}
