package floorsystem;
import requests.*;

/**
 * Arrival Sensor checks the floorNumber in ApproachEvent is equal to the Floor's floorNumber
 * if Both the values are equal allowElevatorStop method from ApproachEvent is called 
 * 
 * @author Ramit Mahajan
 */


public class ArrivalSensor {
	
	private int floorNumber;
	public ArrivalSensor(int floorNumber) {
		this.floorNumber = floorNumber;
		// createButtons(floorNumber);
	}
	public void checkFloorNumber(ApproachEvent approachEvent) {
		if(approachEvent.getFloorNumber()== floorNumber) {
			approachEvent.allowElevatorStop();		
		}
	}
	

}
