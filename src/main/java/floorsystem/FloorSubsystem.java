package floorsystem;

import client_server_host.Client;
import client_server_host.Port;
import client_server_host.RequestMessage;
import misc.*;
import requests.*;
import systemwide.Origin;

import java.util.ArrayList;
import java.util.Collections;

/**
 * FloorSubsystem manages the floors and their requests to the Scheduler
 * 
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class FloorSubsystem implements Runnable, SubsystemMessagePasser, SystemEventListener {

	private final Client client;
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
		boolean notEmpty = true;
		while (true) {
			while (true) {
				if (client != null) {

					// if FloorSubsystem has something to send, send it
					// otherwise, send a data request
					// Note that the only data FloorSubsystem reacts to atm are ApproachEvents
					if (!requests.isEmpty()) {
						SystemEvent requestToSend = requests.remove(requests.size() - 1);
						client.sendAndReceiveReply(requestToSend);
					} else {
						String dataRequest = RequestMessage.REQUEST.getMessage();
						Object receivedData = client.sendAndReceiveReply(dataRequest);

						if (receivedData instanceof ApproachEvent approachEvent) {
							processApproachEvent(approachEvent);
						}
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
