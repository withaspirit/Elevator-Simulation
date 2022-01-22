package floorsystem;

/**
 * 
 * 
 * @author Liam Tripp
 */
public class Floor {
	
	private int floorNumber;
	private FloorSubsystem subSystem; // TODO: what creates the subsystem is yet to be determined
	
	public Floor(int floorNumber) {
		this.floorNumber = floorNumber;
		// subsystem = new FloorSubsystem();
		// createButtons(floorNumber);
	}

	/* public void createButtons(int floorNumber, Scheduler scheduler)
	 *
	 * if (floorNumber == bottom || floorNumber == top) {
	 *     buttons = new ___[1];
	 * } else {
	 * 		buttons = new ___[2];
	 * }
	 * buttons.forEach(button -> button.addActionListener(...));
	 *
	 */
}
