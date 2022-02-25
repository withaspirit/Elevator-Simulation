package elevatorsystem;

import requests.*;
import systemwide.BoundedBuffer;
import systemwide.Direction;
import systemwide.Origin;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * ElevatorSubsystem manages the elevators and their requests to the Scheduler
 *
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class ElevatorSubsystem implements Runnable, SubsystemMessagePasser {

    private final BoundedBuffer elevatorSubsystemBuffer; // Elevator Subsystem - Scheduler link
	private Elevator elevator;
	private FloorsQueue floorsQueue;
	private ElevatorMotor motor;
	// list must be volatile so that origin checks if it's been updated
	// functionally, this is a stack (FIFO)
	private volatile CopyOnWriteArrayList<ServiceRequest> requests;

	/**
	 * Constructor for ElevatorSubsystem.
	 *
	 * @param buffer the buffer the ElevatorSubsystem passes messages to and receives messages from
	 */
	public ElevatorSubsystem(BoundedBuffer buffer, int elevatorNumber) {
		this.elevatorSubsystemBuffer = buffer;
		floorsQueue = new FloorsQueue();
		motor = new ElevatorMotor();
		elevator = new Elevator(elevatorNumber, floorsQueue, motor);
		new Thread(elevator).start();
		requests = new CopyOnWriteArrayList<>();
	}

	/**
	 * Simple message requesting and sending between subsystems.
	 * ElevatorSubsystem
	 * Sends: ApproachEvent
	 * Receives: ApproachEvent, ElevatorRequest
	 */
	public void run() {
//		while (true) {
//			SystemEvent request = receiveMessage(elevatorSubsystemBuffer, origin);
//			if (request instanceof ElevatorRequest elevatorRequest) {
//				// Choose elevator
//				// Move elevator
//				sendMessage(new FloorRequest(elevatorRequest, elevator.getElevatorNumber()), elevatorSubsystemBuffer, origin);
//				System.out.println(origin + " Sent Request Successful to Scheduler");
//			} else if (request instanceof ApproachEvent approachEvent) {
//				System.out.println(elevator.getCurrentFloor());
//				elevator.receiveApproachEvent(approachEvent);
//			}
//		}
	}

	/**
	 * Gets the total expected time that the elevator will need to take to
	 * perform its current requests along with the new elevatorRequest.
	 *
	 * @param elevatorRequest a service request to visit a floor
	 * @return a double containing the elevator's total expected queue time
	 */
	public double getExpectedTime(ElevatorRequest elevatorRequest) {
		return floorsQueue.getQueueTime() + elevator.LOAD_TIME + elevator.requestTime(elevatorRequest);
	}

	/**
	 * Returns the elevator that the subsystem has.
	 *
	 * @return the elevator that the subsystem has
	 */
	public Elevator getElevator() {
		return elevator;
	}

	public ElevatorMotor getMotor() {
		return motor;
	}

	public void addRequest(ElevatorRequest elevatorRequest) {
		floorsQueue.addFloor(elevatorRequest.getFloorNumber(),elevator.getCurrentFloor(),elevatorRequest.getDesiredFloor(), elevatorRequest.getDirection());
		System.out.println("Elevator #" + elevator.getElevatorNumber() + " added request " + elevatorRequest);
		motor.setMovementState(MovementState.ACTIVE);
		if (motor.getDirection() == Direction.NONE) {
			motor.setDirection(elevatorRequest.getDirection());
		}
		floorsQueue.setQueueTime(getExpectedTime(elevatorRequest));
		System.out.println("Time remaining for Elevator #"+ elevator.getElevatorNumber()+" request : " + floorsQueue.getQueueTime());
		sendMessage(new FloorRequest(elevatorRequest, elevator.getElevatorNumber()), elevatorSubsystemBuffer, Origin.SCHEDULER);
	}
}
