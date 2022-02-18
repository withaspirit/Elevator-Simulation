package floorsystem;

import requests.ApproachEvent;

/**
 * Floor simulates a level of a structure that an elevator can visit.
 * 
 * @author Liam Tripp
 */
public class Floor {
	
	private int floorNumber;
	private FloorSubsystem floorSubsystem;

	public Floor(int floorNumber, FloorSubsystem floorSubsystem) {
		this.floorNumber = floorNumber;
		this.floorSubsystem = floorSubsystem;
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

	/**
	 * Passes an ApproachEvent to the ElevatorSubsystem.
	 *
	 * @param approachEvent the ApproachEvent that is examined by the ArrivalSensor
	 */
	public void passApproachEvent(ApproachEvent approachEvent) {
		floorSubsystem.handleApproachEvent(approachEvent);
	}
}
