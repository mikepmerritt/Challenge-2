import javax.swing.JFrame;

public class LinkRepoWindow extends JFrame{

	public LinkRepoWindow(MainWindow mainWindow) {
		super("Link a Repo");
		this.setSize(400, 250);
		this.setDefaultCloseOperation(javax.swing.JFrame.HIDE_ON_CLOSE);
		this.add(new LinkRepoPanel(this, mainWindow));
	}
	public void setVisibility(boolean visibility) {
		this.setVisible(visibility);
	}
}
