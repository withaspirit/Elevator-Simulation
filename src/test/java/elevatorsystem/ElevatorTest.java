package elevatorsystem;

import misc.InputFileReader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.ElevatorRequest;
import requests.FloorRequest;
import requests.ServiceRequest;
import requests.SystemEvent;
import systemwide.Direction;
import systemwide.Origin;

import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests an Elevator's Service Algorithm without transferring messages.
 * Uses events from the inputs.json file.
 *
 * @author Liam Tripp, Brady Norton
 */
class ElevatorTest {

    private ElevatorSubsystem elevatorSubsystem;
    private ArrayList<Elevator> elevatorList;
    private ArrayList<SystemEvent> eventList;
    private final int numberOfTimesToTest = 20;
    private ArrayList<Thread> threads;

    @BeforeEach
    void setUp() {
        elevatorSubsystem = new ElevatorSubsystem();
        threads = new ArrayList<>();
        elevatorList = new ArrayList<>();
        InputFileReader inputFileReader = new InputFileReader();
        eventList = inputFileReader.readInputFile(InputFileReader.INPUTS_FILENAME);
    }

    /**
     * Initializes the list of elevators with the specified number of elevators.
     *
     * @param numberOfElevators the number of elevators in the list of elevators
     */
    private void initNumberOfElevators(int numberOfElevators) {
        // clear array lists to prevent concurrency issues
        elevatorList.clear();
        threads.clear();
        // initialize the list of elevators
        for (int i = 1; i <= numberOfElevators; i++) {
            Elevator elevator = new Elevator(i, elevatorSubsystem);
            System.out.println("Elevator " + i + " instantiated");
            elevatorList.add(elevator);
            elevatorSubsystem.addElevator(elevator);
            elevator.toggleMessageTransfer();
        }
    }

    /**
     * Adds requests from the selected file to the elevator. This enables elevators
     * to move.
     */
    private void addRequestsToElevators() {
        int elevatorToAddTo = 0;
        for (SystemEvent event : eventList) {
            ElevatorRequest elevatorRequest = (ElevatorRequest) event;
            elevatorRequest.setOrigin(Origin.ELEVATOR_SYSTEM);
            // choose an elevator to add a request to
            Elevator chosenElevator = elevatorList.get(elevatorToAddTo % elevatorList.size());
            chosenElevator.addRequest(elevatorRequest);
            elevatorToAddTo++;
        }
    }

    /**
     * Initializes and starts the Elevator threads. Makes each Elevator execute until it is done.
     */
    private void initElevatorThreads() {
        for (Elevator elevator : elevatorList) {
            // initiate elevator threads on the elevator's moveWhilePossible() method
            Runnable testElevatorMovementRunnable = new Runnable() {
                @Override
                public void run() {
                    elevator.moveElevatorWhilePossible();
                }
            };

            Thread thread = new Thread(testElevatorMovementRunnable);
            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.start();
            // wait until thread dies before executing the next thread
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    void testOneElevatorFulfillsRequests() {
        int numberOfElevators = 1;
        initNumberOfElevators(numberOfElevators);
        addRequestsToElevators();

        elevatorList.get(0).moveElevatorWhilePossible();
        assertTrue(elevatorList.get(0).hasNoRequests());
    }

    @Test
    void testOneElevatorFulfillsRequestsMultipleTimes() {
        for (int i = 0; i < numberOfTimesToTest * 10; i++) {
            testOneElevatorFulfillsRequests();
        }
    }

    @Test
    void testOneThreadedElevatorFulfillsRequests() {
        int numberOfElevators = 1;
        initNumberOfElevators(numberOfElevators);
        addRequestsToElevators();
        initElevatorThreads();

        assertEquals(numberOfElevators, elevatorList.size());
        for (Elevator elevator : elevatorList) {
            assertTrue(elevator.hasNoRequests());
        }
    }

    @Test
    void testOneThreadedElevatorFulfillsRequestsMultipleTimes() {
        for (int i = 0; i < numberOfTimesToTest * 10; i++) {
            testOneThreadedElevatorFulfillsRequests();
        }
    }

    @Test
    void testTwoThreadedElevatorsFulfillRequests() {
        int numberOfElevators = 2;
        initNumberOfElevators(numberOfElevators);
        addRequestsToElevators();
        initElevatorThreads();

        assertEquals(numberOfElevators, elevatorList.size());
        for (Elevator elevator : elevatorList) {
            assertTrue(elevator.hasNoRequests());
        }
    }

    @Test
    void testTwoElevatorsMultipleTimes() {
        for (int i = 0; i < numberOfTimesToTest; i++) {
            testTwoThreadedElevatorsFulfillRequests();
        }
    }

    @Test
    void testMultipleThreadedElevatorsFulfillRequests() {
        // this tests that multiple threaded Elevators fulfill their requests to completion
        int numberOfElevators = eventList.size();
        initNumberOfElevators(numberOfElevators);
        addRequestsToElevators();
        initElevatorThreads();

        assertEquals(numberOfElevators, elevatorList.size());
        for (Elevator elevator : elevatorList) {
            assertTrue(elevator.hasNoRequests());
        }
    }

    @Test
    void testRespondToRequestConcurrencyOneElevator() {
        // there shouldn't be any concurrency errors
        // this is for adding a request while the elevator is moving
        int numberOfElevators = 1;
        initNumberOfElevators(numberOfElevators);
        int requestFloor1 = 1;
        int requestFloor2 = 2;
        ServiceRequest serviceRequest1 = new ServiceRequest(LocalTime.now(), requestFloor1, Direction.UP, Origin.ELEVATOR_SYSTEM);
        ServiceRequest serviceRequest2 = new ServiceRequest(LocalTime.now(), requestFloor2, Direction.UP, Origin.ELEVATOR_SYSTEM);

        Elevator elevator = elevatorList.get(0);
        elevator.addRequest(serviceRequest1);

        // elevator should remove Floor 1 from RequestQueue because it is on the same floor as the first request
        int floorToVisit = requestFloor1;


        elevator.compareFloors(floorToVisit);
        assertTrue(elevator.getMotor().isIdle());
        assertTrue(elevator.hasNoRequests());

        // add different request
        elevator.addRequest(serviceRequest2);
        elevator.moveToNextFloor(requestFloor1);

        if (!elevator.getMotor().isIdle()) {
            elevator.compareFloors(requestFloor1);
        }
    }
}
