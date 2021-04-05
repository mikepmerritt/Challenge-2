import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainPanel extends JPanel {
	
	// these components can change during runtime, so they can't be declared in the constructor like the others
	private JLabel selectedUser;
	
	// set up the panel and its components
	public MainPanel() {
		super(new GridLayout(5, 1));
		this.setPreferredSize(new Dimension(400, 600));
		
		// title text
		JPanel titlePanel = new JPanel(new BorderLayout());
		JLabel titleLabel = new JLabel("GitHub Helper");
		titleLabel.setFont(new Font("Arial", Font.PLAIN, 32));
		titleLabel.setHorizontalAlignment(JLabel.CENTER);
		selectedUser = new JLabel("Logged in as " + Driver.getUsername());
		selectedUser.setHorizontalAlignment(JLabel.CENTER);
		titlePanel.add(titleLabel, BorderLayout.CENTER);
		titlePanel.add(selectedUser, BorderLayout.SOUTH);
		
		this.add(titlePanel);
		
		// commit alert
		
		// pull alert
		
		// pull request alert
		
		// other buttons
	}
	
	// this updates all of the components that can be updated (the ones not declared in the constructor)
	public void updateWindow() {
		selectedUser.setText("Logged in as " + Driver.getUsername());
	}
}
