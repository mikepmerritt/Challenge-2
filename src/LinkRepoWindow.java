import javax.swing.JFrame;

public class LinkRepoWindow extends JFrame{

	public LinkRepoWindow(MainWindow mainWindow) {
		super("First Time Setup");
		this.setSize(400, 250);
		this.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		this.add(new LinkRepoPanel(this, mainWindow));
	}
	public void setVisibility(boolean visibility) {
		this.setVisible(visibility);
	}
}
