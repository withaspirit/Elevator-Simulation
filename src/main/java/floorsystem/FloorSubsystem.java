package floorsystem;

import misc.*;
import requests.*;
import systemwide.BoundedBuffer;
import systemwide.Direction;
import systemwide.Origin;

import java.time.LocalTime;
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
	private Origin origin;

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
		origin = Origin.FLOOR_SYSTEM;
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
			//  add to floorBuffer if possible
			if (!requests.isEmpty()) {
				// Sending Data to Scheduler
				SystemEvent event = requests.remove(requests.size() -1);

				sendMessage(event, floorSubsystemBuffer, origin);
				System.out.println(origin + " Sent Request Successful to Scheduler");
			} else {
				//Send dummy useless message
				sendMessage(new FloorRequest(LocalTime.now(), -1, Direction.NONE, -1, origin), floorSubsystemBuffer, origin);
			}
      
			// check if can remove from buffer before trying to remove
			if (floorSubsystemBuffer.canRemoveFromBuffer(origin)) {
				SystemEvent request = receiveMessage(floorSubsystemBuffer, origin);
				if (request instanceof FloorRequest floorRequest) {
					System.out.println("FloorSubsystem: Received FloorRequest: in  Elevator# " +
							floorRequest.getElevatorNumber() + " Arrived \n");
				} else if (request instanceof ApproachEvent approachEvent) {
							processApproachEvent(approachEvent);
				}
			}
		}
	}

	/**
	 * Processes an ApproachEvent, checking its corresponding floor to see whether
	 * an Elevator should stop.
	 *
	 * @param approachEvent the ApproachEvent used to determine whether the Elevator should stop
	 */
	public void processApproachEvent(ApproachEvent approachEvent) {
		Floor floor = floorList.get(approachEvent.getFloorNumber() - 1);
		floor.receiveApproachEvent(approachEvent);
		requests.add(approachEvent);
	}

	/**
	 * Adds a floor to the subsystem's list of floors.
	 *
	 * @param floor a floor in the FloorSubsystem
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
		sendMessage(approachEvent, floorSubsystemBuffer, origin);
	}
}
