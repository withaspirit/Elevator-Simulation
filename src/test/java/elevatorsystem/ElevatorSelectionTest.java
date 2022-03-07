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
    Scheduler scheduler;
    Elevator elevator1, elevator2;
    ElevatorSubsystem elevatorSubsystem1, elevatorSubsystem2;
    ArrayList<Elevator> elevatorList;

    @BeforeEach
    void setUp() {
        elevator1 = new Elevator(1, elevatorSubsystemBuffer);
        elevator2 = new Elevator(2, elevatorSubsystemBuffer);
        elevatorSubsystem1 = elevator1.getElevatorSubsystem();
        elevatorSubsystem2 = elevator2.getElevatorSubsystem();
        elevatorList = new ArrayList<>();
        elevatorList.add(elevator1);
        elevatorList.add(elevator2);
        scheduler = new Scheduler(new BoundedBuffer(), elevatorSubsystemBuffer, elevatorList);
    }

    @Test
    void testSelectingIdleElevators(){
        assertEquals(scheduler.chooseElevator(elevatorRequest), 1);
        elevatorSubsystem1.addRequest(elevatorRequest);
        assertEquals(scheduler.chooseElevator(elevatorRequest), 2);
    }

    @Test
    void testSelectActiveElevators(){
        elevatorSubsystem1.addRequest(elevatorRequest);
        elevatorSubsystem1.addRequest(elevatorRequest);
        elevatorSubsystem1.addRequest(elevatorRequest);

        elevatorSubsystem2.addRequest(elevatorRequest);
        elevatorSubsystem2.addRequest(elevatorRequest);

        elevatorSubsystem1.getMotor().setMovementState(MovementState.ACTIVE);
        elevatorSubsystem2.getMotor().setMovementState(MovementState.ACTIVE);

        assertEquals(scheduler.chooseElevator(elevatorRequest), 2);
    }
}
