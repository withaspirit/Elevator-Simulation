package scheduler;

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

import static org.junit.jupiter.api.Assertions.*;

/**
 * SchedulerTest tests the Scheduler's methods for passing data back and forth between the 3 systems
 *
 * @author Brady, Ryan Dash
 */
class SchedulerTest {

    private BoundedBuffer elevatorBuffer;
    private BoundedBuffer floorBuffer;
    private Scheduler scheduler;
    private ElevatorSubsystem elevatorSubsystem;
    private FloorSubsystem floorSubsystem;
    private ServiceRequest serviceRequest;

    @BeforeEach
    void setUp() {
        // Request
        serviceRequest = new ServiceRequest(LocalTime.NOON, 1, Direction.UP, Origin.FLOOR_SYSTEM);

        // Set up buffers
        elevatorBuffer = new BoundedBuffer();
        floorBuffer = new BoundedBuffer();

        // Set up systems
        scheduler = new Scheduler(elevatorBuffer, floorBuffer);
        elevatorSubsystem = new ElevatorSubsystem(elevatorBuffer);
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