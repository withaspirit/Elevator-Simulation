package elevatorsystem;

import java.util.ArrayList;

import javax.swing.JButton;

import misc.BoundedBuffer;

/**
 * 
 * 
 * @author Liam Tripp
 */
public class ElevatorSubsystem implements Runnable {
	
	// private Scheduler scheduler;

	private BoundedBuffer schedulerElevatorsubBuffer;
	
	public ElevatorSubsystem(BoundedBuffer buffer) {
		this.schedulerElevatorsubBuffer = buffer;
		
	}
	
	public ElevatorSubsystem() {
		
	}
	
	// readInputFile();
	
	
	public void run() 
	{
		
		//A sleep to allow communication between Floor Subsystem and Scheduler to happen first
		try {
			Thread.sleep(1000);
		}catch (InterruptedException e) {}
		
		//Sending Data to Scheduler
		if (sendRequest("Floor 10 requested", schedulerElevatorsubBuffer)) 
		{
			System.out.println("Send Request Successful");
		} else {
			System.out.println("Failed Successful");
		}
		
		//Receiving Data from Scheduler
		if (receiveRequest(schedulerElevatorsubBuffer)) 
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
