import javax.swing.JFrame;

public class SetupWindow extends JFrame {
	
	// make the window, MainWindow reference is passed to SetupPanel
	public SetupWindow(MainWindow mainWindow) {
		super("First Time Setup");
		this.setSize(400, 250);
		this.add(new SetupPanel(this, mainWindow));
		this.setVisible(false);
	}
	
	// set the window to be visible
	public void setVisibility(boolean visibility) {
		this.setVisible(visibility);
	}
	
}
