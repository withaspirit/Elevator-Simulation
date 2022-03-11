package scheduler;

import elevatorsystem.Elevator;
import elevatorsystem.ElevatorSubsystem;
import floorsystem.FloorSubsystem;
import systemwide.BoundedBuffer;
import requests.ServiceRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import systemwide.Direction;
import systemwide.Origin;

import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SchedulerTest tests the Scheduler's methods for passing data back and forth between the 3 systems
 *
 * @author Brady, Ryan Dash
 */
class SchedulerTest {

    private ServiceRequest serviceRequest;
    private BoundedBuffer elevatorBuffer;
    private BoundedBuffer floorBuffer;
    private ElevatorSubsystem elevatorSubsystem;
    private ArrayList<Elevator> elevators;
    private Elevator elevator;
    private Scheduler scheduler;
    private FloorSubsystem floorSubsystem;

    @BeforeEach
    void setUp() {
        // Request
        serviceRequest = new ServiceRequest(LocalTime.NOON, 1, Direction.UP, Origin.FLOOR_SYSTEM);

        // Set up buffers
        elevatorBuffer = new BoundedBuffer();
        floorBuffer = new BoundedBuffer();

        // Set up systems
        elevatorSubsystem = new ElevatorSubsystem(elevatorBuffer);
        elevators = new ArrayList<>();
        elevator = new Elevator(1, elevatorSubsystem);
        elevators.add(elevator);
        scheduler = new Scheduler(elevatorBuffer, floorBuffer, elevators.size());
        floorSubsystem = new FloorSubsystem(floorBuffer);
    }

    @AfterEach
    void tearDown() {}

    @Test
    void sendElevatorRequest() {
        // Send req from scheduler to elevator buffer
        scheduler.sendMessage(serviceRequest, elevatorBuffer, Origin.SCHEDULER);
        assertEquals(1, elevatorBuffer.getSize());

        // Elevator receives request from buffer
        ServiceRequest result = (ServiceRequest) elevatorSubsystem.receiveMessage(elevatorBuffer, Origin.ELEVATOR_SYSTEM);
        System.out.println(result);
        assertEquals(0, elevatorBuffer.getSize());

        // Verify values
        assertEquals(LocalTime.NOON, result.getTime());
        assertEquals(1, result.getFloorNumber());
        assertEquals(Direction.UP, result.getDirection());
    }

    @Test
    void sendFloorRequest() {
        // Send req from scheduler to FloorBuffer
        scheduler.sendMessage(serviceRequest, floorBuffer, Origin.SCHEDULER);
        assertEquals(1, floorBuffer.getSize());

        // Elevator receives request from buffer
        ServiceRequest result = (ServiceRequest) floorSubsystem.receiveMessage(floorBuffer, Origin.FLOOR_SYSTEM);
        assertEquals(0, floorBuffer.getSize());

        // Verify values
        assertEquals(LocalTime.NOON, result.getTime());
        assertEquals(1, result.getFloorNumber());
        assertEquals(Direction.UP, result.getDirection());
    }

    @Test
    void receiveElevatorRequest() {
        // Send request to buffer
        elevatorSubsystem.sendMessage(serviceRequest, elevatorBuffer, Origin.ELEVATOR_SYSTEM);

        // Scheduler receives request from buffer
        ServiceRequest result = (ServiceRequest) scheduler.receiveMessage(elevatorBuffer, Origin.SCHEDULER);

        // Verify values
        assertEquals(LocalTime.NOON, result.getTime());
        assertEquals(1, result.getFloorNumber());
        assertEquals(Direction.UP, result.getDirection());
    }

    @Test
    void receiveFloorRequest() {
        // Ensure buffer is initially empty
        assertTrue(floorBuffer.isEmpty());

        // Send request to buffer
        floorSubsystem.sendMessage(serviceRequest, floorBuffer, Origin.FLOOR_SYSTEM);

        // Scheduler receives request from buffer
        ServiceRequest result = (ServiceRequest) scheduler.receiveMessage(floorBuffer, Origin.SCHEDULER);

        // Verify values
        assertEquals(LocalTime.NOON, result.getTime());
        assertEquals(1, result.getFloorNumber());
        assertEquals(Direction.UP, result.getDirection());
    }
}