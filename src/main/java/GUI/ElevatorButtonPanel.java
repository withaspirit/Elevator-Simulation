package GUI;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * An elevator button panel
 *
 * @author Ryan Dash
 */
public class ElevatorButtonPanel {
	private final JPanel elevatorButtons;
	// remove with removal of main method
	private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	/**
	 * Main Constructor for elevator button panel
	 *
	 * @param numberOfFloors the number of floors in the system
	 */
	public ElevatorButtonPanel(int numberOfFloors, int height){
		int numberOfRows = numberOfFloors/4 + 1;
		elevatorButtons = new JPanel();
		elevatorButtons.setLayout(new GridLayout(numberOfRows, 4));
		int floor;
		for (int i = 0; i < numberOfRows; i++){
			for (int j = 4; j > 0; j--){
				floor = numberOfFloors - (i * 4 + j);
				if (floor > 0){
					elevatorButtons.add(new RoundFloorButton(floor));
				} else{
					JButton blank = new JButton();
					blank.setEnabled(false);
					elevatorButtons.add(blank);
					//elevatorButtons.add(new JButton("Emergency"));
				}
			}
		}
	}

	/**
	 * Gets the elevatorButtons panel
	 *
	 * @return the elevatorButtons panel
	 */
	public JPanel getPanel(){
		return elevatorButtons;
	}

	/**
	 *
	 *
	 * @param number
	 * @param color
	 */
	public void changeButtonColor(int number, Color color){
		for (Component buttons: elevatorButtons.getComponents()){
			if (buttons instanceof RoundFloorButton button){
				if (button.getText().equals(number + "")){
					button.setForeground(color);
				}
			}
		}
	}

	/**
	 * Used for testing the elevator button panel for creating all buttons on the panel
	 *
	 * @param args not used
	 */
	public static void main(String[] args) {
		JPanel panel = new ElevatorButtonPanel(22, screenSize.height/4).getPanel();
		JFrame frame = new JFrame();
		frame.add(panel);
		frame.setSize(panel.getSize());
		frame.setVisible(true);
	}
}
