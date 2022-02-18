package elevatorsystem;

import requests.ElevatorRequest;
import requests.FloorRequest;
import requests.ServiceRequestListener;
import requests.SystemEvent;
import systemwide.BoundedBuffer;
import systemwide.Direction;

import java.util.ArrayList;


/**
 * ElevatorSubsystem manages the elevators and their requests to the Scheduler
 *
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class ElevatorSubsystem implements Runnable, ServiceRequestListener {

    private final BoundedBuffer elevatorSubsystemBuffer; // Elevator Subsystem - Scheduler link
    private final ArrayList<Elevator> elevatorList;

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
     * Returns an elevator number corresponding to an elevator that is
     * best suited to perform the given ElevatorRequest based on
     * expected time to fulfill the request and direction of elevator.
     *
     * @param elevatorRequest an ElevatorRequest
     * @return a number corresponding to an elevator
     */
    public int chooseElevator(ElevatorRequest elevatorRequest) {
        double elevatorBestExpectedTime = 0.0;
        double elevatorWorstExpectedTime = 0.0;
        int chosenBestElevator = 0;
        int chosenWorstElevator = 0;
        for (Elevator elevator : elevatorList) {
//			sendMessage(new StatusRequest(elevatorRequest,Thread.currentThread(), i), elevatorSubsystemBuffer, Thread.currentThread());
//			SystemEvent request = receiveMessage(elevatorSubsystemBuffer, Thread.currentThread());
            double tempExpectedTime = elevator.getExpectedTime(elevatorRequest);
            if (elevator.getState() == MovementState.IDLE) {
                return elevator.getElevatorNumber();

            } else if (elevator.getState() == MovementState.STUCK) {
                System.err.println("Elevator is stuck");

            } else if (elevator.getCurrentDirection() == elevatorRequest.getDirection()) {
                if (elevatorBestExpectedTime == 0 || elevatorBestExpectedTime > tempExpectedTime) {
                    if (elevatorRequest.getDirection() == Direction.DOWN && elevator.getCurrentFloor() > elevatorRequest.getDesiredFloor()) {
                        elevatorBestExpectedTime = tempExpectedTime;
                        chosenBestElevator = elevator.getElevatorNumber();

                    } else if (elevatorRequest.getDirection() == Direction.UP && elevator.getCurrentFloor() < elevatorRequest.getDesiredFloor()) {
                        elevatorBestExpectedTime = tempExpectedTime;
                        chosenBestElevator = elevator.getElevatorNumber();
                    } else {
                        // Add to the third queue of the elevator
                    }
                }

            } else {
                if (elevatorWorstExpectedTime == 0 || elevatorWorstExpectedTime > tempExpectedTime) {
                    elevatorWorstExpectedTime = tempExpectedTime;
                    chosenWorstElevator = elevator.getElevatorNumber();
                }
            }
        }
        if (chosenBestElevator == 0) {
            chosenBestElevator = chosenWorstElevator;
        }
        return chosenBestElevator;
    }

    /**
     * Simple message requesting and sending between subsystems.
     */
    public void run() {
        while (true) {
            SystemEvent request = receiveMessage(elevatorSubsystemBuffer, Thread.currentThread());
            if (request instanceof ElevatorRequest elevatorRequest) {
                int chosenElevator = chooseElevator(elevatorRequest);
                sendMessage(new FloorRequest(elevatorRequest, chosenElevator), elevatorSubsystemBuffer, Thread.currentThread());
                System.out.println(Thread.currentThread().getName() + " Sent Request Successful to Scheduler");
            }
        }
    }
}
