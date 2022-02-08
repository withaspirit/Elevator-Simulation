package scheduler;

import misc.*;

/**
 * Scheduler handles the requests from all system components
 * 
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class Scheduler implements Runnable, ServiceRequestListener {

	private final BoundedBuffer elevatorSubsystemBuffer; // ElevatorSubsystem - Scheduler link
	private final BoundedBuffer floorSubsystemBuffer; // FloorSubsystem- Scheduler link
	// private ArrayList<Elevator> elevators;
	// private ArrayList<Floor> floors;
	private Origin origin;

	/**
	 * Constructor for Scheduler
	 *
	 * @param buffer1
	 * @param buffer2
	 */
	public Scheduler(BoundedBuffer buffer1, BoundedBuffer buffer2) {
		// create floors and elevators here? or in a SchedulerModel
		// add subsystems to elevators, pass # floors
		this.elevatorSubsystemBuffer = buffer1;
		this.floorSubsystemBuffer = buffer2;
		origin = Origin.SCHEDULER;
	}

	/**
	 * Simple message requesting and sending between subsystems.
	 * 
	 */
	public void run() {
		// TODO: make getting input file size a method?
		InputFileReader inputFileReader = new InputFileReader();
		int numberOfInputs = inputFileReader.readInputFile("inputs").size();
		for (int i = 0; i < numberOfInputs * 2; i++) {
			ServiceRequest serviceRequest = receiveRequest(floorSubsystemBuffer);
			sendRequest(serviceRequest, elevatorSubsystemBuffer);
			serviceRequest = receiveRequest(elevatorSubsystemBuffer);
			sendRequest(serviceRequest, floorSubsystemBuffer);
		}
		/*
		// Receiving Data from Floor Subsystem
		if (receiveRequest(floorSubsystemBuffer)) {
			System.out.println("Scheduler received Request from Floor SubSystem Successful");
		} else {
			System.out.println("Failed Successful");
		}

		// Receiving Data from Elevator Subsystem
		if (receiveRequest(elevatorSubsystemBuffer)) {
			System.out.println("Scheduler received Request from Elevator SubSystem Successful");
		} else {
			System.out.println("Failed Successful");
		}
		 */
	}

	/**
	 * Puts the request message into the buffer
	 *
	 * @param request the message being sent
	 * @param buffer the BoundedBuffer used for sending the request
	 * @return true if request is successful, false otherwise
	 */
	public boolean sendRequest(ServiceRequest request, BoundedBuffer buffer) {
		sendMessage(request, buffer);
		return true;
	}

	/**
	 * Removes a ServiceRequest from the Buffer.
	 *
	 * @param buffer the BoundedBuffer used for receiving the request
	 * @return serviceRequest a request by a person on a floor or in an elevator
	 */
	public ServiceRequest receiveRequest(BoundedBuffer buffer) {
		return (ServiceRequest) receiveMessage(buffer);
	}

	/**
	 * Checks the buffer for messages
	 * 
	 * @param buffer the BoundedBuffer used for receiving the request
	 * @return true if request is successful, false otherwise
	 */
	public boolean receiveRequestBoolean(BoundedBuffer buffer) {
		ServiceRequest request = buffer.removeFirst(origin);
		System.out.println(Thread.currentThread().getName() + " received the request: " + request);

		if (request instanceof FloorRequest floorRequest){
			if (sendRequest(floorRequest, floorSubsystemBuffer)) {
				System.out.println("Scheduler Sent Request to floor Successful");
			} else {
				System.out.println("Failed Successful");
			}
		} else if (request instanceof ElevatorRequest elevatorRequest){
			if (sendRequest(elevatorRequest, elevatorSubsystemBuffer)) {
				System.out.println("Scheduler Sent Request to Elevator Successful");
			} else {
				System.out.println("Failed Successful");
			}
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}

		return true;
	}
}
