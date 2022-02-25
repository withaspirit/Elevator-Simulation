package elevatorsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.ElevatorRequest;
import scheduler.Scheduler;
import systemwide.BoundedBuffer;
import systemwide.Direction;
import systemwide.Origin;

import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Tests an Elevator's Selection Algorithm
 * Uses inputs from a file.
 *
 * @author Ryan Dash
 */
public class ElevatorSelectionTest {

    ElevatorRequest elevatorRequest = new ElevatorRequest(LocalTime.now(), 0, Direction.UP, 2, Origin.FLOOR_SYSTEM);
    BoundedBuffer elevatorSubsystemBuffer = new BoundedBuffer();
    ElevatorSubsystem elevatorSubsystem, elevatorSubsystem2;
    ArrayList<ElevatorSubsystem> elevatorSubsystems;
    Scheduler scheduler;
    Elevator elevator1, elevator2;

    @BeforeEach
    void setUp() {
        elevatorSubsystem = new ElevatorSubsystem(elevatorSubsystemBuffer, 1);
        elevatorSubsystem2 = new ElevatorSubsystem(elevatorSubsystemBuffer, 2);
        ArrayList<ElevatorSubsystem> elevatorSubsystems = new ArrayList<>();
        elevatorSubsystems.add(elevatorSubsystem);
        elevatorSubsystems.add(elevatorSubsystem2);
        scheduler = new Scheduler(elevatorSubsystemBuffer, elevatorSubsystems);
    }

    @Test
    void testSelectingIdleElevators(){
        assertEquals(scheduler.chooseElevator(elevatorRequest), 1);
        elevatorSubsystem.addRequest(elevatorRequest);
        elevatorSubsystem.getMotor().setMovementState(MovementState.ACTIVE);
        assertEquals(scheduler.chooseElevator(elevatorRequest), 2);
    }

    @Test
    void testSelectActiveElevators(){
        elevatorSubsystem.addRequest(elevatorRequest);
        elevatorSubsystem.addRequest(elevatorRequest);
        elevatorSubsystem.addRequest(elevatorRequest);

        elevatorSubsystem2.addRequest(elevatorRequest);
        elevatorSubsystem2.addRequest(elevatorRequest);

        assertEquals(scheduler.chooseElevator(elevatorRequest), 2);
    }
}
