package floorsystem;

import javax.swing.JButton;

import misc.BoundedBuffer;

/**
 * 
 * 
 * @author Liam Tripp
 */
public class FloorSubsystem implements Runnable{
	
	// private Scheduler scheduler;
	private BoundedBuffer schedulerFloorsubBuffer;
	
	public FloorSubsystem(BoundedBuffer buffer) {
		this.schedulerFloorsubBuffer = buffer;
	}
	
	// readInputFile();
	
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
	
	public boolean sendRequest(String request, BoundedBuffer buffer) {
		System.out.println(Thread.currentThread().getName() + " requested for: " + request);
		buffer.addLast(request);
		
		try {
			Thread.sleep(500);
		}catch (InterruptedException e) {}
		
		return true;
	}
	
	public boolean receiveRequest(BoundedBuffer buffer) {
		String request = (String)buffer.removeFirst();
		System.out.println(Thread.currentThread().getName() + " received the request: " + request);

		
		try {
			Thread.sleep(500);
		}catch (InterruptedException e) {}
		
		return true;
	}
}
