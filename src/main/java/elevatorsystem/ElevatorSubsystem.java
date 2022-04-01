package elevatorsystem;

import client_server_host.Client;
import client_server_host.Port;
import client_server_host.RequestMessage;
import requests.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * ElevatorSubsystem manages the elevators and their requests to the Scheduler
 *
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class ElevatorSubsystem implements Runnable, SystemEventListener {

	private final Client server;
	private final Elevator elevator;
	private final Queue<SystemEvent> eventQueue;

	/**
	 * Constructor for ElevatorSubsystem.
	 */
	public ElevatorSubsystem(Elevator elevator) {
		server = new Client(Port.SERVER.getNumber() + elevator.getElevatorNumber());
		this.elevator = elevator;
		eventQueue = new LinkedList<>();
	}

	/**
	 * Simple message requesting and sending between subsystems.
	 * ElevatorSubsystem
	 * Sends: ApproachEvent, ElevatorMonitor
	 * Receives: ApproachEvent, ElevatorRequest
	 */
	public void run() {
		subsystemUDPMethod();
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
	 * Sends and receives messages for system using UDP packets.
	 */
	private void subsystemUDPMethod() {
		while (true) {
			Object object;
			if (!eventQueue.isEmpty()) {
				object = server.sendAndReceiveReply(eventQueue.remove());
			} else {
				object = server.sendAndReceiveReply(RequestMessage.REQUEST.getMessage());
			}

			if (object instanceof ElevatorRequest elevatorRequest) {
				elevator.addRequest(elevatorRequest);
				eventQueue.add(elevator.makeElevatorMonitor());
			} else if (object instanceof ApproachEvent approachEvent) {
				elevator.receiveApproachEvent(approachEvent);
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

	/**
	 * Initialize the elevatorSubsystem with elevators
	 * and start threads for each elevator and the elevatorSubsystem.
	 *
	 * @param args not used
	 */
	public static void main(String[] args) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		int numberOfElevators = 2;
		ArrayList<Elevator> elevatorList = new ArrayList<>();
		for (int elevatorNumber = 1; elevatorNumber <= numberOfElevators; elevatorNumber++) {
			Elevator elevator = new Elevator(elevatorNumber);
			elevatorList.add(elevator);
		}

		// Start elevator Origins
		for (int i = 0; i < numberOfElevators; i++) {
			(new Thread(elevatorList.get(i), elevatorList.get(i).getClass().getSimpleName())).start();
		}
	}
}
