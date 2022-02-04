package floorsystem;

import misc.*;

import java.util.ArrayList;

/**
 * FloorSubsystem manages the floors and their requests to the Scheduler
 * 
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class FloorSubsystem implements Runnable {

	private final BoundedBuffer schedulerFloorsubBuffer; // Floor Subsystem- Scheduler link
	private final ArrayList<ElevatorRequest> requests;
	private int receive;

	public FloorSubsystem(BoundedBuffer buffer) {
		this.schedulerFloorsubBuffer = buffer;
		InputFileReader inputFileReader = new InputFileReader();
		requests = inputFileReader.readInputFile("inputs");
		receive = requests.size();
	}

	/**
	 * Simple message requesting and sending between subsystems.
	 *
	 */
	public void run() {
		while (receive != 0) {
			if (!requests.isEmpty()) {
				// Sending Data to Scheduler
				if (sendRequest(requests.get(0))) {
					System.out.println(Thread.currentThread().getName() + " Sent Request Successful to Scheduler");
				} else {
					System.err.println(Thread.currentThread().getName() + " failed Sending Successful");
				}
			}

			ServiceRequest floorRequest = receiveRequest();
			// Receiving Data from Scheduler
			if (floorRequest != null) {
				receive--;
				System.out.println("Expected Elevator# "+ ((FloorRequest)floorRequest).getElevatorNumber() + " Arrived \n");
			} else {
				System.err.println(Thread.currentThread().getName() + " failed Receiving Successful");
			}
		}
		System.exit(0);
	}

	/**
	 * Puts the request message into the buffer
	 * 
	 * @param request the message being sent
	 * @return true if request is successful, false otherwise
	 */
	public boolean sendRequest(ServiceRequest request) {
		System.out.println(Thread.currentThread().getName() + " sending: " + request);
		schedulerFloorsubBuffer.addLast(request);
		requests.remove(0);

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
		if(schedulerFloorsubBuffer.checkFirst() instanceof FloorRequest) {
			ServiceRequest request = schedulerFloorsubBuffer.removeFirst();
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
