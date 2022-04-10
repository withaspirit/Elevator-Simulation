package elevatorsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.ServiceRequest;
import systemwide.Direction;
import systemwide.Origin;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ElevatorFaultTest ensures the fault methods for the elevator function correctly.
 *
 * @author Liam Tripp
 */
public class ElevatorFaultTest {

    private ElevatorSubsystem elevatorSubsystem;
    private ArrayList<Elevator> elevatorList;
    private final int numberOfTimesToTest = 20;
    private ArrayList<Thread> threads;

    @BeforeEach
    void setUp() {
        elevatorSubsystem = new ElevatorSubsystem();
        threads = new ArrayList<>();
        elevatorList = new ArrayList<>();
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
        }
    }

    /**
     * Initializes and starts the Elevator threads. Makes each Elevator execute until it is done.
     */
    private void initElevatorThreads() {
        for (Elevator elevator : elevatorList) {
            // initiate elevator threads on the elevator's moveWhilePossible() method
            Runnable testElevatorMovementRunnable = elevator::moveElevatorWhilePossible;

            Thread thread = new Thread(testElevatorMovementRunnable);
            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.start();
        }
    }

    @Test
    void testElevatorStuckFromInterrupt() {
        int numberOfElevators = 1;
        int floorNumber = 2;
        Direction requestDirection = Direction.UP;
        ServiceRequest serviceRequest = new ServiceRequest(LocalTime.now(), floorNumber, requestDirection, Origin.ELEVATOR_SYSTEM);
        ServiceRequest serviceRequest2 = new ServiceRequest(LocalTime.now(), floorNumber + 1, requestDirection, Origin.ELEVATOR_SYSTEM);

        initNumberOfElevators(numberOfElevators);
        Elevator elevator1 = elevatorList.get(0);
        elevator1.addRequest(serviceRequest);
        elevator1.addRequest(serviceRequest2);
        elevator1.toggleMessageTransfer();
        int travelTime = 300;
        elevator1.setTravelTime(travelTime);

        initElevatorThreads();

        // give elevator time to enter wait statement -> doesn't work without this
        try {
            System.out.println("Attempting sleep");
            TimeUnit.MILLISECONDS.sleep(travelTime / 3);
            System.out.println("Finished sleep");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // elevator1.interrupt() -> doesn't trigger interrupt for some reason
        threads.get(0).interrupt();

        // give elevator time to respond (set Fault) -> doesn't work without this
        try {
            TimeUnit.MILLISECONDS.sleep(travelTime / 3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Elevator #" + elevator1.getElevatorNumber() + " fault after interrupt: " + elevator1.getFault().toString());
        assertEquals(Fault.ELEVATOR_STUCK, elevator1.getFault());
        assertTrue(elevator1.hasNoRequests());
    }

    @Test
    void testElevatorStuckFromInterruptMultipleTimes() {
        for (int i = 0; i < numberOfTimesToTest; i++) {
            testElevatorStuckFromInterrupt();
        }
    }

    @Test
    void testArrivalSensorFail() {
        int numberOfElevators = 1;
        int floorNumber = 2;
        Direction requestDirection = Direction.UP;
        ServiceRequest serviceRequest = new ServiceRequest(LocalTime.now(), floorNumber, requestDirection, Origin.ELEVATOR_SYSTEM);
        ServiceRequest serviceRequest2 = new ServiceRequest(LocalTime.now(), floorNumber + 1, requestDirection, Origin.ELEVATOR_SYSTEM);

        initNumberOfElevators(numberOfElevators);
        Elevator elevator1 = elevatorList.get(0);
        elevator1.addRequest(serviceRequest);
        elevator1.addRequest(serviceRequest2);
        // include message transfer
        // enable travel time
        int travelTime = 300;
        elevator1.setTravelTime(travelTime);

        initElevatorThreads();

        // wait the same amount of time or more as the elevator's travel time
        try {
            TimeUnit.MILLISECONDS.sleep(travelTime * 2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Elevator #" + elevator1.getElevatorNumber() + " fault after: " +
                Fault.ARRIVAL_SENSOR_FAIL.getName() + ": " + elevator1.getFault().toString());
        assertEquals(Fault.ARRIVAL_SENSOR_FAIL, elevator1.getFault());
        assertTrue(elevator1.hasNoRequests());
    }

    @Test
    void testDoorsStuckOnClosing() {
        // from OPEN to CLOSED
        initNumberOfElevators(1);
        Elevator elevator1 = elevatorList.get(0);
        // disable message transfer
        elevator1.toggleMessageTransfer();
        // enable door time
        int doorTime = 300;
        elevator1.setDoorTime(doorTime);
        elevator1.setDoorsMalfunctioning(true);

        Runnable closeDoorsRunnable = () -> elevator1.changeDoorState(Doors.State.CLOSED);
        Thread elevatorThread = new Thread(closeDoorsRunnable);
        threads.add(elevatorThread);
        elevatorThread.start();

        // give elevator time to enter wait statement -> doesn't work without this
        try {
            TimeUnit.MILLISECONDS.sleep(doorTime / 3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // time should be the same as the DOOR_TIME
        try {
            TimeUnit.MILLISECONDS.sleep(doorTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Elevator #" + elevator1.getElevatorNumber() + " fault after: " +
                Fault.DOORS_STUCK.getName() + ": " + elevator1.getFault().toString());
        assertEquals(Doors.State.STUCK, elevator1.getDoors().getState());
        assertEquals(Fault.DOORS_STUCK, elevator1.getFault());
        assertTrue(elevator1.doorsAreMalfunctioning());
    }

    @Test
    void testDoorsStuckOnClosingHandled() {
        // from OPEN to CLOSED
        initNumberOfElevators(1);
        Elevator elevator1 = elevatorList.get(0);
        // disable message transfer
        elevator1.toggleMessageTransfer();
        // enable door time
        int doorTime = 300;
        elevator1.setDoorTime(doorTime);
        elevator1.setDoorsMalfunctioning(true);

        ServiceRequest serviceRequest = new ServiceRequest(LocalTime.now(), 2, Direction.UP, Origin.ELEVATOR_SYSTEM);
        elevator1.addRequest(serviceRequest);

        Runnable startMovingToFloorRunnable = () -> elevator1.startMovingToFloor(serviceRequest.getFloorNumber());
        Thread elevatorThread = new Thread(startMovingToFloorRunnable);
        threads.add(elevatorThread);
        elevatorThread.start();

        // give elevator time to enter wait statement -> doesn't work without this
        try {
            TimeUnit.MILLISECONDS.sleep(doorTime / 3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // allow door to correct itself
        try {
            TimeUnit.MILLISECONDS.sleep(doorTime * 2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Elevator #" + elevator1.getElevatorNumber() + " fault after: " +
                Fault.DOORS_STUCK.getName() + ": " + elevator1.getFault().toString());
        assertEquals(Doors.State.CLOSED, elevator1.getDoors().getState());
        assertEquals(Fault.NONE, elevator1.getFault());
    }

    @Test
    void testDoorsStuckOnOpening() {
        initNumberOfElevators(1);
        Elevator elevator1 = elevatorList.get(0);
        // disable message transfer
        elevator1.toggleMessageTransfer();
        // enable door time
        int doorTime = 300;
        elevator1.setDoorTime(doorTime);

        Runnable openDoorsRunnable = () -> elevator1.changeDoorState(Doors.State.OPEN);
        Thread elevatorThread = new Thread(openDoorsRunnable);
        threads.add(elevatorThread);
        elevatorThread.start();

        // give elevator time to enter wait statement -> doesn't work without this
        try {
            TimeUnit.MILLISECONDS.sleep(doorTime / 3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        elevator1.setDoorsMalfunctioning(true);
        // time should be the same as the DOOR_TIME
        try {
            TimeUnit.MILLISECONDS.sleep(doorTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Elevator #" + elevator1.getElevatorNumber() + " fault after: " +
                Fault.DOORS_STUCK.getName() + ": " + elevator1.getFault().toString());
        assertEquals(Doors.State.STUCK, elevator1.getDoors().getState());
        assertEquals(Fault.DOORS_STUCK, elevator1.getFault());
        assertTrue(elevator1.doorsAreMalfunctioning());
    }

    @Test
    void testDoorsStuckOnOpeningHandled() {
        // test that elevator's stopAtFloor handles getting stuck on opening
        // from OPEN to CLOSED
        initNumberOfElevators(1);
        Elevator elevator1 = elevatorList.get(0);
        // disable message transfer
        elevator1.toggleMessageTransfer();
        // enable door time
        int doorTime = 300;
        elevator1.setDoorTime(doorTime);
        elevator1.setDoorsMalfunctioning(true);

        ServiceRequest serviceRequest = new ServiceRequest(LocalTime.now(), 1, Direction.UP, Origin.ELEVATOR_SYSTEM);
        elevator1.addRequest(serviceRequest);

        Runnable stopAtFloorRunnable = () -> elevator1.stopAtFloor(serviceRequest.getFloorNumber());
        Thread elevatorThread = new Thread(stopAtFloorRunnable);
        threads.add(elevatorThread);
        elevatorThread.start();

        // give elevator time to enter wait statement -> doesn't work without this
        try {
            TimeUnit.MILLISECONDS.sleep(doorTime / 3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // allow stopAtFloor to correct door not opening
        try {
            TimeUnit.MILLISECONDS.sleep(doorTime * 3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Elevator #" + elevator1.getElevatorNumber() + " fault after: " +
                Fault.DOORS_STUCK.getName() + "handled : " + elevator1.getFault().toString());
        assertEquals(Doors.State.OPEN, elevator1.getDoors().getState());
        assertEquals(Fault.NONE, elevator1.getFault());
        assertFalse(elevator1.doorsAreMalfunctioning());
    }
}
