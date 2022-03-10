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
	private Queue<SystemEvent> requestQueue;
	private IntermediateHost intermediateHost;
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
	 * Takes a DatagramPacket from the IntermediateHost and processes it if it's Data rather
	 * than a request for data.
	 */
	public void receiveAndProcessPacket() {
		DatagramPacket receivePacket = intermediateHost.receivePacket();

		// if request is data, proceed. otherwise,
		if (intermediateHost.processReceivePacket(receivePacket)) {
			processData(receivePacket);
		}
	}

	/**
	 * Process data that Scheduler's DatagramSocket has received.
	 *
	 * @param packet a DatagramPacket containing a SystemEvent
	 */
	// FIXME: this is the only part of the code that Scheduler needs access to, all other parts are optional
	public void processData(DatagramPacket packet) {

		// identify the Origin of the packet, take action accordingly
		SystemEvent event = intermediateHost.convertPacketToSystemEvent(packet);
		Origin eventOrigin = event.getOrigin();

		if (eventOrigin == Origin.ELEVATOR_SYSTEM) {
			// scheduler method here to do FLOORSUBSYSTEM stuff
			event.setOrigin(Origin.changeOrigin(eventOrigin));
			packet.setPort(Port.CLIENT.getNumber());
		} else if (eventOrigin == Origin.FLOOR_SYSTEM) {
			// scheduler method here to do ELEVATORSUBSYSTEM stuff
			event.setOrigin(Origin.changeOrigin(eventOrigin));
			packet.setPort(Port.SERVER.getNumber());
		} else {
			System.err.println("Error: unexpected origin: ");
			// printReceiveMessage(name, packet);
		}
		// intermediate host
		intermediateHost.addNewMessageToQueue(event, packet);
	}

	/**
	 * Simple message requesting and sending between subsystems.
	 * Scheduler
	 * Sends: ApproachEvent, FloorRequest, ElevatorRequest
	 * Receives: ApproachEvent, ElevatorRequest
	 */
	public void run() {
		while(true) {
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
					if (request instanceof ElevatorRequest elevatorRequest){
						sendMessage(elevatorRequest, elevatorSubsystemBuffer, origin);
						System.out.println("Scheduler Sent Request to Elevator Successful");
					} else if (request instanceof ApproachEvent approachEvent) {
						// FIXME: this code might be redundant as it's identical to the one above
						sendMessage(approachEvent, elevatorSubsystemBuffer, origin);
					}
				} else if (request.getOrigin() == Origin.ELEVATOR_SYSTEM) {
					if (request instanceof StatusResponse) {

					} else if (request instanceof FloorRequest floorRequest){
						sendMessage(floorRequest, floorSubsystemBuffer, origin);
						System.out.println("Scheduler Sent Request to Floor Successful");
					} else if (request instanceof ApproachEvent approachEvent) {
						sendMessage(approachEvent, floorSubsystemBuffer, origin);
					}
				} else {
					System.err.println("Scheduler should not contain items whose origin is Scheduler: " + request);
				}
 			}
		}
	}
}
