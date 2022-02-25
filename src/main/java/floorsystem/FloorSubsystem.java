package floorsystem;

import misc.*;
import requests.*;
import systemwide.BoundedBuffer;
import systemwide.Origin;

import java.util.ArrayList;
import java.util.Collections;

/**
 * FloorSubsystem manages the floors and their requests to the Scheduler
 * 
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class FloorSubsystem implements Runnable, SubsystemMessagePasser, SystemEventListener {

	private final BoundedBuffer floorSubsystemBuffer; // Floor Subsystem- Scheduler link
	private final ArrayList<SystemEvent> requests;
	private final ArrayList<Floor> floorList;

	/**
	 * Constructor for FloorSubsystem.
	 *
	 * @param buffer the buffer the FloorSubsystem passes messages to and receives messages from
	 */
	public FloorSubsystem(BoundedBuffer buffer) {
		this.floorSubsystemBuffer = buffer;
		InputFileReader inputFileReader = new InputFileReader();
		requests = inputFileReader.readInputFile(InputFileReader.INPUTS_FILENAME);
		floorList = new ArrayList<>();
	}

	/**
	 * Simple message requesting and sending between subsystems.
	 * FloorSubsystem
	 * Sends: ApproachEvent, ElevatorRequest
	 * Receives: ApproachEvent
	 */
	public void run() {
		Collections.reverse(requests);

		while (true) {
			if (!requests.isEmpty()) {
				// Sending Data to Scheduler
				SystemEvent event = requests.remove(requests.size() -1);

				sendMessage(event, floorSubsystemBuffer, Origin.SCHEDULER);
				System.out.println(Origin.FLOOR_SYSTEM + " Sent Request Successful to Scheduler");
			}
			SystemEvent request = receiveMessage(floorSubsystemBuffer, Origin.FLOOR_SYSTEM);
			if (request instanceof FloorRequest floorRequest) {
				System.out.println("FloorSubsystem: Received FloorRequest: in  Elevator# " +
						floorRequest.getElevatorNumber() + " Arrived \n");
			} else if (request instanceof ApproachEvent approachEvent) {
				Floor floor = floorList.get(approachEvent.getFloorNumber());
				floor.receiveApproachEvent(approachEvent);
				requests.add(approachEvent);
			}
		}
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
	 * Passes an ApproachEvent between a Subsystem component and the Subsystem.
	 *
	 * @param approachEvent the approach event for the system
	 */
	@Override
	public void handleApproachEvent(ApproachEvent approachEvent) {
		sendMessage(approachEvent, floorSubsystemBuffer, Origin.SCHEDULER);
	}
}
