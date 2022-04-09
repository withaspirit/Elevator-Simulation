package floorsystem;

import client_server_host.Client;
import client_server_host.Port;
import client_server_host.RequestMessage;
import misc.InputFileReader;
import requests.*;
import systemwide.Structure;
import systemwide.SystemStatus;

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
	private volatile SystemStatus systemStatus;

	/**
	 * Constructor for FloorSubsystem.
	 */
	public FloorSubsystem() {
		client = new Client(Port.CLIENT.getNumber());
		InputFileReader inputFileReader = new InputFileReader();
		eventList = inputFileReader.readInputFile(InputFileReader.INPUTS_FILENAME);
		floorList = new ArrayList<>();
		systemStatus = new SystemStatus(false);
	}

	/**
	 * Simple message requesting and sending between subsystems.
	 * FloorSubsystem
	 * Sends: ApproachEvent, ElevatorRequest
	 * Receives: ApproachEvent
	 */
	public void run() {
		Collections.reverse(eventList);
		systemStatus.setSystemActivated(true);
		while (systemStatus.activated()) {
			subsystemUDPMethod();
		}
		System.out.println(getClass().getSimpleName() + " Thread terminated");
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
	 * Adds a SystemEvent to the FloorSubsystem.
	 *
	 * @param systemEvent a SystemEvent originating from the FloorSubsystem
	 */
	public void addEvent(SystemEvent systemEvent) {
		eventList.add(systemEvent);
	}

	/**
	 * Gets the SystemStatus of the System.
	 *
	 * @return the SystemStatus of the System
	 */
	public SystemStatus getSystemStatus() {
		return systemStatus;
	}

	/**
	 * Sends and receives messages for the system using UDP packets.
	 */
	private void subsystemUDPMethod() {
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
					} else if (string.trim().equals(RequestMessage.TERMINATE.getMessage())) {
						systemStatus.setSystemActivated(false);
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

	/**
	 * Receives and returns a Structure from the Scheduler.
	 *
	 * @return Structure contains information to initialize the floors and elevators
	 */
	@Override
	public Structure receiveStructure() {
		Structure structure = (Structure) client.receive();
		return structure;
	}

	public static void main(String[] args) {
		FloorSubsystem floorSubsystem = new FloorSubsystem();
		Structure structure = floorSubsystem.receiveStructure();

		floorSubsystem.initializeFloors(structure.getNumberOfFloors());
		System.out.println("Floors initialized");
		Thread floorSubsystemThread = new Thread(floorSubsystem, floorSubsystem.getClass().getSimpleName());
		floorSubsystemThread.start();
	}
}
