package elevatorsystem;

import client_server_host.Client;
import client_server_host.Port;
import client_server_host.RequestMessage;
import requests.*;
import systemwide.Structure;
import systemwide.SystemStatus;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * ElevatorSubsystem manages the elevators and their requests to the Scheduler.
 *
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class ElevatorSubsystem implements Runnable, SystemEventListener {

	private final ArrayList<Elevator> elevatorList;
	private Client server;
	private final Queue<SystemEvent> eventQueue;
	private volatile SystemStatus systemStatus;

	/**
	 * Constructor for ElevatorSubsystem.
	 */
	public ElevatorSubsystem() {
		server = new Client(Port.SERVER.getNumber());
		elevatorList = new ArrayList<>();
		eventQueue = new LinkedList<>();
		systemStatus = new SystemStatus(false);
	}

	/**
	 * Returns the list of Elevators in the ElevatorSubsystem.
	 *
	 * @return the list of Elevators
	 */
	public ArrayList<Elevator> getElevatorList() {
		return elevatorList;
	}

	/**
	 * Simple message requesting and sending between subsystems.
	 * ElevatorSubsystem
	 * Sends: ApproachEvent, ElevatorMonitor
	 * Receives: ApproachEvent, ElevatorRequest
	 */
	public void run() {
		// TODO: replace with systemActivated
		while (true) {
			subsystemUDPMethod();
		}
	}

	/**
	 * Adds an elevator to the subsystem's list of elevators.
	 *
	 * @param elevator an elevator
	 */
	public void addElevator(Elevator elevator) {
		elevatorList.add(elevator);
	}

	/**
	 * Passes an ApproachEvent between a Subsystem component and the Subsystem.
	 *
	 * @param approachEvent the approach event for the system
	 */
	@Override
	public void handleApproachEvent(ApproachEvent approachEvent) {
		eventQueue.add(approachEvent);
	}

	/**
	 * Sends new updated elevator status information to the scheduler.
	 *
	 * @param elevatorMonitor an elevator monitor containing updated elevator information.
	 */
	public void handleElevatorMonitorUpdate(ElevatorMonitor elevatorMonitor) {
		eventQueue.add(elevatorMonitor);
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
	 * Sends and receives messages for system using UDP packets.
	 */
	private void subsystemUDPMethod() {
			Object object;
			if (!eventQueue.isEmpty()) {
				object = server.sendAndReceiveReply(eventQueue.remove());
			} else {
				object = server.sendAndReceiveReply(RequestMessage.REQUEST.getMessage());
			}

			if (object instanceof ElevatorRequest elevatorRequest) {
				Elevator elevator = elevatorList.get(elevatorRequest.getElevatorNumber() - 1);
				elevator.addRequest(elevatorRequest);
				eventQueue.add(elevator.makeElevatorMonitor());
			} else if (object instanceof ApproachEvent approachEvent) {
				elevatorList.get(approachEvent.getElevatorNumber() - 1).receiveApproachEvent(approachEvent);
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

	/**
	 * Initializes the ElevatorSubsystem with the specified number of Elevators.
	 *
	 * @param structure contains the information for initializing the elevators
	 */
	public void initializeElevators(Structure structure) {
		// initialize the list of elevators
		for (int i = 1; i <= structure.getNumberOfElevators(); i++) {
			Elevator elevator = new Elevator(i, this);
			addElevator(elevator);
			elevator.setTravelTime(structure.getElevatorTime());
			elevator.setDoorTime(structure.getDoorsTime());
		}
	}

	/**
	 * Initializes the Elevator threads for the ElevatorSubsystem.
	 */
	public void initializeElevatorThreads() {
		// Start elevator Threads
		for (Elevator elevator : elevatorList) {
			(new Thread(elevator, elevator.getClass().getSimpleName())).start();
		}
	}

	/**
	 * Receives and returns a Structure from the Scheduler.
	 *
	 * @return Structure contains information to initialize the floors and elevators
	 */
	@Override
	public Structure receiveStructure() {
		Structure structure = (Structure) server.receive();
		return structure;
	}

	public static void main(String[] args) {
		ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem();
		Structure structure = elevatorSubsystem.receiveStructure();
		elevatorSubsystem.initializeElevators(structure);

		Thread elevatorSubsystemThread = new Thread(elevatorSubsystem, elevatorSubsystem.getClass().getSimpleName());
		elevatorSubsystemThread.start();
		System.out.println("ElevatorSubsystem initialized");
		elevatorSubsystem.initializeElevatorThreads();
	}
}
