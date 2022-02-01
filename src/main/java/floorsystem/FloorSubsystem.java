package floorsystem;

import javax.swing.JButton;

import misc.BoundedBuffer;

/**
 * 
 * 
 * @author Liam Tripp, Julian
 */
public class FloorSubsystem implements Runnable{
	
	// private Scheduler scheduler;
	private BoundedBuffer schedulerFloorsubBuffer;		//Floor Subsystem- Scheduler link
	
	public FloorSubsystem(BoundedBuffer buffer) {
		this.schedulerFloorsubBuffer = buffer;
	}
	
	// readInputFile();
	
	
	/**
	 * Simple message requesting and sending between subsystems.
	 * 
	 */
	public void run() 
	{
		
		//Sending Data to Scheduler
		if (sendRequest("Input File", schedulerFloorsubBuffer)) 
		{
			System.out.println("Send Request Successful");
		} else {
			System.out.println("Failed Successful");
		}
		
		//Receiving Data from Scheduler
		if (receiveRequest(schedulerFloorsubBuffer)) 
		{
			System.out.println("Receive Request Successful");
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
