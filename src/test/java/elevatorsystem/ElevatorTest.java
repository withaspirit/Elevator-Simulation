package elevatorsystem;

import misc.InputFileReader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.ElevatorRequest;
import requests.SystemEvent;
import systemwide.Origin;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

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
        elevatorList = new ArrayList<>();
        InputFileReader inputFileReader = new InputFileReader();
        eventList = inputFileReader.readInputFile(InputFileReader.INPUTS_FILENAME);
    }

    @AfterEach
    void tearDown() {
        // this prevents multiple elevators from being added
        elevatorList.clear();
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
        threads = new ArrayList<>();
        for (Elevator elevator : elevatorList) {
            Thread thread = new Thread(elevator);
            thread.start();
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

            for (Elevator elevator: elevatorList) {
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
    // NOTE: sometimes this doesn't work
    void testOneElevatorFulfillsRequestsMultipleTimes() {
        for (int i = 0; i < numberOfTimesToTest; i++) {
            testOneElevatorFulfillsRequests();
        }
    }

    @Test
    void testOneThreadedElevatorFulfillsRequests() {
        int numberOfElevators = 1;
        initNumberOfElevators(numberOfElevators);
        addRequestsToElevators();
        initElevatorThreads();

        // NOTE: test doesn't work if this is commented out
        try {
            TimeUnit.MILLISECONDS.sleep(numberOfMilliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // wait while elevator has requests
        while (!elevatorList.get(0).hasNoRequests()) {

        }
        assertEquals(1, elevatorList.size());
        assertTrue(elevatorList.get(0).hasNoRequests());
    }

    @Test
    // NOTE: this doesn't always work.
    void testOneThreadedElevatorFulfillsRequestsMultipleTimes() {
        for (int i = 0; i < numberOfTimesToTest; i++) {
            testOneThreadedElevatorFulfillsRequests();
        }
    }

    // FIXME: this fails
    @Test
    void testTwoThreadedElevatorsFulfillRequests() {
        int numberOfElevators = 2;
        initNumberOfElevators(numberOfElevators);
        addRequestsToElevators();
        initElevatorThreads();

        try {
            TimeUnit.MILLISECONDS.sleep(numberOfMilliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Elevator elevator: elevatorList) {
            assertTrue(elevator.hasNoRequests());
        }

        elevatorList = new ArrayList<>();
    }

    // FIXME: this always fails due to elevator selection not working
    @Test
    void testTwoElevatorsMultipleTimes() {
        for (int i = 0; i < numberOfTimesToTest; i++) {
            testTwoThreadedElevatorsFulfillRequests();
        }
    }
}