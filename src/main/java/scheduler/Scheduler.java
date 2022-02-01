package scheduler;

import java.util.ArrayList;

import misc.BoundedBuffer;

/**
 * 
 * 
 * @author Liam Tripp, Julian
 */
public class Scheduler implements Runnable{
	
	private BoundedBuffer schedulerElevatorsubBuffer;	//Elevator Subsystem - Scheduler link
	private BoundedBuffer schedulerFloorsubBuffer;		//Floor Subsystem- Scheduler link	
	// private ArrayList<Elevator> elevators;
	// private ArrayList<Floor> floors;
	
	public Scheduler(BoundedBuffer buffer1, BoundedBuffer buffer2) {
		// create floors and elevators here? or in a SchedulerModel
		// add subsystems to elevators, pass # floors
		this.schedulerElevatorsubBuffer = buffer1;
		this.schedulerFloorsubBuffer = buffer2;
		
		
	}
	
	
	/**
	 * Simple message requesting and sending between subsystems.
	 * 
	 */
	public void run() 
	{
		
		//Receiving Data from Floor Subsystem
		if (receiveRequest(schedulerFloorsubBuffer)) 
		{
			System.out.println("Receive Request Successful");
		} else {
			System.out.println("Failed Successful");
		}
		
		//Sending Data to Floor Subsystem
		if (sendRequest("Turn Light On", schedulerFloorsubBuffer)) 
		{
			System.out.println("Send Request Successful");
		} else {
			System.out.println("Failed Successful");
		}
		
		
		//Receiving Data from Floor Subsystem
		if (receiveRequest(schedulerElevatorsubBuffer)) 
		{
			System.out.println("Receive Request Successful");
		} else {
			System.out.println("Failed Successful");
		}
		
		//Sending Data to Floor Subsystem
		if (sendRequest("Motor Up", schedulerElevatorsubBuffer)) 
		{
			System.out.println("Send Request Successful");
		} else {
			System.out.println("Failed Successful");
		}
		
	}

	/**
	 * Puts the request message into the buffer
	 * 
	 * @param	request	the message being sent
	 * @param 	buffer	the buffer used for sending the request
	 * @return 			request success
	 */
	public boolean sendRequest(String request, BoundedBuffer buffer) {
		System.out.println(Thread.currentThread().getName() + " requested for: " + request);
		buffer.addLast(request);
		
		try {
			Thread.sleep(500);
		}catch (InterruptedException e) {}
		
		return true;
	}
	
	
	/**
	 * Checks the buffer for messages
	 * 
	 * @param 	buffer	the buffer used for receiving the request
	 * @return 			request success
	 */
	public boolean receiveRequest(BoundedBuffer buffer) {
		String request = (String)buffer.removeFirst();
		System.out.println(Thread.currentThread().getName() + " received the request: " + request);

		
		try {
			Thread.sleep(500);
		}catch (InterruptedException e) {}
		
		return true;
	}

}
