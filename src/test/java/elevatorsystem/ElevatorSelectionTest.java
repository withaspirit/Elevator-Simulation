package elevatorsystem;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.ElevatorRequest;
import systemwide.Direction;
import systemwide.Origin;

import java.time.LocalTime;


public class ElevatorSelectionTest {

    ElevatorRequest elevatorRequest = new ElevatorRequest(LocalTime.now(), 0, Direction.UP, 2, Origin.FLOOR_SYSTEM);
    ElevatorSubsystem elevatorSubsystem;
    Elevator elevator1, elevator2;

    @BeforeEach
    void setUp() {
        elevatorSubsystem = new ElevatorSubsystem();
        elevator1 = new Elevator(1, elevatorSubsystem);
        elevator2 = new Elevator(2, elevatorSubsystem);
        elevatorSubsystem.addElevator(elevator1);
        elevatorSubsystem.addElevator(elevator2);
    }

    @Test
    void testSelectingIdleElevators(){
        assertEquals(elevatorSubsystem.chooseElevator(elevatorRequest), 1);
        elevator1.addRequest(elevatorRequest);
        elevator1.processRequest(elevator1.getNextRequest());
        elevator1.getMotor().setMovementState(MovementState.ACTIVE);
        assertEquals(elevatorSubsystem.chooseElevator(elevatorRequest), 2);
    }

    @Test
    void testSelectActiveElevators(){
        elevator1.addRequest(elevatorRequest);
        elevator1.addRequest(elevatorRequest);
        elevator1.addRequest(elevatorRequest);
        elevator1.processRequest(elevator1.getNextRequest());
        elevator1.processRequest(elevator1.getNextRequest());
        elevator1.processRequest(elevator1.getNextRequest());

        elevator2.addRequest(elevatorRequest);
        elevator2.addRequest(elevatorRequest);
        elevator2.processRequest(elevator2.getNextRequest());
        elevator2.processRequest(elevator2.getNextRequest());

        elevator1.getMotor().setMovementState(MovementState.ACTIVE);
        elevator2.getMotor().setMovementState(MovementState.ACTIVE);

        assertEquals(elevatorSubsystem.chooseElevator(elevatorRequest), 2);
    }
}
