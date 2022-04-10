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
	private final ArrayList<Thread> elevatorThreads;
	private final Client server;
	private final Queue<SystemEvent> eventQueue;
	private final SystemStatus systemStatus;

	/**
	 * Constructor for ElevatorSubsystem.
	 */
	public ElevatorSubsystem() {
		server = new Client(Port.SERVER.getNumber());
		elevatorList = new ArrayList<>();
		elevatorThreads = new ArrayList<>();
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
	 * Returns the list of Elevator Threads.
	 *
	 * @return the list of Elevator Threads
	 */
	public ArrayList<Thread> getElevatorThreads() {
		return elevatorThreads;
	}

	/**
	 * Simple message requesting and sending between subsystems.
	 * ElevatorSubsystem
	 * Sends: ApproachEvent, ElevatorMonitor
	 * Receives: ApproachEvent, ElevatorRequest
	 */
	public void run() {
		systemStatus.setSystemActivated(true);
		while (systemStatus.activated()) {
			subsystemUDPMethod();
		}
		// terminate elevator threads
		for (Elevator elevator : elevatorList) {
			elevator.getSystemStatus().setSystemActivated(false);
		}
		System.out.println(getClass().getSimpleName() + " Thread terminated");
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
	 * Adds a SystemEvent to a System's queue of events.
	 *
	 * @param systemEvent the SystemEvent to add
	 */
	@Override
	public void addEventToQueue(SystemEvent systemEvent) {
		eventQueue.add(systemEvent);
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
				} else if (string.trim().equals(RequestMessage.TERMINATE.getMessage())) {
					systemStatus.setSystemActivated(false);
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
			elevator.setTravelTime(structure.getElevatorTime());
			elevator.setDoorTime(structure.getDoorsTime());
			addElevator(elevator);
		}
	}

	/**
	 * Initializes the Elevator threads for the ElevatorSubsystem.
	 */
	public void initializeElevatorThreads() {
		for (Elevator elevator : elevatorList) {
			Thread newElevatorThread = new Thread(elevator, elevator.getClass().getSimpleName() + " " + elevator.getElevatorNumber());
			elevatorThreads.add(newElevatorThread);
		}
		// Start elevator Threads
		for (Thread thread : elevatorThreads) {
			thread.start();
		}
	}

	/**
	 * Receives and returns a Structure from the Scheduler.
	 *
	 * @return Structure contains information to initialize the floors and elevators
	 */
	@Override
	public Structure receiveStructure() {
		return (Structure) server.receive();
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
