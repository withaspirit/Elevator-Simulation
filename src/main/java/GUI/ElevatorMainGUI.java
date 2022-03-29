package GUI;

import javax.swing.*;

public class ElevatorMainGUI {

	public ElevatorMainGUI(){
		JFrame mainFrame = new JFrame("Elevator GUI");
		ElevatorDoorPanel doorPanel = new ElevatorDoorPanel();
		ElevatorButtonPanel buttonGUI = new ElevatorButtonPanel(22);
		mainFrame.add(doorPanel.getPanel());
		mainFrame.add(buttonGUI.getPanel());
		mainFrame.setSize(buttonGUI.getPanel().getSize());
		mainFrame.setPreferredSize(mainFrame.getSize());
		mainFrame.setVisible(true);
	}

	public static void main(String[] args) {
		new ElevatorMainGUI();
	}
}
