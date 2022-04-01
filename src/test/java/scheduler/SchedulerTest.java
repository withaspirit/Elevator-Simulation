package scheduler;

import client_server_host.Port;
import elevatorsystem.Elevator;
import elevatorsystem.ElevatorSubsystem;
import elevatorsystem.MovementState;
import floorsystem.FloorSubsystem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import requests.ElevatorMonitor;
import requests.ElevatorRequest;
import requests.SystemEvent;
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

    private static Scheduler schedulerClient;
    private static Scheduler schedulerServer;
    private static FloorSubsystem floorSubsystem;
    private static Elevator elevator1;
    private static SystemEvent elevatorRequest;
    private static SystemEvent elevatorMonitor;

    @BeforeAll
    static void setUpOnce() {
        // Request
        elevatorRequest = new ElevatorRequest(LocalTime.now(), 1, Direction.UP, 2, Origin.FLOOR_SYSTEM);
        elevatorMonitor = new ElevatorMonitor(0.0, MovementState.ACTIVE, 1, Direction.UP, 1, false);
        elevatorMonitor.setOrigin(Origin.ELEVATOR_SYSTEM);

        // Set up systems
        schedulerClient = new Scheduler(Port.CLIENT_TO_SERVER.getNumber());
        schedulerServer = new Scheduler(Port.SERVER_TO_CLIENT.getNumber());
        floorSubsystem = new FloorSubsystem();
        elevator1 = new Elevator(1);
        schedulerClient.addElevatorMonitor(elevator1.getElevatorNumber());

        new Thread(floorSubsystem, floorSubsystem.getClass().getSimpleName()).start();
        new Thread(schedulerClient, schedulerClient.getClass().getSimpleName()).start();
        new Thread(schedulerServer, schedulerServer.getClass().getSimpleName()).start();
    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void sendElevatorRequest() {
        assertTrue(elevator1.getRequestQueue().isEmpty());

        try {
            schedulerClient.processData(elevatorRequest);
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // Elevator receives request from buffer
        assertFalse(elevator1.getRequestQueue().isCurrentQueueEmpty());

        // Verify values
        assertEquals(1, elevator1.getRequestQueue().removeRequest());
        assertEquals(2, elevator1.getRequestQueue().removeRequest());
        assertEquals(Direction.UP, elevator1.getServiceDirection());
    }

    @Test
    void sendElevatorMonitor() {
        // Send req from scheduler to FloorBuffer
        try {
            schedulerServer.processData(elevatorMonitor);
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(1, floorSubsystem.getEventListSize()); // fails because floorSubsystem processes requests instantly
    }

    @Test
    void receiveElevatorRequest() {
        floorSubsystem.addEvent(elevatorRequest);

    }

    @Test
    void receiveFloorRequest() {

    }
}