package elevatorsystem;

import java.time.LocalTime;
import java.util.ArrayList;
import javax.swing.JButton;

import misc.*;
import scheduler.Scheduler;
import systemwide.Direction;

/**
 * ElevatorSubsystem manages the elevators and their requests to the Scheduler
 * 
 * @author Liam Tripp, Julian
 */
public class ElevatorSubsystem implements Runnable {

	private final BoundedBuffer schedulerElevatorsubBuffer; // Elevator Subsystem - Scheduler link
	private FloorRequest floorRequest;

	public ElevatorSubsystem(BoundedBuffer buffer) {
		this.schedulerElevatorsubBuffer = buffer;

	}

	/**
	 * Simple message requesting and sending between subsystems.
	 * 
	 */
	public void run() {

		// A sleep to allow communication between Floor Subsystem and Scheduler to
		// happen first
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			System.err.println(e);
		}

		// Receiving Data from Scheduler
		if (receiveRequest(schedulerElevatorsubBuffer)) {
			System.out.println("Receive Request Successful");
		} else {
			System.out.println("Failed Successful");
		}

		// Sending Data to Scheduler
		if (sendRequest(floorRequest, schedulerElevatorsubBuffer)) { // Expect elevator # at floor #
			System.out.println("Send Request Successful");
		} else {
			System.out.println("Failed Successful");
		}
	}

	/**
	 * Puts the request message into the buffer
	 * 
	 * @param request the message being sent
	 * @param buffer the BoundedBuffer used for sending the request
	 * @return true if request is successful, false otherwise
	 */
	public boolean sendRequest(FloorRequest request, BoundedBuffer buffer) {
		System.out.println(Thread.currentThread().getName() + " requested for: " + request);
		buffer.addLast(request);

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			System.err.println(e);
		}

		return true;
	}

	/**
	 * Checks the buffer for messages
	 * 
	 * @param buffer the BoundedBuffer used for receiving the request
	 * @return true if request is successful, false otherwise
	 */
	public boolean receiveRequest(BoundedBuffer buffer) {
		ServiceRequest request = buffer.removeFirst();
		System.out.println(Thread.currentThread().getName() + " received the request: " + request);
		if (request instanceof ElevatorRequest elevatorRequest){
			if (elevatorRequest.getDirection().equals(Direction.UP)){
				floorRequest = new FloorRequest(LocalTime.now(), elevatorRequest.getDesiredFloor(), Direction.DOWN, 1);
			} else {
				floorRequest = new FloorRequest(LocalTime.now(), elevatorRequest.getDesiredFloor(), Direction.UP, 1);
			}
		} else {
			System.err.println("Incorrect Request");
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			System.err.println(e);
		}

		return true;
	}
}
