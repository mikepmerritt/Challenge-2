import javax.swing.JFrame;

public class SetupWindow extends JFrame {
	
	public SetupWindow() {
		super("First Time Setup");
		this.setSize(400, 150);
		this.add(new SetupPanel());
		this.setVisible(true);
	}
	
}
