package systemwide;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.*;
import javax.swing.*;

/**
 * Creates a round floor JButton by overwriting the default JButton
 *
 * @author JavaCodex, Ryan Dash
 */
public class RoundFloorButton extends JButton implements ActionListener{

	// Hit detection
	private Shape shape;

	/**
	 * Main Constructor for RoundFloorButton
	 *
	 * @param floorNumber an integer corresponding to a floor number
	 */
	public RoundFloorButton(int floorNumber) {
		super("" + floorNumber);

		setBackground(Color.lightGray);
		setFocusable(false);
		Dimension size = getPreferredSize();
		size.width = size.height = Math.max(size.width, size.height);
		setPreferredSize(size);
		setContentAreaFilled(false);
		setActionCommand(floorNumber + "");
	}

	/**
	 * Overwrite the paintComponent to make the button an oval
	 *
	 * @param g the graphics for the button
	 */
	protected void paintComponent(Graphics g) {
		if (getModel().isArmed()) {
			g.setColor(Color.gray);
		} else {
			g.setColor(getBackground());
		}
		g.fillOval(0, 0, getSize().width - 1, getSize().height - 1);
		g.setColor(Color.orange);
		g.fillOval(5, 5, getSize().width - 11, getSize().height - 11);
		super.paintComponent(g);
	}

	/**
	 * Overwrite the paintBorder to make the button border black
	 *
	 * @param g the graphics for the button
	 */
	protected void paintBorder(Graphics g) {
		g.setColor(Color.BLACK);
		g.drawOval(0, 0, getSize().width - 1, getSize().height - 1);
	}

	/**
	 * Overwrite the hit box of the JButton to the oval shape
	 *
	 * @param x the x position for the start of the circular button
	 * @param y the y position for the start of the circular button
	 *
	 * @return true if the button was clicked within the hit box for the new button, false otherwise
	 */
	public boolean contains(int x, int y) {
		// If the button has changed size,  make a new shape object.
		if (shape == null || !shape.getBounds().equals(getBounds())) {
			shape = new Ellipse2D.Float(0, 0, getWidth(), getHeight());
		}
		return shape.contains(x, y);
	}

	/**
	 * Make the button perform an action
	 *
	 * @param e an action event for a button press
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		int floor = Integer.parseInt(e.getActionCommand());

	}

	/**
	 * Test creating a basic Round Button on a GUI
	 *
	 * @param args not used
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame("Round Button");
		frame.setLayout(new FlowLayout());
		frame.add(new RoundFloorButton(1));
		frame.setSize(300, 150);
		frame.setVisible(true);
	}
}