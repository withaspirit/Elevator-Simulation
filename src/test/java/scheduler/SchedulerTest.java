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

import javax.xml.transform.Result;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SchedulerTest tests the Scheduler's methods for passing data back and forth between the 3 systems
 *
 * @author Brady
 */
class SchedulerTest {

    BoundedBuffer elevator;
    BoundedBuffer floor;
    Scheduler scheduler;
    ElevatorSubsystem elevatorSys;
    FloorSubsystem floorSys;
    ServiceRequest req;

    @BeforeEach
    void setUp() {
        // Request
        req = new ServiceRequest(LocalTime.NOON, 1, Direction.UP);

        // Set up buffers
        elevator = new BoundedBuffer();
        floor = new BoundedBuffer();

        // Set up systems
        scheduler = new Scheduler(elevator, floor);
        elevatorSys = new ElevatorSubsystem(elevator);
        floorSys = new FloorSubsystem(floor);
    }

    @AfterEach
    void tearDown() {}

    @Test
    void sendElevatorRequest() {
        // Send req from scheduler to elevator buffer
        scheduler.sendRequest(req, elevator);

        // Elevator receives request from buffer
        ServiceRequest result = elevatorSys.receiveRequest();

        // Verify values
        assertEquals(LocalTime.NOON, result.getTime());
        assertEquals(1, result.getFloorNumber());
        assertEquals(Direction.UP, result.getDirection());
    }

    @Test
    void sendFloorRequest() {
        // Send req from scheduler to elevator buffer
        scheduler.sendRequest(req, floor);

        // Elevator receives request from buffer
        ServiceRequest result = floorSys.receiveRequest();

        // Verify values
        assertEquals(LocalTime.NOON, result.getTime());
        assertEquals(1, result.getFloorNumber());
        assertEquals(Direction.UP, result.getDirection());
    }

    @Test
    void receiveElevatorRequest() {
        // Send request to buffer
        elevatorSys.sendRequest(req);

        // Scheduler receives request from buffer
        ServiceRequest result = scheduler.receiveRequest(elevator);

        // Verify values
        assertEquals(LocalTime.NOON, result.getTime());
        assertEquals(1, result.getFloorNumber());
        assertEquals(Direction.UP, result.getDirection());
    }

    @Test
    void receiveFloorRequest() {
        // Ensure buffer is initially empty
        assertTrue(floor.isEmpty());

        // Send request to buffer
        floorSys.sendRequest(req);

        // Scheduler receives request from buffer
        ServiceRequest result = scheduler.receiveRequest(floor);

        // Verify values
        assertEquals(LocalTime.NOON, result.getTime());
        assertEquals(1, result.getFloorNumber());
        assertEquals(Direction.UP, result.getDirection());
    }
}