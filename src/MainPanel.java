import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import github.tools.client.GitHubApiClient;
import github.tools.client.QueryParams;
import git.tools.client.GitSubprocessClient;
import github.tools.client.GitHubApiClient;
import github.tools.client.QueryParams;
import github.tools.responseObjects.*;

public class MainPanel extends JPanel {

	// these components can change during runtime, so they can't be declared in the
	// constructor like the others

	private JLabel selectedUser, pullRequestLabel;
	private JLabel pullLabel;
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
		pullLabel = new JLabel("Check to see if your added repos are up to date.", SwingConstants.CENTER);
		pullLabel.setHorizontalAlignment(JLabel.CENTER);
		pullLabel.setForeground(Color.black);
		// checkLocalRepositoriesForPulls();
		JPanel pullButtons = new JPanel();
		JButton refreshButton = new JButton("Refresh");
		refreshButton.addActionListener(new ActionListener() {
			// on click, check the repositories again
			@Override
			public void actionPerformed(ActionEvent e) {
				pullLabel.setText("Checking all added repos...");
				checkLocalRepositoriesForPulls();
			}
		});
		JButton resolveButton = new JButton("Pull down changes");
		resolveButton.addActionListener(new ActionListener() {
			// on click, open the pull window and hide this one
			@Override
			public void actionPerformed(ActionEvent e) {
				pullAllRepositories();
			}
		});

		pullButtons.add(refreshButton);
		pullButtons.add(resolveButton);
		pullPanel.add(pullLabel);
		pullPanel.add(pullButtons);

		this.add(pullPanel);

		// pull request alert
		JPanel pullRequestPanel = new JPanel();
		pullRequestLabel = new JLabel("Check for open pull requests");
		pullRequestLabel.setHorizontalAlignment(JLabel.CENTER);
		JButton pullRequestRefreshButton = new JButton("Refresh");
		pullRequestRefreshButton.addActionListener(new ActionListener() {
			// on click, check the pull requests again  again
			@Override
			public void actionPerformed(ActionEvent e) {
				pullRequest();
			}
		});

		// other buttons
		
		// button to link the repository 
		JButton repoButton = new JButton("Link a repo");
		repoButtonListener(repoButton);
		pullRequestPanel.add(pullRequestLabel);
		pullRequestPanel.add(pullRequestRefreshButton);
		this.add(repoButton);
		
		this.add(pullRequestPanel);
	}

	// this updates all of the components that can be updated (the ones not declared
	// in the constructor)
	public void updateWindow() {
		selectedUser.setText("Logged in as " + Driver.getUsername());
	}

	// this checks the repos for open pull requests and updates the label 
	public void pullRequest() {
		// used to only get the open pull requests 
		QueryParams queryParams = new QueryParams();
		queryParams.addParam("state", "open");
		// try catch to make sure it only checks the repo file if it is there 
		try {
			File repoFile = findRepoFile();
			Scanner fileScan = new Scanner(repoFile);
			String pulls = "There are no open pull requests";
			// loop through any repos in the file and check for open pulls 
			while (fileScan.hasNext()) {
				String filepath = fileScan.nextLine();
				ListPullRequestsResponse listPullRequestsResponse = gitHubApiClient.listPullRequests(getRepoOwner(filepath), getRepoName(filepath).trim(), queryParams);
				ArrayList<PullRequest> openPullRequests = listPullRequestsResponse.getPullRequests();
				// if there are open pulls, displays to the user 
				if (openPullRequests.size() > 0) {
					pulls = "There are open pull requests on the repo " + getRepoName(filepath) + ": ";
					for (int i = 0; i < openPullRequests.size(); i++) {
						pulls = pulls + openPullRequests.get(i).getTitle() + " ";
					}
				}
			}
			pullRequestLabel.setText(pulls);
			fileScan.close();

		} catch (FileNotFoundException e) {
			pullRequestLabel.setText("There are no linked repos to search");
		}
	}

	// get an instance of the GitHubApiClient we made earlier so we can use it later
	public void setGitHubApiClient(GitHubApiClient gitHubApiClient) {
		this.gitHubApiClient = gitHubApiClient;
	}

	// given a local repository, find the username of the owner
	// this name, along with the name of the repo (see getRepoName(String
	// filepath)), are necessary for GitHub commands
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
	// this name, along with the username of the repo owner (see getRepoOwner(String
	// filepath)), are necessary for GitHub commands
	public String getRepoName(String filepath) {
		GitSubprocessClient finder = new GitSubprocessClient(filepath);
		String commandResult = finder.runGitCommand("remote get-url origin");
		commandResult += "\n";
		commandResult = commandResult.replace("https://github.com/", "");
		commandResult = commandResult.replace(".git\n", "");
		commandResult = commandResult.substring(commandResult.indexOf("/") + 1);
		return commandResult;
	}

	// action listener for the linking repo button
	public void repoButtonListener(JButton repoButton) {
		repoButton.addActionListener(new ActionListener() {
			// on click, the button should the setup window, and try connecting the user
			// again
			@Override
			public void actionPerformed(ActionEvent e) {
				Driver.updateLinkRepoVisibility();
			}
		});
	}

	public void checkLocalRepositoriesForPulls() {
		pullLabel.setForeground(Color.black);
		pullLabel.setText("Checking all added repos...");
		pullLabel.paintImmediately(pullLabel.getVisibleRect());
		boolean pullNeeded = false;
		GitSubprocessClient repoSearcher;
		try {
			File repoFile = findRepoFile();
			Scanner fileScan = new Scanner(repoFile);
			while (fileScan.hasNext()) {
				// get local commits
				String filepath = fileScan.nextLine();
				repoSearcher = new GitSubprocessClient(filepath);
				// run "git log --oneline"
				String localCommits = repoSearcher.gitLogAllOneLine();
				// get remote commits
				// run "git branch --show-current"
				String currentBranch = repoSearcher.runGitCommand("branch --show-current");
				QueryParams queryParams = new QueryParams();
				queryParams.addParam("sha", currentBranch);
				ListCommitsInRepoResponse remoteCommits = gitHubApiClient.listCommitsInRepo(getRepoOwner(filepath),
						getRepoName(filepath).trim(), queryParams);
				// make sure every remote commit is in local commits (meaning you are up-to-date
				// or ahead)
				for (Commit commit : remoteCommits.getCommits()) {
					String remoteCommitHash = commit.getCommitHash().substring(0, 7);
					if (!localCommits.contains(remoteCommitHash)) {
						pullNeeded = true;
					}
				}
			}
			fileScan.close();
		} catch (FileNotFoundException e) {
			pullLabel.setForeground(Color.red);
			pullLabel.setText("You don't have any repos added yet!");
		}

		if (pullNeeded) {
			pullLabel.setForeground(Color.red);
			pullLabel.setText("One of your local repositories is out of date!");
		} else {
			pullLabel.setForeground(Color.black);
			pullLabel.setText("Your local repositories appear to be up to date.");
		}
	}

	public void pullAllRepositories() {
		pullLabel.setForeground(Color.black);
		pullLabel.setText("Pulling all added repos...");
		pullLabel.paintImmediately(pullLabel.getVisibleRect());
		boolean mergeConflictExists = false; // flag for the first merge conflict
		String mergeConflictList = "<br>";
		try {
			File repoFile = findRepoFile();
			Scanner fileScan = new Scanner(repoFile);
			while (fileScan.hasNext()) {
				// get local repo
				String filepath = fileScan.nextLine();
				GitSubprocessClient repoPuller = new GitSubprocessClient(filepath);
				// if there hasn't been a conflict, pull and keep waiting for the first one
				if (!mergeConflictExists) {
					mergeConflictExists = pullDownChanges(repoPuller);
					if (mergeConflictExists) {
						mergeConflictList += getRepoOwner(filepath) + "/" + getRepoName(filepath);
					}
				}
				// else, pull and check to see if we need to add to the list of conflicts
				else {
					boolean currentMergeResult = pullDownChanges(repoPuller);
					if (currentMergeResult) {
						mergeConflictList += "<br>" + getRepoOwner(filepath) + "/" + getRepoName(filepath);
					}
				}
			}
			fileScan.close();

			if (mergeConflictExists) {
				pullLabel.setForeground(Color.red);
				String mergeFailedMessage = "<html><div style='text-align: center;'><body>The following repositories ran into merge conflicts that you need to resolve and commit: "
						+ mergeConflictList + "</body></html>";
				pullLabel.setText(mergeFailedMessage);
			} else {
				pullLabel.setForeground(Color.black);
				pullLabel.setText("Your local repositories are now up to date.");
			}
		} catch (FileNotFoundException e) {
			pullLabel.setForeground(Color.red);
			pullLabel.setText("You don't have any repos added yet!");
		}
	}

	public boolean pullDownChanges(GitSubprocessClient localRepo) {
		String pull = localRepo.gitPull(localRepo.runGitCommand("branch --show-current"));
		return pull.contains("Automatic merge failed; fix conflicts")
				|| pull.contains("fatal: Exiting because of an unresolved conflict.");
	}

	// used to locate the credential file (token.txt) in the project
	private static File findRepoFile() {
		File currentDirectory = new File(".");
		File repoFile;
		if (currentDirectory.getAbsolutePath().contains("\\src")
				|| currentDirectory.getAbsolutePath().contains("/src")) {
			repoFile = new File("../repo.txt");
		} else {
			repoFile = new File("repo.txt");
		}
		return repoFile;
	}

}
