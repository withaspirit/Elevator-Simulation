package scheduler;

import elevatorsystem.Doors;
import elevatorsystem.Fault;
import elevatorsystem.MovementState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.ElevatorMonitor;
import requests.ServiceRequest;
import systemwide.Direction;
import systemwide.Origin;

import javax.swing.*;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * PresenterTest tests for the Presenter class methods and integration with the system
 *
 * @author Julian, Liam Tripp
 */
public class PresenterTest {

    private Presenter presenter;
    private ElevatorViewContainer elevatorViewContainer;
    private static final int NUMBER_OF_ELEVATORS = 20;

    @BeforeEach
    void setUp() {
        presenter = new Presenter();
        elevatorViewContainer = new ElevatorViewContainer(NUMBER_OF_ELEVATORS);
        presenter.addView(elevatorViewContainer);
    }

    @Test
    void testElevatorViewDefaultStatus() {
        // TODO: assert the default status of each elevatorView is correct
    }

    @Test
    void testElevatorViewUpdate() {
        // Updating first view
        int elevatorNumber = 1;
        int currentFloor = 2;
        Direction currentDirection = Direction.DOWN;
        ServiceRequest currentRequest = new ServiceRequest(LocalTime.now(), currentFloor, currentDirection, Origin.SCHEDULER);
        currentRequest.setElevatorNumber(elevatorNumber);

        MovementState movementState = MovementState.ACTIVE;
        Direction movementDirection = Direction.UP;
        Doors.State doorsState = Doors.State.OPEN;
        Fault fault = Fault.NONE;

        ElevatorMonitor elevatorMonitor = new ElevatorMonitor(elevatorNumber, currentFloor, currentDirection, movementState, movementDirection, doorsState, fault, false, 0);
        elevatorMonitor.setCurrentRequest(currentRequest);
        presenter.updateElevatorView(elevatorMonitor);

        ElevatorView elevatorView = elevatorViewContainer.getElevatorView(elevatorMonitor.getElevatorNumber());
        JTextPane[] statusPanes = elevatorView.getStatusPanes();

        assertEquals(Integer.toString(currentFloor), statusPanes[0].getText());
        assertEquals(currentDirection.toString(), statusPanes[1].getText());
        assertEquals(movementState.getName(), statusPanes[2].getText());
        assertEquals( movementDirection.getName(), statusPanes[3].getText());
        assertEquals(doorsState.toString(), statusPanes[4].getText());
        assertEquals( fault.getName(), statusPanes[5].getText());
        // exclude time from request.toString()
        int localTimeCutoffIndex = "HH:mm:ss.SSS ".length();
        assertEquals(currentRequest.toString().substring(localTimeCutoffIndex), statusPanes[6].getText());
    }
}
