package elevatorsystem;

import misc.*;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

/**
 * ElevatorSubsystem manages the elevators and their requests to the Scheduler
 * 
 * @author Liam Tripp, Julian, Ryan Dash
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
			ServiceRequest request = receiveRequest();
			if (request != null){
				if (sendRequest(new FloorRequest((ElevatorRequest) request, 1))) {
					System.out.println("Elevator SubSystem Sent Request to Scheduler Successful");
				} else {
					System.out.println(Thread.currentThread().getName() + " failed sending Successful");
				}
			} else {
				System.out.println(Thread.currentThread().getName() + " failed receiving Successful");
			}


		}
	}

	/**
	 * Puts the request message into the buffer
	 *
	 * @param request the message being sent
	 * @return true if request is successful, false otherwise
	 */
	public boolean sendRequest(ServiceRequest request) {
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
	public ServiceRequest receiveRequest() {
		if(schedulerElevatorsubBuffer.checkFirst() instanceof ElevatorRequest) {
			ServiceRequest request = schedulerElevatorsubBuffer.removeFirst();
			System.out.println(Thread.currentThread().getName() + " received the request: " + request);

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return request;
		}
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
