import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;
import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import github.tools.client.GitHubApiClient;
import github.tools.client.QueryParams;
import github.tools.responseObjects.ListPullRequestsResponse;
import git.tools.client.GitSubprocessClient;

public class MainPanel extends JPanel {
	
	// these components can change during runtime, so they can't be declared in the constructor like the others
	private JLabel selectedUser, pullRequestLabel;
	
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
		
		//pullRequestLabel = new JLabel(pullRequest());
		this.add(repoButton);
		this.add(pullRequestLabel);
	}
	
	// this updates all of the components that can be updated (the ones not declared in the constructor)
	public void updateWindow() {
		selectedUser.setText("Logged in as " + Driver.getUsername());
		//pullRequestLabel.setText(pullRequest());
	}
	
	// this would set the label of the text to show any open pull requests
	/*
	public String pullRequest() {
		GitHubApiClient gitHubApiClient = Driver.getApiClient();
		QueryParams queryParams = new QueryParams();
		queryParams.addParam("state", "open");
		String returnString = " ";
		try {
			File repoFile = new File("repo.txt");
			Scanner fileScan = new Scanner(repoFile);
			String filepath = fileScan.nextLine();
			fileScan.close();			
			returnString = getRepoName(filepath);

			//ListPullRequestsResponse listPullRequestsResponse = gitHubApiClient.listPullRequests(getRepoOwner(filepath), getRepoName(filepath), null);
			
		}
		catch (FileNotFoundException e) {
			returnString = " ";
		}
		return returnString;
	}
	*/
	
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
