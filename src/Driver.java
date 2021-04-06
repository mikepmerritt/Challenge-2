import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import github.tools.client.GitHubApiClient;
import github.tools.responseObjects.*;

public class Driver {

	// username and API token from credential file
	private static String user;
	private static String token;
	
	// main method, loads all windows (although they aren't all visible by default)
	public static void main(String[] args) {
		MainWindow mainWindow = new MainWindow();
		SetupWindow setupWindow = new SetupWindow(mainWindow);
		GitHubApiClient gitHubApiClient = connectUser(mainWindow, setupWindow);
	}
	
	// used to locate the credential file (token.txt) in the project
	private static File findCredentialFile() {
		File currentDirectory = new File(".");
		File credentialFile;
		if (currentDirectory.getAbsolutePath().contains("\\src") || currentDirectory.getAbsolutePath().contains("/src")) {
			credentialFile = new File("../token.txt");
		}
		else {
			credentialFile = new File("token.txt");
		}
		return credentialFile;
	}

	// looks for the credentials in the credential file
	// if the file is found, a GitHubApiClient is created and returned
	// if not, the app switches to the setup screen to get login information from the user
	public static GitHubApiClient connectUser(MainWindow mainWindow, SetupWindow setupWindow) {
		GitHubApiClient gitHubApiClient = null;
		try {
			File credentialFile = findCredentialFile();
			Scanner fileScan = new Scanner(credentialFile);
			user = fileScan.nextLine();
			token = fileScan.nextLine();
			gitHubApiClient = new GitHubApiClient(user, token);
			mainWindow.setGitHubApiClient(gitHubApiClient);
			mainWindow.setVisibility(true);
			fileScan.close();
		}
		catch (FileNotFoundException e) {
			setupWindow.setVisibility(true);
		}
		return gitHubApiClient;
	}
	
	// returns the username used to make the GitHubApiClient
	public static String getUsername() {
		return user;
	}
}
