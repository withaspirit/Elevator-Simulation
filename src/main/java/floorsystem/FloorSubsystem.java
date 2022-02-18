package floorsystem;

import misc.*;
import requests.*;
import systemwide.BoundedBuffer;

import java.util.ArrayList;

/**
 * FloorSubsystem manages the floors and their requests to the Scheduler
 * 
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class FloorSubsystem implements Runnable, ServiceRequestListener, SystemEventListener {

	private final BoundedBuffer floorSubsystemBuffer; // Floor Subsystem- Scheduler link
	private final ArrayList<ElevatorRequest> requests;
	private ArrayList<Floor> floorList;

	/**
	 * Constructor for FloorSubsystem.
	 *
	 * @param buffer the buffer the FloorSubsystem passes messages to and receives messages from
	 */
	public FloorSubsystem(BoundedBuffer buffer) {
		this.floorSubsystemBuffer = buffer;
		InputFileReader inputFileReader = new InputFileReader();
		requests = inputFileReader.readInputFile("inputs");
		floorList = new ArrayList<>();
	}

	/**
	 * Adds a floor to the subsystem's list of floors.
	 *
	 * @param floor a floor
	 */
	public void addFloor(Floor floor) {
		floorList.add(floor);
	}

	/**
	 * Simple message requesting and sending between subsystems.
	 * FloorSubsystem
	 * Sends: ApproachEvent, ElevatorRequest
	 * Receives: ApproachEvent
	 */
	public void run() {
		int receive = requests.size();
		while (receive != 0) {
			if (!requests.isEmpty()) {
				// Sending Data to Scheduler
				sendMessage(requests.get(0), floorSubsystemBuffer, Thread.currentThread());
				System.out.println(Thread.currentThread().getName() + " Sent Request Successful to Scheduler");
				requests.remove(0);
			}
			ServiceRequest request = receiveMessage(floorSubsystemBuffer, Thread.currentThread());
			if (request instanceof FloorRequest floorRequest){
				receive--;
				System.out.println("Expected Elevator# " + (floorRequest).getElevatorNumber() + " Arrived \n");
			} else if (request instanceof ApproachEvent approachEvent) {
				// pass to floorList.get((approachEvent.getFloorNumber());
			}
		}
	}

	/**
	 * Passes an ApproachEvent between a Subsystem component and the Subsystem.
	 *
	 * @param approachEvent the approach event for the system
	 */
	@Override
	public void handleApproachEvent(ApproachEvent approachEvent) {
		sendMessage(approachEvent, floorSubsystemBuffer, Thread.currentThread());
	}
}
