import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import github.tools.client.GitHubApiClient;
import github.tools.responseObjects.*;

public class Driver {

	public static void main(String[] args) {
		MainWindow mainWindow = new MainWindow();
		SetupWindow setupWindow = new SetupWindow(mainWindow);
		GitHubApiClient gitHubApiClient = connectUser(mainWindow, setupWindow);
	}
	
	// used to locate the credential file in the project
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

	private static GitHubApiClient connectUser(MainWindow mainWindow, SetupWindow setupWindow) {
		GitHubApiClient gitHubApiClient = null;
		try {
			File credentialFile = findCredentialFile();
			Scanner fileScan = new Scanner(credentialFile);
			String user = fileScan.nextLine();
			String token = fileScan.nextLine();
			gitHubApiClient = new GitHubApiClient(user, token);
			mainWindow.setVisibility(true);
			fileScan.close();
		}
		catch (FileNotFoundException e) {
			setupWindow.setVisibility(true);
		}
		return gitHubApiClient;
	}
}
