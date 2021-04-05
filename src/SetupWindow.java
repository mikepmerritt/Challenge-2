import javax.swing.JFrame;

public class SetupWindow extends JFrame {
	
	public SetupWindow() {
		super("First Time Setup");
		this.setSize(400, 250);
		this.add(new SetupPanel());
		this.setVisible(true);
	}
	
}
