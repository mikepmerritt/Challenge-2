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

import javax.swing.*;

import github.tools.client.GitHubApiClient;
import github.tools.client.QueryParams;
import git.tools.client.GitSubprocessClient;
import github.tools.client.GitHubApiClient;
import github.tools.client.QueryParams;
import github.tools.responseObjects.*;

public class MainPanel extends JPanel {

	// these components can change during runtime, so they can't be declared in the
	// constructor like the others
	private JLabel selectedUser, pullLabel, titleLabel, pullRequestLabel, addCommitLabel, linkFailLabel;
	private JPanel titlePanel, pullPanel, pullButtons, otherButtonsJPanel, pullRequestPanel, commitPanel, commitFieldsPanel, commitButtonsPanel, otherPanel;
	public JButton refreshButton, resolveButton, repoButton, themeButton, pullRequestRefreshButton, addButton, commitButton;
	public JTextField commitPath, commitMessage;
	private GitHubApiClient gitHubApiClient;
	private MainWindow mainWindow;
	public boolean theme;

	// set up the panel and its components
	public MainPanel(MainWindow mainWindow) {
		super(new GridLayout(5, 1));
		this.setPreferredSize(new Dimension(400, 600));
		this.mainWindow = mainWindow;
		theme = false; // dark mode is off by default

		// title text
		titlePanel = new JPanel(new BorderLayout());
		titleLabel = new JLabel("GitHub Helper");
		titleLabel.setFont(new Font("Arial", Font.PLAIN, 32));
		titleLabel.setHorizontalAlignment(JLabel.CENTER);
		selectedUser = new JLabel("Logged in as " + Driver.getUsername());
		selectedUser.setHorizontalAlignment(JLabel.CENTER);
		titlePanel.add(titleLabel, BorderLayout.CENTER);
		titlePanel.add(selectedUser, BorderLayout.SOUTH);

		this.add(titlePanel);

		// commit alert
		commitPanel = new JPanel(new GridLayout(3,1));
		commitFieldsPanel = new JPanel();
		commitButtonsPanel = new JPanel();
		commitButton = new JButton("Commit changes");
		commitPath = new JTextField("Put repo filepath here",25);
		commitMessage = new JTextField("Put commit message here", 25);
		addCommitLabel = new JLabel("");
		addCommitLabel.setHorizontalAlignment(JLabel.CENTER);
		commitButton.addActionListener(new ActionListener() {
			// on click, check the repositories again
			@Override
			public void actionPerformed(ActionEvent e) {
				addCommitLabel.setText("Checking all added repos...");
				commitChanges(commitPath.getText(), commitMessage.getText());
			}
		});
		addButton = new JButton("Add Changes");
		addButton.addActionListener(new ActionListener() {
			// on click, open the pull window and hide this one
			@Override
			public void actionPerformed(ActionEvent e) {
				addChanges();
			}
		});

		commitPanel.add(addCommitLabel);
		commitFieldsPanel.add(commitPath);
		commitFieldsPanel.add(commitMessage);
		commitPanel.add(commitFieldsPanel);
		commitButtonsPanel.add(addButton);
		commitButtonsPanel.add(commitButton);
		commitPanel.add(commitButtonsPanel);
		
		this.add(commitPanel);

		// pull alert
		pullPanel = new JPanel(new GridLayout(2, 1));
		pullLabel = new JLabel("Check to see if your added repos are up to date.", SwingConstants.CENTER);
		pullLabel.setHorizontalAlignment(JLabel.CENTER);
		pullLabel.setForeground(Color.black);
		// checkLocalRepositoriesForPulls();

		pullButtons = new JPanel();
		refreshButton = new JButton("Refresh");

		refreshButton.addActionListener(new ActionListener() {
			// on click, check the repositories again
			@Override
			public void actionPerformed(ActionEvent e) {
				pullLabel.setText("Checking all added repos...");
				checkLocalRepositoriesForPulls();
			}
		});
		resolveButton = new JButton("Pull down changes");
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
		pullRequestPanel = new JPanel();
		pullRequestLabel = new JLabel("Check for open pull requests");
		pullRequestLabel.setHorizontalAlignment(JLabel.CENTER);
		pullRequestRefreshButton = new JButton("Refresh");
		pullRequestRefreshButton.addActionListener(new ActionListener() {
			// on click, check the pull requests again  again
			@Override
			public void actionPerformed(ActionEvent e) {
				pullRequest();
			}
		});
		
		pullRequestPanel.add(pullRequestLabel);
		pullRequestPanel.add(pullRequestRefreshButton);
		this.add(pullRequestPanel);
		
		// other buttons
		otherPanel = new JPanel(new GridLayout(2, 1));
		linkFailLabel = new JLabel("", SwingConstants.CENTER);
		linkFailLabel.setForeground(Color.red);
		
		otherButtonsJPanel = new JPanel();
		repoButton = new JButton("Link a repo");
		repoButton.addActionListener(new ActionListener() {
			// on click, the button should the setup window, and try connecting the user
			// again
			@Override
			public void actionPerformed(ActionEvent e) {
				// when the button is clicked to open the new window, the theme setting is also
				// passed in
				Driver.updateLinkRepoVisibility(theme);
			}
		});

		themeButton = new JButton("Theme");
		themeButton.addActionListener(new ActionListener() {
			// on click, open the pull window and hide this one
			@Override
			public void actionPerformed(ActionEvent e) {
				updateTheme();
			}
		});

		otherPanel.add(linkFailLabel);
		otherButtonsJPanel.add(repoButton);
		otherButtonsJPanel.add(themeButton);
		otherPanel.add(otherButtonsJPanel);
		this.add(otherPanel);
	}

	// this updates all of the components that can be updated (the ones not declared
	// in the constructor)
	public void updateWindow() {
		selectedUser.setText("Logged in as " + Driver.getUsername());
	}
	
	public void setLinkFailLabel(boolean fail) {
		if(fail) {
			linkFailLabel.setText("The last repo you tried to link was not a valid Git repository.");
		}
		else {
			linkFailLabel.setText("");
		}
	}

	public void addChanges() {
		try {
			File repoFile = findRepoFile();
			Scanner fileScan = new Scanner(repoFile);
			// loop through any repos in the file and check for open pulls
			while (fileScan.hasNext()) {
				String filepath = fileScan.nextLine();
				GitSubprocessClient finder = new GitSubprocessClient(filepath);
				finder.runGitCommand("add .");
			}
			addCommitLabel.setText("All changes have been added.");
		} catch (FileNotFoundException e) {
			addCommitLabel.setText("Cannot add: there are no linked repos to search.");
		}
	}

	public void commitChanges(String repoLink, String commitMessage) {
		try {
			GitSubprocessClient finder = new GitSubprocessClient(repoLink);
			String resultMessage = finder.runGitCommand("commit -m \"" + commitMessage + "\"");
			if (resultMessage.contains("no changes added to commit")) {
				addCommitLabel.setText("No changes have been added to the commit.");
			} else if (resultMessage.contains("nothing to commit")) {
				addCommitLabel.setText("There are no changes to commit.");
			} else addCommitLabel.setText("All changes have been committed.");
		} catch (Exception e) {
			addCommitLabel.setText("Cannot commit: The repo filepath is not valid.");
		}
	}

	// updates the theme from light to dark when the user clicks the button
	public void updateTheme() {
		if (theme) {
			Color defaultButtonColor = new JButton().getBackground();
			
			this.setBackground(Color.white);
			titlePanel.setBackground(Color.white);
			pullPanel.setBackground(Color.white);
			pullButtons.setBackground(Color.white);
			otherButtonsJPanel.setBackground(Color.white);
			otherPanel.setBackground(Color.white);
			pullRequestPanel.setBackground(Color.white);
			selectedUser.setForeground(Color.darkGray);
			titleLabel.setForeground(Color.darkGray);
			pullRequestLabel.setForeground(Color.darkGray);
			addCommitLabel.setForeground(Color.darkGray);
			commitPanel.setBackground(Color.white);
			commitFieldsPanel.setBackground(Color.white);
			commitButtonsPanel.setBackground(Color.white);
			if(!pullLabel.getForeground().equals(Color.red)) {
				pullLabel.setForeground(Color.darkGray);
			}
			theme = false;
			
			refreshButton.setBackground(defaultButtonColor);
			refreshButton.setForeground(Color.darkGray);
			resolveButton.setBackground(defaultButtonColor);
			resolveButton.setForeground(Color.darkGray);
			repoButton.setBackground(defaultButtonColor);
			repoButton.setForeground(Color.darkGray);
			themeButton.setBackground(defaultButtonColor);
			themeButton.setForeground(Color.darkGray);
			pullRequestRefreshButton.setBackground(defaultButtonColor);
			pullRequestRefreshButton.setForeground(Color.darkGray);
			commitButton.setBackground(defaultButtonColor);
			commitButton.setForeground(Color.darkGray);
			addButton.setBackground(defaultButtonColor);
			addButton.setForeground(Color.darkGray);
			commitPath.setBackground(Color.white);
			commitPath.setForeground(Color.black);
			commitMessage.setBackground(Color.white);
			commitMessage.setForeground(Color.black);
			
			
		} else {
			this.setBackground(Color.darkGray);
			titlePanel.setBackground(Color.darkGray);
			pullPanel.setBackground(Color.darkGray);
			pullButtons.setBackground(Color.darkGray);
			otherButtonsJPanel.setBackground(Color.darkGray);
			otherPanel.setBackground(Color.darkGray);
			pullRequestPanel.setBackground(Color.darkGray);
			selectedUser.setForeground(Color.white);
			titleLabel.setForeground(Color.white);
			pullRequestLabel.setForeground(Color.white);
			addCommitLabel.setForeground(Color.white);
			commitPanel.setBackground(Color.darkGray);
			commitFieldsPanel.setBackground(Color.darkGray);
			commitButtonsPanel.setBackground(Color.darkGray);
			if(!pullLabel.getForeground().equals(Color.red)) {
				pullLabel.setForeground(Color.white);
			}
			theme = true;
			
			refreshButton.setBackground(Color.gray);
			refreshButton.setForeground(Color.white);
			resolveButton.setBackground(Color.gray);
			resolveButton.setForeground(Color.white);
			repoButton.setBackground(Color.gray);
			repoButton.setForeground(Color.white);
			themeButton.setBackground(Color.gray);
			themeButton.setForeground(Color.white);
			pullRequestRefreshButton.setBackground(Color.gray);
			pullRequestRefreshButton.setForeground(Color.white);
			commitButton.setBackground(Color.gray);
			commitButton.setForeground(Color.white);
			addButton.setBackground(Color.gray);
			addButton.setForeground(Color.white);
			commitPath.setBackground(Color.lightGray);
			commitPath.setForeground(Color.darkGray);
			commitMessage.setBackground(Color.lightGray);
			commitMessage.setForeground(Color.darkGray);
		}
		mainWindow.getLinkRepoWindow().updateTheme(theme);
	}
	
	// get an instance of the GitHubApiClient we made earlier so we can use it later
		public void setGitHubApiClient(GitHubApiClient gitHubApiClient) {
			this.gitHubApiClient = gitHubApiClient;
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
				Driver.updateLinkRepoVisibility(theme);
			}
		});
	}

	public void checkLocalRepositoriesForPulls() {
		if(theme) {
			pullLabel.setForeground(Color.white);
		}
		else {
			pullLabel.setForeground(Color.black);
		}
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
			if(theme) {
				pullLabel.setForeground(Color.white);
			}
			else {
				pullLabel.setForeground(Color.black);
			}
			pullLabel.setText("Your local repositories appear to be up to date.");
		}
	}

	public void pullAllRepositories() {
		if(theme) {
			pullLabel.setForeground(Color.white);
		}
		else {
			pullLabel.setForeground(Color.black);
		}
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
				if(theme) {
					pullLabel.setForeground(Color.white);
				}
				else {
					pullLabel.setForeground(Color.black);
				}
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
