package floorsystem;

import client_server_host.Client;
import client_server_host.Port;
import client_server_host.RequestMessage;
import misc.InputFileReader;
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
	private Client client;
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
		requests = inputFileReader.readInputFile(InputFileReader.INPUTS_FILENAME);
		floorList = new ArrayList<>();
		origin = Origin.FLOOR_SYSTEM;
	}

	/**
	 * Constructor for FloorSubsystem.
	 */
	public FloorSubsystem() {
		floorSubsystemBuffer = null;
		client = new Client(Port.CLIENT.getNumber());
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
			if (client != null) {
				if (!requests.isEmpty()) {
					client.sendAndReceiveReply(requests.remove(requests.size() - 1));
				} else {
					Object object = client.sendAndReceiveReply(RequestMessage.REQUEST.getMessage());

					if (object instanceof ApproachEvent approachEvent) {
						processApproachEvent(approachEvent);
					} else if (object instanceof ElevatorRequest elevatorRequest) {
						requests.add(elevatorRequest);
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
			} else {
				//  add to floorBuffer if possible
				if (!requests.isEmpty()) {
					// Sending Data to Scheduler
					SystemEvent event = requests.remove(requests.size() - 1);

					sendMessage(event, floorSubsystemBuffer, origin);
				}

				// check if can remove from buffer before trying to remove
				if (floorSubsystemBuffer.canRemoveFromBuffer(origin)) {
					SystemEvent request = receiveMessage(floorSubsystemBuffer, origin);
					if (request instanceof FloorRequest floorRequest) {

					} else if (request instanceof ApproachEvent approachEvent) {
						processApproachEvent(approachEvent);
					}
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
		requests.add(approachEvent);
		sendMessage(approachEvent, floorSubsystemBuffer, origin);
	}

	public static void main(String[] args) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		int numberOfFloors = 10;
		FloorSubsystem floorSubsystem = new FloorSubsystem();
		for (int i = 1; i <= numberOfFloors; i++) {
			Floor floor = new Floor(i, floorSubsystem);
			floorSubsystem.addFloor(floor);
		}
		Thread floorSubsystemThead = new Thread (floorSubsystem, floorSubsystem.getClass().getSimpleName());
		floorSubsystemThead.start();
	}
}
