package elevatorsystem;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.ElevatorRequest;
import scheduler.Scheduler;
import systemwide.BoundedBuffer;
import systemwide.Direction;
import systemwide.Origin;

import java.time.LocalTime;
import java.util.ArrayList;


public class ElevatorSelectionTest {

    ElevatorRequest elevatorRequest = new ElevatorRequest(LocalTime.now(), 0, Direction.UP, 2, Origin.FLOOR_SYSTEM);
    BoundedBuffer elevatorSubsystemBuffer = new BoundedBuffer();
    ElevatorSubsystem elevatorSubsystem;
    Elevator elevator1, elevator2;
    ArrayList<Elevator> elevators;
    Scheduler scheduler;

    @BeforeEach
    void setUp() {
        elevatorSubsystem = new ElevatorSubsystem(elevatorSubsystemBuffer);
        elevator1 = new Elevator(1, elevatorSubsystem);
        elevator2 = new Elevator(2, elevatorSubsystem);
        elevatorSubsystem.addElevator(elevator1);
        elevatorSubsystem.addElevator(elevator2);
        elevators = new ArrayList<>();
        elevators.add(elevator1);
        elevators.add(elevator2);
        scheduler = new Scheduler(new BoundedBuffer(), elevatorSubsystemBuffer, elevators);
    }

    @Test
    void testSelectingIdleElevators(){
        assertEquals(scheduler.chooseElevator(elevatorRequest), 1);
        elevator1.addRequest(elevatorRequest);
        elevator1.getMotor().setMovementState(MovementState.ACTIVE);
        assertEquals(scheduler.chooseElevator(elevatorRequest), 2);
    }

    @Test
    void testSelectActiveElevators(){
        elevator1.addRequest(elevatorRequest);
        elevator1.addRequest(elevatorRequest);
        elevator1.addRequest(elevatorRequest);

        elevator2.addRequest(elevatorRequest);
        elevator2.addRequest(elevatorRequest);

        elevator1.getMotor().setMovementState(MovementState.ACTIVE);
        elevator2.getMotor().setMovementState(MovementState.ACTIVE);

        assertEquals(scheduler.chooseElevator(elevatorRequest), 2);
    }
}
