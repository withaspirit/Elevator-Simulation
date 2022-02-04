package floorsystem;

import javax.swing.JButton;

import misc.*;
import scheduler.Scheduler;

import java.util.ArrayList;

/**
 * FloorSubsystem manages the floors and their requests to the Scheduler
 * 
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class FloorSubsystem implements Runnable {

	private final BoundedBuffer schedulerFloorsubBuffer; // Floor Subsystem- Scheduler link
	private final ArrayList<ElevatorRequest> requests;
	private FloorRequest floorRequest;

	public FloorSubsystem(BoundedBuffer buffer) {
		this.schedulerFloorsubBuffer = buffer;
		InputFileReader inputFileReader = new InputFileReader();
		requests = inputFileReader.readInputFile("inputs");
	}

	// readInputFile();

	/**
	 * Simple message requesting and sending between subsystems.
	 *
	 */
	public void run() {
		int size = requests.size();
		for (int i = 0; i < size; i++) {
			System.out.println("Queue Size " + requests.size());

			ElevatorRequest elevatorRequest = requests.get(0);
			sendRequest(elevatorRequest);
			FloorRequest floorRequest = (FloorRequest) receiveRequest();
			System.out.println("Printing buffer contents for buffer");
			schedulerFloorsubBuffer.printBufferContents();
		}
		/*
		while (!requests.isEmpty()) {
			// Sending Data to Scheduler
			if (sendRequest(requests.get(0))) {
				System.out.println("Floor Subsystem Sent Request Successful to Scheduler");
			} else {
				System.out.println("Failed Successful");
			}

			// Receiving Data from Scheduler
			if (receiveRequest()) {
				System.out.println("Expected Elevator# "+ floorRequest.getElevatorNumber() + " Arrived");
			} else {
				System.out.println("Failed Successful");
			}
		}

		 */
	}

	/**
	 * Puts the request message into the buffer
	 * 
	 * @param request the message being sent
	 * @return true if request is successful, false otherwise
	 */
	public boolean sendRequest(ElevatorRequest request) {
		System.out.println(Thread.currentThread().getName() + " sending: " + request);
		schedulerFloorsubBuffer.addLast(request);
		requests.remove(0);

		try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
			System.err.println(e);
		}

		return true;
	}

	/**
	 * Removes a ServiceRequest from the Buffer.
	 *
	 * @return serviceRequest a request by a person on a floor or in an elevator
	 */
	public ServiceRequest receiveRequest() {
		ServiceRequest request = schedulerFloorsubBuffer.removeFirst();
		System.out.println(Thread.currentThread().getName() + " received the request: " + request);

		try {
			Thread.sleep(0);
		} catch (InterruptedException e) {
			System.err.println(e);
		}
		return request;
	}

	/**
	 * Checks the buffer for messages
	 *
	 * @return true if request is successful, false otherwise
	 */
	public boolean receiveRequestBoolean() {
		ServiceRequest request = schedulerFloorsubBuffer.removeFirst();
		System.out.println(Thread.currentThread().getName() + " received the request: " + request + "\n");

		if (request instanceof FloorRequest floorRequest){
			this.floorRequest = floorRequest;
		} else if (request instanceof ElevatorRequest){
			System.err.println("Incorrect Request. This is for an elevator");
		}

		try {
			Thread.sleep(0);
		} catch (InterruptedException e) {
			System.err.println(e);
		}

		return true;
	}
}
