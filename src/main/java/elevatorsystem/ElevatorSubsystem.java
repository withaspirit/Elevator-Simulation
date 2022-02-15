package elevatorsystem;

import requests.ElevatorRequest;
import requests.FloorRequest;
import requests.ServiceRequest;
import requests.ServiceRequestListener;
import systemwide.BoundedBuffer;

import java.util.ArrayList;

import java.time.LocalTime;
import java.util.ArrayList;


/**
 * ElevatorSubsystem manages the elevators and their requests to the Scheduler
 *
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class ElevatorSubsystem implements Runnable, ServiceRequestListener {

	private final BoundedBuffer elevatorSubsystemBuffer; // Elevator Subsystem - Scheduler link
	private ArrayList<Elevator> elevatorList;
    private final BoundedBuffer elevatorSubsystemBuffer; // Elevator Subsystem - Scheduler link
    private ArrayList<Elevator> elevatorList;

	/**
	 * Constructor for ElevatorSubsystem.
	 *
	 * @param buffer the buffer the ElevatorSubsystem passes messages to and receives messages from
	 */
	public ElevatorSubsystem(BoundedBuffer buffer) {
		this.elevatorSubsystemBuffer = buffer;
		elevatorList = new ArrayList<>();
	}

	/**
	 * Adds an elevator to the subsystem's list of elevators.
	 *
	 * @param elevator an elevator
	 */
	public void addElevator(Elevator elevator) {
		elevatorList.add(elevator);
	}
    /**
     * Constructor for ElevatorSubsystem.
     *
     * @param buffer the buffer the ElevatorSubsystem passes messages to and receives messages from
     */
    public ElevatorSubsystem(BoundedBuffer buffer) {
        this.elevatorSubsystemBuffer = buffer;
        elevatorList = new ArrayList<>();
    }

    /**
     * Adds an elevator to the subsystem's list of elevators.
     *
     * @param elevator an elevator
     */
    public void addElevator(Elevator elevator) {
        elevatorList.add(elevator);
    }

    public int chooseElevator(ElevatorRequest elevatorRequest) {
        double elevatorTime = 0.0;
        int chosenElevator = 0;
        for (Elevator elevator : elevatorList) {
//			sendMessage(new StatusRequest(elevatorRequest,Thread.currentThread(), i), elevatorSubsystemBuffer, Thread.currentThread());
//			SystemEvent request = receiveMessage(elevatorSubsystemBuffer, Thread.currentThread());
            double tempExpectedTime = elevator.getExpectedTime(elevatorRequest);
            if (elevator.getState() == MovementState.IDLE) {
                return elevator.getElevatorNumber();

            } else if (elevator.getState() == MovementState.STUCK) {
                System.err.println("Elevator is stuck");

            } else if (elevatorTime == 0 || elevatorTime > tempExpectedTime) {
                elevatorTime = tempExpectedTime;
                chosenElevator = elevator.getElevatorNumber();
            }
        }
        return chosenElevator;
    }

    /**
     * Simple message requesting and sending between subsystems.
     */
    public void run() {
        while (true) {
            SystemEvent request = receiveMessage(elevatorSubsystemBuffer, Thread.currentThread());
            if (request instanceof ElevatorRequest elevatorRequest) {
				int chosenElevator = chooseElevator(elevatorRequest);
				elevatorList.get(chosenElevator).addRequest(elevatorRequest);
				sendMessage(new FloorRequest(elevatorRequest, chosenElevator), elevatorSubsystemBuffer, Thread.currentThread());
                System.out.println(Thread.currentThread().getName() + " Sent Request Successful to Scheduler");
            } else if (request instanceof StatusRequest statusRequest) {
//				if (statusRequest.getElevatorNumber() == elevator.getElevatorNumber()){
//					double expectedTime = elevator.getExpectedTime() + Math.abs(elevator.getElevation() - (statusRequest.getFloorRequest().getFloorNumber()* 4) / 2.67);
//					sendMessage(new StatusResponse(LocalTime.now(), Thread.currentThread(), expectedTime, elevator.getStatus()));
//				}
            }
        }
    }
}
