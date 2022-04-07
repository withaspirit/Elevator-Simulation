package floorsystem;

import client_server_host.Client;
import client_server_host.Port;
import client_server_host.RequestMessage;
import misc.InputFileReader;
import requests.*;

import java.util.ArrayList;
import java.util.Collections;

/**
 * FloorSubsystem manages the floors and their requests to the Scheduler.
 *
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class FloorSubsystem implements Runnable, SystemEventListener {

	private Client client;
	private final ArrayList<SystemEvent> eventList;
	private final ArrayList<Floor> floorList;

	/**
	 * Constructor for FloorSubsystem.
	 */
	public FloorSubsystem() {
		client = new Client(Port.CLIENT.getNumber());
		InputFileReader inputFileReader = new InputFileReader();
		eventList = inputFileReader.readInputFile(InputFileReader.INPUTS_FILENAME);
		floorList = new ArrayList<>();
	}

	/**
	 * Simple message requesting and sending between subsystems.
	 * FloorSubsystem
	 * Sends: ApproachEvent, ElevatorRequest
	 * Receives: ApproachEvent
	 */
	public void run() {
		Collections.reverse(eventList);

		while (true) {
			subsystemUDPMethod();
		}
	}

	/**
	 * Processes an ApproachEvent, checking its corresponding floor to see whether
	 * an Elevator should stop.
	 *
	 * @param approachEvent the ApproachEvent used to determine whether the Elevator should stop
	 */
	public void processApproachEvent(ApproachEvent approachEvent) {
		// Send ApproachEvent to the correct floor for comparison
		floorList.get(approachEvent.getFloorNumber() - 1).receiveApproachEvent(approachEvent);

		if (getSpecificFloor(approachEvent.getFloorNumber()).isElevatorExpected(approachEvent.getElevatorNumber(), approachEvent.getDirection())){
			approachEvent.allowElevatorStop();
		}

		// Add the ApproachEvent to the event list
		eventList.add(approachEvent);
	}

	/**
	 * Adds a floor to the FloorSubsystem's list of floors.
	 *
	 * @param floor a floor in the FloorSubsystem
	 */
	public void addFloor(Floor floor) {
		floorList.add(floor);
	}

	/**
	 * Passes an ApproachEvent between a Subsystem component and the Subsystem.
	 *
	 * @param approachEvent the ApproachEvent for the system
	 */
	@Override
	public void handleApproachEvent(ApproachEvent approachEvent) {
		eventList.add(approachEvent);
	}

	/**
	 * Gets the size of the event list.
	 *
	 * @return the number of events in the event list
	 */
	public int getEventListSize() {
		return eventList.size();
	}

	/**
	 * Gets the next ApproachEvent in the event list
	 *
	 * @return ApproachEvent object from the event list, or null if there is no ApproachEvent in the event list
	 */
	public ApproachEvent getNextEventListApproachEvent() {
		for (SystemEvent systemEvent : eventList) {
			if (systemEvent instanceof ApproachEvent) {
				return (ApproachEvent) systemEvent;
			}
		}
		return null;
	}

	/**
	 * Adds a SystemEvent to the FloorSubsystem.
	 *
	 * @param systemEvent a SystemEvent originating from the FloorSubsystem
	 */
	public void addEvent(SystemEvent systemEvent) {
		eventList.add(systemEvent);
	}

	/**
	 * Adds a ServiceRequest to a list of requests held in the appropriate floors' ArrivalSensor
	 *
	 * @param serviceRequest the ServiceRequest to be added
	 */
	public void addServiceRequestToFloor(ServiceRequest serviceRequest) {
		// Get the specific floor from
		floorList.get(serviceRequest.getFloorNumber() - 1).addRequestToSensor(serviceRequest);
	}

	/**
	 * Gets a specific Floor from floorList
	 *
	 * @param floorNumber the number of the requested floor
	 * @return the Floor object in floorList at the floorNumber index
	 */
	public Floor getSpecificFloor(int floorNumber) { return floorList.get(floorNumber - 1); }

	/**
	 * Sends and receives messages for the system using UDP packets.
	 */
	private void subsystemUDPMethod() {
		while (true) {
			if (!eventList.isEmpty()) {
				client.sendAndReceiveReply(eventList.remove(eventList.size() - 1));
			} else {
				Object object = client.sendAndReceiveReply(RequestMessage.REQUEST.getMessage());

				if (object instanceof ApproachEvent approachEvent) {
					processApproachEvent(approachEvent);
				} else if (object instanceof ElevatorRequest elevatorRequest) {
					eventList.add(elevatorRequest);
				} else if (object instanceof String string) {
					if (string.trim().equals(RequestMessage.EMPTYQUEUE.getMessage())) {
						try {
							Thread.sleep(5);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	/**
	 * Initializes the specified number of Floors for the FloorSubsystem.
	 *
	 * @param numberOfFloors the number of the Floors to be initialized
	 */
	public void initializeFloors(int numberOfFloors) {
		for (int i = 1; i <= numberOfFloors; i++) {
			Floor floor = new Floor(i, this);
			this.addFloor(floor);
		}
	}

	/**
	 * Returns the list of Floors in the FloorSubystem.
	 *
	 * @return the list of Floors
	 */
	public ArrayList<Floor> getFloorList() {
		return floorList;
	}

	public static void main(String[] args) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		int numberOfFloors = 10;
		FloorSubsystem floorSubsystem = new FloorSubsystem();
		floorSubsystem.initializeFloors(numberOfFloors);
		Thread floorSubsystemThead = new Thread(floorSubsystem, floorSubsystem.getClass().getSimpleName());
		floorSubsystemThead.start();
	}
}
