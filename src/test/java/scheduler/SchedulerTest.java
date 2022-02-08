package scheduler;

import elevatorsystem.ElevatorSubsystem;
import floorsystem.FloorSubsystem;
import misc.BoundedBuffer;
import misc.ElevatorRequest;
import misc.ServiceRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import systemwide.Direction;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SchedulerTest tests the Scheduler's methods for passing data back and forth between the 3 systems
 *
 * @author Brady
 */
class SchedulerTest {

    private BoundedBuffer elevatorBuffer;
    private BoundedBuffer floorBuffer;
    private Scheduler scheduler;
    private ElevatorSubsystem elevatorSubsystem;
    private FloorSubsystem floorSubsystem;
    private Thread schedulerThread, elevatorThreadSubsystem, floorThreadSubsystem;
    private ServiceRequest serviceRequest;

    @BeforeEach
    void setUp() {
        // Request
        serviceRequest = new ServiceRequest(LocalTime.NOON, 1, Direction.UP);

        // Set up buffers
        elevatorBuffer = new BoundedBuffer();
        floorBuffer = new BoundedBuffer();

        // Set up systems
        scheduler = new Scheduler(elevatorBuffer, floorBuffer);
        elevatorSubsystem = new ElevatorSubsystem(elevatorBuffer);
        floorSubsystem = new FloorSubsystem(floorBuffer);
        schedulerThread = new Thread(scheduler);
        elevatorThreadSubsystem = new Thread (elevatorSubsystem);
        floorThreadSubsystem = new Thread (floorSubsystem);
    }

    @AfterEach
    void tearDown() {}

    @Test
    void sendElevatorRequest() {
        // Send req from scheduler to elevator buffer
        scheduler.sendMessage(serviceRequest, elevatorBuffer, schedulerThread);
        assertEquals(1, elevatorBuffer.getSize());

        // Elevator receives request from buffer
        ServiceRequest result = elevatorSubsystem.receiveMessage(elevatorBuffer, elevatorThreadSubsystem);
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
        scheduler.sendMessage(serviceRequest, floorBuffer, schedulerThread);
        assertEquals(1, floorBuffer.getSize());

        // Elevator receives request from buffer
        ServiceRequest result = floorSubsystem.receiveMessage(floorBuffer, floorThreadSubsystem);
        assertEquals(0, floorBuffer.getSize());

        // Verify values
        assertEquals(LocalTime.NOON, result.getTime());
        assertEquals(1, result.getFloorNumber());
        assertEquals(Direction.UP, result.getDirection());
    }

    @Test
    void receiveElevatorRequest() {
        // Send request to buffer
        elevatorSubsystem.sendMessage(serviceRequest, elevatorBuffer, elevatorThreadSubsystem);

        // Scheduler receives request from buffer
        ServiceRequest result = scheduler.receiveMessage(elevatorBuffer, schedulerThread);

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
        floorSubsystem.sendMessage(serviceRequest, floorBuffer, floorThreadSubsystem);

        // Scheduler receives request from buffer
        ServiceRequest result = scheduler.receiveMessage(floorBuffer, schedulerThread);

        // Verify values
        assertEquals(LocalTime.NOON, result.getTime());
        assertEquals(1, result.getFloorNumber());
        assertEquals(Direction.UP, result.getDirection());
    }
}