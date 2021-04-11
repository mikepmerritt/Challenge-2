import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import git.tools.client.GitSubprocessClient;

public class MainPanel extends JPanel {
	
	// these components can change during runtime, so they can't be declared in the constructor like the others
	private JLabel selectedUser;
	
	// set up the panel and its components
	public MainPanel() {
		super(new GridLayout(5, 1));
		this.setPreferredSize(new Dimension(400, 600));
		
		// title text
		JPanel titlePanel = new JPanel(new BorderLayout());
		JLabel titleLabel = new JLabel("GitHub Helper");
		titleLabel.setFont(new Font("Arial", Font.PLAIN, 32));
		titleLabel.setHorizontalAlignment(JLabel.CENTER);
		selectedUser = new JLabel("Logged in as " + Driver.getUsername());
		selectedUser.setHorizontalAlignment(JLabel.CENTER);
		titlePanel.add(titleLabel, BorderLayout.CENTER);
		titlePanel.add(selectedUser, BorderLayout.SOUTH);

	
		this.add(titlePanel);
		// commit alert
		
		// pull alert
		
		// pull request alert
		
		// other buttons
		JButton repoButton = new JButton("Link a repo");
		repoButtonListener(repoButton);
		this.add(repoButton);
	}
	
	// this updates all of the components that can be updated (the ones not declared in the constructor)
	public void updateWindow() {
		selectedUser.setText("Logged in as " + Driver.getUsername());
	}
	
	// given a local repository, find the username of the owner
	// this name, along with the name of the repo (see getRepoName(String filepath)), are necessary for GitHub commands
	public String getRepoOwner(String filepath) {
		GitSubprocessClient finder = new GitSubprocessClient(filepath);
		String commandResult = finder.runGitCommand("remote get-url origin");
		commandResult += "\n";
		commandResult = commandResult.replace("https://github.com/", "");
		commandResult = commandResult.replace(".git\n", "");
		commandResult = commandResult.substring(0, commandResult.indexOf("/"));
		return commandResult;
	}
	
	// given a local repository, find the name of the remote one
	// this name, along with the username of the repo owner (see getRepoOwner(String filepath)), are necessary for GitHub commands
	public String getRepoName(String filepath) {
		GitSubprocessClient finder = new GitSubprocessClient(filepath);
		String commandResult = finder.runGitCommand("remote get-url origin");
		commandResult += "\n";
		commandResult = commandResult.replace("https://github.com/", "");
		commandResult = commandResult.replace(".git\n", "");
		commandResult = commandResult.substring(commandResult.indexOf("/") + 1);
		return commandResult;
	}
	
	//action listener for the linking repo button
	public void repoButtonListener(JButton repoButton) {
		repoButton.addActionListener(new ActionListener() {
			// on click, the button should the setup window, and try connecting the user again
			@Override
			public void actionPerformed(ActionEvent e) {
				Driver.updateLinkRepoVisibility();
			}
		});
	}

}
