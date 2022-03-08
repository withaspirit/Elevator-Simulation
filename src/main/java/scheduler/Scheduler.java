package scheduler;

import elevatorsystem.Elevator;
import elevatorsystem.ElevatorSubsystem;
import requests.*;
import systemwide.BoundedBuffer;
import systemwide.Direction;
import systemwide.Origin;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Scheduler handles the requests from all system components
 *
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class Scheduler implements Runnable, SubsystemMessagePasser {

    private final BoundedBuffer elevatorSubsystemBuffer; // ElevatorSubsystem - Scheduler link
    private final BoundedBuffer floorSubsystemBuffer; // FloorSubsystem- Scheduler link
    private ArrayList<ElevatorSubsystem> elevatorSubsystemList;
    private Origin origin;
    private Queue<SystemEvent> requestQueue;

    /**
     * Constructor for Scheduler
     *
     * @param elevatorSubsystemBuffer a BoundedBuffer for Requests between the Scheduler and elevatorSubsystem
     * @param floorSubsystemBuffer    a BoundedBuffer for Requests between the Scheduler and floorSubsystem
     * @param elevatorSubsystemList
     */
    public Scheduler(BoundedBuffer elevatorSubsystemBuffer, BoundedBuffer floorSubsystemBuffer, ArrayList<ElevatorSubsystem> elevatorSubsystemList) {
        // create floors and elevators here? or in a SchedulerModel
        // add subsystems to elevators, pass # floors
        this.elevatorSubsystemBuffer = elevatorSubsystemBuffer;
        this.floorSubsystemBuffer = floorSubsystemBuffer;
        this.elevatorSubsystemList = elevatorSubsystemList;
        requestQueue = new LinkedList<>();
        origin = Origin.SCHEDULER;
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
        for (ElevatorSubsystem elevatorSubsystem : elevatorSubsystemList) {
            //TODO this is a temporary way to access the elevatorSubsystem, UDP messaging will be direct using port
            Elevator elevator = elevatorSubsystem.getElevator();

            double tempExpectedTime = elevatorSubsystem.getExpectedTime(elevatorRequest);
            if (elevatorSubsystem.getMotor().isIdle()) {
                return elevator.getElevatorNumber();

            } else if (!elevatorSubsystem.getMotor().isActive()) {
                System.err.println("Elevator is stuck");

            } else if (elevatorSubsystem.getMotor().getDirection() == elevatorRequest.getDirection()) {
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
     * Scheduler
     * Sends: ApproachEvent, FloorRequest, ElevatorRequest
     * Receives: ApproachEvent, ElevatorRequest
     */
    public void run() {
        while (true) {
            SystemEvent request = null;
            // remove from either floorBuffer or ElevatorBuffer
            if (floorSubsystemBuffer.canRemoveFromBuffer(origin)) {
                request = receiveMessage(floorSubsystemBuffer, origin);
                requestQueue.add(request);
            } else if (elevatorSubsystemBuffer.canRemoveFromBuffer(origin)) {
                request = receiveMessage(elevatorSubsystemBuffer, origin);
                requestQueue.add(request);
            }

			// send a request if possible
			if (!requestQueue.isEmpty()) {
				request = requestQueue.remove();

				if (request.getOrigin() == Origin.FLOOR_SYSTEM) {
					if (request instanceof ElevatorRequest elevatorRequest){
						sendMessage(elevatorRequest, elevatorSubsystemBuffer, origin);
						System.out.println("Scheduler Sent Request to Elevator Successful");
					} else if (request instanceof ApproachEvent approachEvent) {
						// FIXME: this code might be redundant as it's identical to the one above
						sendMessage(approachEvent, elevatorSubsystemBuffer, origin);
					}
				} else if (request.getOrigin() == Origin.ELEVATOR_SYSTEM) {
					if (request instanceof StatusResponse) {

					} else if (request instanceof FloorRequest floorRequest){
						sendMessage(floorRequest, floorSubsystemBuffer, origin);
						System.out.println("Scheduler Sent Request to Floor Successful");
					} else if (request instanceof ApproachEvent approachEvent) {
						sendMessage(approachEvent, floorSubsystemBuffer, origin);
					}
				} else {
					System.err.println("Scheduler should not contain items whose origin is Scheduler: " + request);
				}
 			}
		}
	}
}
