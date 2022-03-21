package floorsystem;

import client_server_host.Client;
import client_server_host.Port;
import client_server_host.RequestMessage;
import misc.InputFileReader;
import requests.*;
import systemwide.Origin;

import java.util.ArrayList;
import java.util.Collections;

/**
 * FloorSubsystem manages the floors and their requests to the Scheduler
 *
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class FloorSubsystem implements Runnable, SystemEventListener {

	private Client client;
	private final ArrayList<SystemEvent> requests;
	private final ArrayList<Floor> floorList;

	/**
	 * Constructor for FloorSubsystem.
	 */
	public FloorSubsystem() {
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
	}

	/**
	 * Gets the size of the requests list.
	 *
	 * @return the size of the requests list
	 */
	public int getRequestSize() {
		return requests.size();
	}

	/**
	 * Add a request to the requests list.
	 *
	 * @param systemEvent a new system event
	 */
	public void addRequest(SystemEvent systemEvent){
		requests.add(systemEvent);
	}

	/**
	 * Sends and receives messages for the system using UDP packets.
	 */
	private void subsystemUDPMethod() {
		while (true) {
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
		}
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
		Thread floorSubsystemThead = new Thread(floorSubsystem, floorSubsystem.getClass().getSimpleName());
		floorSubsystemThead.start();
	}
}
