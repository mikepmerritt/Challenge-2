import javax.swing.JFrame;

public class MainWindow extends JFrame {
	
	public MainWindow() {
		super("GitHub Helper");
		this.setSize(400, 600);
		this.setVisible(false);
	}
	
	public void setVisibility(boolean visibility) {
		this.setVisible(visibility);
	}
}
