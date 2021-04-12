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
import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import git.tools.client.GitSubprocessClient;
import github.tools.client.GitHubApiClient;
import github.tools.client.QueryParams;
import github.tools.responseObjects.*;

public class MainPanel extends JPanel {
	
	// these components can change during runtime, so they can't be declared in the constructor like the others
	private JLabel selectedUser, pullLabel;
	private GitHubApiClient gitHubApiClient;
	private MainWindow mainWindow;
	
	// set up the panel and its components
	public MainPanel(MainWindow mainWindow) {
		super(new GridLayout(5, 1));
		this.setPreferredSize(new Dimension(400, 600));
		this.mainWindow = mainWindow;
		
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
		JPanel pullPanel = new JPanel(new GridLayout(2, 1));
		pullLabel = new JLabel("Check to see if your added repos are up to date.");
		pullLabel.setHorizontalAlignment(JLabel.CENTER);
		pullLabel.setForeground(Color.black);
		//checkLocalRepositoriesForPulls();
		JPanel pullButtons = new JPanel();
		JButton refreshButton = new JButton("Refresh");
		refreshButton.addActionListener(new ActionListener() {
			// on click, check the repositories again
			@Override
			public void actionPerformed(ActionEvent e) {
				checkLocalRepositoriesForPulls();
			}
		});
		JButton resolveButton = new JButton("Pull down changes");
		refreshButton.addActionListener(new ActionListener() {
			// on click, open the pull window and hide this one
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO: implement
			}
		});
		
		pullButtons.add(refreshButton);
		pullButtons.add(resolveButton);
		pullPanel.add(pullLabel);
		pullPanel.add(pullButtons);
		
		this.add(pullPanel);
		
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
	
	// get an instance of the GitHubApiClient we made earlier so we can use it later
	public void setGitHubApiClient(GitHubApiClient gitHubApiClient) {
		this.gitHubApiClient = gitHubApiClient;
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

	public void checkLocalRepositoriesForPulls() {
		boolean pullNeeded = false;
		GitSubprocessClient repoSearcher;
		try {
			File repoFile = findRepoFile();
			Scanner fileScan = new Scanner(repoFile);
			while(fileScan.hasNext()) {
				// get latest local commit
				String filepath = fileScan.nextLine();
				repoSearcher = new GitSubprocessClient(filepath);
				// same as "git log --oneline -n 1"
				String latestLocalCommit = repoSearcher.gitLogOneLine(1);
				//System.out.println(latestLocalCommit);
				// get latest remote commit
				ListCommitsInRepoResponse remoteCommits = gitHubApiClient.listCommitsInRepo(getRepoOwner(filepath), getRepoName(filepath), null);
				//System.out.println(remoteCommits.getCommits().toString());
			}
			fileScan.close();
		}
		catch (FileNotFoundException e) {
			// TODO: what goes here
		}
		
		if(pullNeeded) {
			pullLabel.setForeground(Color.red);
			pullLabel.setText("One of your local repositories is out of date!");
		}
	}
	
	// used to locate the credential file (token.txt) in the project
	private static File findRepoFile() {
		File currentDirectory = new File(".");
		File repoFile;
		if (currentDirectory.getAbsolutePath().contains("\\src") || currentDirectory.getAbsolutePath().contains("/src")) {
			repoFile = new File("../repo.txt");
		}
		else {
			repoFile = new File("repo.txt");
		}
		return repoFile;
	}
	
}
