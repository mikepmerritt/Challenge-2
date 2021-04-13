import javax.swing.JFrame;
import github.tools.client.GitHubApiClient;
import github.tools.responseObjects.*;

public class MainWindow extends JFrame {
	
	private MainPanel mainPanel;
	private GitHubApiClient gitHubApiClient;
	public boolean theme;
	private LinkRepoWindow linkRepoWindow;

	// make the window
	public MainWindow() {
		super("GitHub Helper");
		this.setSize(800, 600);
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
	
	// set an instance of the LinkRepoWindow we made earlier so we can recolor it
	public void setLinkRepoWindow(LinkRepoWindow linkRepoWindow) {
		this.linkRepoWindow = linkRepoWindow;
	}
	
	// get an instance of the LinkRepoWindow we made earlier so we can recolor it
	public LinkRepoWindow getLinkRepoWindow() {
		return linkRepoWindow;
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
