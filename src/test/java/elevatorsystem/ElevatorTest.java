package elevatorsystem;

import misc.InputFileReader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.ElevatorRequest;
import requests.SystemEvent;
import systemwide.Origin;

import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests an Elevator's Service Algorithm without transferring messages.
 * Uses inputs from a file.
 *
 * @author Liam Tripp, Brady Norton
 */
class ElevatorTest {

    private ElevatorSubsystem elevatorSubsystem;
    private ArrayList<Elevator> elevatorList;
    private ArrayList<SystemEvent> eventList;
    private final int numberOfMilliseconds = 300;
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

    @AfterEach
    void tearDown() {
        // this prevents multiple elevators from being added
        // elevatorList.clear();
        threads.clear();
    }

    /**
     * Initializes the list of elevators with the specified number of elevators.
     *
     * @param numberOfElevators the number of elevators in the list of elevators
     */
    private void initNumberOfElevators(int numberOfElevators) {
        // initialize the list of elevators
        for (int i = 1; i <= numberOfElevators; i++) {
            Elevator elevator = new Elevator(i, elevatorSubsystem);
            System.out.println("Elevator " + i + " instantiated.");
            elevatorList.add(elevator);
            elevatorSubsystem.addElevator(elevator);
            elevator.toggleMessageTransfer();
        }
    }

    /**
     * Adds requests from the selected file to the elevator.
     */
    private void addRequestsToElevators() {
        // add requests to elevators
        // setup requests to be read inputs
        for (SystemEvent event : eventList) {
            ElevatorRequest elevatorRequest = (ElevatorRequest) event;
            // this set origin is defensive in case origin ends up affecting elevator1 request
            elevatorRequest.setOrigin(Origin.ELEVATOR_SYSTEM);
            int chosenElevator = elevatorSubsystem.chooseElevator(elevatorRequest);
            elevatorList.get(chosenElevator - 1).addRequest(elevatorRequest);
        }
    }

    /**
     * Starts the elevator threads.
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
            thread.start();
            // wait until thread dies before executing the next thread
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // FIXME: this fails, unequal distribution of requests
    @Test
    void testTwoElevatorSelection() {
        int numberOfElevators = 2;
        initNumberOfElevators(numberOfElevators);
        addRequestsToElevators();

        for (Elevator elevator : elevatorList) {
            assertFalse(elevator.hasNoRequests());
        }
    }

    // test up to 7 elevators
    // FIXME: this fails
    @Test
    void testElevatorSelectionAtLeastOneRequestPerElevator() {
        // ensure each elevator has at least one request
        // this tests for even distribution of elevators
        // does for up to 7 elevators
        for (int i = 1; i <= eventList.size(); i++) {
            initNumberOfElevators(i);
            addRequestsToElevators();

            for (Elevator elevator : elevatorList) {
                System.out.println("Testing Elevator " + elevator.getElevatorNumber());
                assertFalse(elevator.hasNoRequests());
            }
        }
    }

    @Test
    void testOneElevatorFulfillsRequests() {
        int numberOfElevators = 1;
        initNumberOfElevators(numberOfElevators);
        addRequestsToElevators();
        // if this statement isn't included, it doesn't work
        elevatorList.get(0).moveElevatorWhilePossible();
        assertTrue(elevatorList.get(0).hasNoRequests());
    }

    @Test
    void testOneElevatorFulfillsRequestsMultipleTimes() {
        for (int i = 0; i < numberOfTimesToTest * 5; i++) {
            testOneElevatorFulfillsRequests();
        }
    }

    @Test
    void testOneThreadedElevatorFulfillsRequests() {
        int numberOfElevators = 1;
        // this statement clears elevators in case more than necessary are currently being tested
        elevatorList.clear();
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
        elevatorList.clear();
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
}