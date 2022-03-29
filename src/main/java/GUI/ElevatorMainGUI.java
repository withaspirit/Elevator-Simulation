package GUI;

import javax.swing.*;
import java.awt.*;

public class ElevatorMainGUI {

	private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	public ElevatorMainGUI(){
		JFrame mainFrame = new JFrame("Elevator GUI");
		mainFrame.setLayout(new BorderLayout());
		ElevatorDoorPanel doorPanel = new ElevatorDoorPanel(screenSize.height/2);
		ElevatorButtonPanel buttonGUI = new ElevatorButtonPanel(22, screenSize.height/2);
		mainFrame.add(doorPanel.getPanel(), BorderLayout.NORTH);
		mainFrame.add(buttonGUI.getPanel(), BorderLayout.SOUTH);
		mainFrame.setSize(buttonGUI.getPanel().getWidth(), screenSize.height);
		mainFrame.setVisible(true);
	}

	public static void main(String[] args) {
		new ElevatorMainGUI();
	}
}
