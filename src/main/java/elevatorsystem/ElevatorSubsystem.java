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
		while(true) {
			// A sleep to allow communication between Floor Subsystem and Scheduler to
			// happen first
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// Receiving Data from Scheduler
			if (receiveRequest()) {
				System.out.println("Elevator SubSystem received Request Successful");
			} else {
				System.out.println(Thread.currentThread().getName() + " failed receiving Successful");
			}

			// Sending Data to Scheduler
			if (sendRequest(floorRequest)) { // Expect elevator # at floor #
				System.out.println("Elevator SubSystem Sent Request to Scheduler Successful");
			} else {
				System.out.println(Thread.currentThread().getName() + " failed sending Successful");
			}
		}
	}

	/**
	 * Puts the request message into the buffer
	 *
	 * @param request the message being sent
	 * @return true if request is successful, false otherwise
	 */
	public boolean sendRequest(FloorRequest request) {
		System.out.println(Thread.currentThread().getName() + " requested for: " + request);
		schedulerElevatorsubBuffer.addLast(request);

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return true;
	}

	/**
	 * Checks the buffer for messages
	 *
	 * @return true if request is successful, false otherwise
	 */
	public boolean receiveRequest() {
		if((schedulerElevatorsubBuffer.checkFirst() instanceof ElevatorRequest)) {
			ElevatorRequest request = (ElevatorRequest) schedulerElevatorsubBuffer.removeFirst();
			System.out.println(Thread.currentThread().getName() + " received the request: " + request);
			floorRequest = new FloorRequest(request, 1);

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return false;
		}

		return true;
	}
}
