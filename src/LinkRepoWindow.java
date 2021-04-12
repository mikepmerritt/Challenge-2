import javax.swing.JFrame;

public class LinkRepoWindow extends JFrame{
	
	public LinkRepoPanel linkRepoPanel;

	public LinkRepoWindow(MainWindow mainWindow) {
		super("Link a Repo");
		this.setSize(400, 250);
		this.setDefaultCloseOperation(javax.swing.JFrame.HIDE_ON_CLOSE);
		linkRepoPanel = new LinkRepoPanel(this, mainWindow);
		this.add(linkRepoPanel);
	}
	public void setVisibility(boolean visibility) {
		this.setVisible(visibility);
	}
	
	// updates the theme in the panel
	public void updateTheme(boolean theme) {
		linkRepoPanel.updateTheme(theme);
	}
}
