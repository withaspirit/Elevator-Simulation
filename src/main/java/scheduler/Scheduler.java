package scheduler;

import client_server_host.IntermediateHost;
import client_server_host.Port;
import requests.*;
import systemwide.BoundedBuffer;
import systemwide.Origin;

import java.net.DatagramPacket;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Scheduler handles the requests from all system components
 *
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class Scheduler implements Runnable, SubsystemMessagePasser {

	private final BoundedBuffer elevatorSubsystemBuffer; // ElevatorSubsystem - Scheduler link
	private final BoundedBuffer floorSubsystemBuffer; // FloorSubsystem- Scheduler link
	private final Origin origin = Origin.SCHEDULER;
	private final Queue<SystemEvent> requestQueue;
	private final IntermediateHost intermediateHost;
	// private ArrayList<Elevator> elevators;
	// private ArrayList<Floor> floors;

	/**
	 * Constructor for Scheduler
	 *
	 * @param elevatorSubsystemBuffer a BoundedBuffer for Requests between the Scheduler and elevatorSubsystem
	 * @param floorSubsystemBuffer a BoundedBuffer for Requests between the Scheduler and floorSubsystem
	 */
	public Scheduler(BoundedBuffer elevatorSubsystemBuffer, BoundedBuffer floorSubsystemBuffer) {
		// create floors and elevators here? or in a SchedulerModel
		// add subsystems to elevators, pass # floors
		this.elevatorSubsystemBuffer = elevatorSubsystemBuffer;
		this.floorSubsystemBuffer = floorSubsystemBuffer;
		intermediateHost = null;
		requestQueue = new LinkedList<>();
	}

	/**
	 * Constructor for Scheduler.
	 *
	 * @param portNumber the port number associated with the class's DatagramSocket
	 */
	public Scheduler(int portNumber) {
		elevatorSubsystemBuffer = null;
		floorSubsystemBuffer = null;
		intermediateHost = new IntermediateHost(portNumber);
		requestQueue = new LinkedList<>();
	}

	/**
	 * Takes a DatagramPacket from the IntermediateHost and processes it.
	 * If it's data (i.e. contains a SystemEvent), it is processed by Scheduler.
	 * Otherwise, it's a request for data and is processed by IntermediateHost.
	 */
	public void receiveAndProcessPacket() {
		while (true) {
			DatagramPacket receivePacket = intermediateHost.receivePacket();

			// if request is data, process it as data.
			// otherwise it is a data request
			if (intermediateHost.processPacketObject(receivePacket)) {
				processData(receivePacket);
			} else {
				intermediateHost.respondToDataRequest(receivePacket);
			}
		}
	}

	/**
	 * Process data that Scheduler's DatagramSocket has received.
	 * Create a new packet and manipulate it according to the packet's Origin.
	 *
	 * @param packet a DatagramPacket containing a SystemEvent
	 */
	public void processData(DatagramPacket packet) {

		// identify the Origin of the packet
		SystemEvent event = intermediateHost.convertToSystemEvent(packet);
		Origin eventOrigin = event.getOrigin();

		// manipulate the packet according to its origin
		if (eventOrigin == Origin.ELEVATOR_SYSTEM) {
			// scheduler method here to do FLOORSUBSYSTEM stuff
			packet.setPort(Port.CLIENT.getNumber());
		} else if (eventOrigin == Origin.FLOOR_SYSTEM) {
			// scheduler method here to do ELEVATORSUBSYSTEM stuff
			packet.setPort(Port.SERVER.getNumber());
		} else {
			throw new IllegalArgumentException("Error: Invalid Origin");
		}
		event.setOrigin(Origin.changeOrigin(eventOrigin));
		// intermediate host
		intermediateHost.addNewPacketToQueue(event, packet);
	}

	/**
	 * Sends and receives messages for the system using BoundedBuffers.
	 */
	private void subsystemBufferRunMethod() {
		while (true) {
			SystemEvent request;
			// remove from either floorBuffer or ElevatorBuffer
			if (floorSubsystemBuffer.canRemoveFromBuffer(origin)) {
				request = receiveMessage(floorSubsystemBuffer, origin);
				requestQueue.add(request);
			} else if (elevatorSubsystemBuffer.canRemoveFromBuffer(origin)) {
				request = receiveMessage(elevatorSubsystemBuffer, origin);
				requestQueue.add(request);
			}

			// send a request if possible
			if (!requestQueue.isEmpty()) {
				request = requestQueue.remove();

				if (request.getOrigin() == Origin.FLOOR_SYSTEM) {
					if (request instanceof ElevatorRequest elevatorRequest) {
						sendMessage(elevatorRequest, elevatorSubsystemBuffer, origin);
					} else if (request instanceof ApproachEvent approachEvent) {
						// FIXME: this code might be redundant as it's identical to the one above
						sendMessage(approachEvent, elevatorSubsystemBuffer, origin);
					}
				} else if (request.getOrigin() == Origin.ELEVATOR_SYSTEM) {
					if (request instanceof ElevatorMonitor) {

					} else if (request instanceof FloorRequest floorRequest) {
						sendMessage(floorRequest, floorSubsystemBuffer, origin);
					} else if (request instanceof ApproachEvent approachEvent) {
						sendMessage(approachEvent, floorSubsystemBuffer, origin);
					}
				} else {
					System.err.println("Scheduler should not contain items whose origin is Scheduler: " + request);
				}
			}
		}
	}

	/**
	 * Simple message requesting and sending between subsystems.
	 * Scheduler
	 * Sends: ApproachEvent, FloorRequest, ElevatorRequest
	 * Receives: ApproachEvent, ElevatorRequest
	 */
	public void run() {
		// take action depending on if using buffers or IntermediateHost
		if (intermediateHost != null) {
			receiveAndProcessPacket();
		} else {
			subsystemBufferRunMethod();
		}
	}

	public static void main(String[] args) {
		new Thread(new Scheduler(Port.CLIENT_TO_SERVER.getNumber()), "Client To Server").start();
		new Thread(new Scheduler(Port.SERVER_TO_CLIENT.getNumber()), "Server To Client").start();
	}
}
