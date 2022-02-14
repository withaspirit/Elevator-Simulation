package elevatorsystem;

import requests.*;
import systemwide.BoundedBuffer;

import java.time.LocalTime;


/**
 * ElevatorSubsystem manages the elevators and their requests to the Scheduler
 * 
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class ElevatorSubsystem implements Runnable, ServiceRequestListener {

	private final BoundedBuffer elevatorSubsystemBuffer; // Elevator Subsystem - Scheduler link
	private Elevator elevator;

	/**
	 * Constructor for ElevatorSubsystem.
	 *
	 * @param buffer the buffer the ElevatorSubsystem passes messages to and receives messages from
	 */
	public ElevatorSubsystem(BoundedBuffer buffer) {
		this.elevatorSubsystemBuffer = buffer;
	}

	/**
	 * Simple message requesting and sending between subsystems.
	 * 
	 */
	public void run() {
		while(true) {
			SystemEvent request = receiveMessage(elevatorSubsystemBuffer, Thread.currentThread());
			if (request instanceof ElevatorRequest elevatorRequest) {
				sendMessage(new FloorRequest(elevatorRequest, 1), elevatorSubsystemBuffer, Thread.currentThread());
				System.out.println(Thread.currentThread().getName() + " Sent Request Successful to Scheduler");
			} else if (request instanceof StatusRequest statusRequest) {
				if (statusRequest.getElevatorNumber() == elevator.getElevatorNumber()){
					double expectedTime = elevator.getExpectedTime() + Math.abs(elevator.getElevation() - (statusRequest.getFloorRequest().getFloorNumber()* 4) / 2.67);
					sendMessage(new StatusResponse(LocalTime.now(), Thread.currentThread(), expectedTime, elevator.getStatus()));
				}
			}
		}
	}
}
