import javax.swing.JFrame;
import github.tools.client.GitHubApiClient;
import github.tools.responseObjects.*;

public class MainWindow extends JFrame {
	
	private MainPanel mainPanel;
	private GitHubApiClient gitHubApiClient;
	
	// make the window
	public MainWindow() {
		super("GitHub Helper");
		this.setSize(400, 600);
		this.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		mainPanel = new MainPanel(this);
		this.add(mainPanel);
		this.setVisible(false);
	}
	
	// get an instance of the GitHubApiClient we made earlier so we can use it later
	public void setGitHubApiClient(GitHubApiClient gitHubApiClient) {
		this.gitHubApiClient = gitHubApiClient;
		mainPanel.setGitHubApiClient(gitHubApiClient);
	}
	
	public GitHubApiClient getGitHubApiClient() {
		return gitHubApiClient;
	}
	
	// update the window and make it visible again
	public void setVisibility(boolean visibility) {
		mainPanel.updateWindow();
		this.setVisible(visibility);
	}
}
