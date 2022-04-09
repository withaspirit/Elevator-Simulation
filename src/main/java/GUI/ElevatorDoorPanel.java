package GUI;

import javax.swing.*;
import java.awt.*;

public class ElevatorDoorPanel {

	public JPanel elevatorPanel;
	private final Rectangle doorLeft, doorRight;

	public ElevatorDoorPanel(int height){
		elevatorPanel = new JPanel();
		doorLeft = new Rectangle(height/4,height);
		doorRight = new Rectangle(height/4,height);
		elevatorPanel.paintImmediately(doorLeft);
		elevatorPanel.paintImmediately(doorRight);
		elevatorPanel.setVisible(true);
	}

	public Component getPanel() {
		return elevatorPanel;
	}
}
