import javax.swing.JFrame;
import github.tools.client.GitHubApiClient;
import github.tools.responseObjects.*;

public class MainWindow extends JFrame {
	
	private MainPanel mainPanel;
	private GitHubApiClient gitHubApiClient;
	
	public MainWindow() {
		super("GitHub Helper");
		this.setSize(400, 600);
		mainPanel = new MainPanel();
		this.add(mainPanel);
		this.setVisible(false);
	}
	
	public void setGitHubApiClient(GitHubApiClient gitHubApiClient) {
		this.gitHubApiClient = gitHubApiClient;
	}
	
	public void setVisibility(boolean visibility) {
		mainPanel.updateWindow();
		this.setVisible(visibility);
	}
}
