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
 * @author Julian
 * */
public class PresenterTest {

    private Presenter presenter;
    private ElevatorViewContainer elevatorViewContainer;

    @BeforeEach
    void setUp() {
        presenter = new Presenter();
        elevatorViewContainer = new ElevatorViewContainer(20);
    }

    @Test
    void testUpdateElevatorView() {
        presenter.addView(elevatorViewContainer);

        //Updating first view
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
        JTextPane[] panes = elevatorView.getStatusPanes();
        assertEquals(Integer.toString(currentFloor), panes[0].getText());
        assertEquals(currentDirection.toString(), panes[1].getText());
        assertEquals(movementState.getName(), panes[2].getText());
        assertEquals( movementDirection.getName(), panes[3].getText());
        assertEquals(doorsState.toString(), panes[4].getText());
        assertEquals( fault.getName(), panes[5].getText());
        // exclude time from request.toString()
        int localTimeCutoffIndex = "HH:mm:ss.SSS ".length();
        assertEquals(currentRequest.toString().substring(localTimeCutoffIndex), panes[6].getText());
    }
}
