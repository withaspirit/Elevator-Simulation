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
        assertEquals(panes[0].getText(), Integer.toString(currentFloor));
        assertEquals(panes[1].getText(), currentDirection.toString());
        assertEquals(panes[2].getText(), movementState.getName());
        assertEquals(panes[3].getText(), movementDirection.getName());
        assertEquals(panes[4].getText(), doorsState.toString());
        assertEquals(panes[5].getText(), fault.getName());
        // exclude time from request.toString()
        int localTimeCutoffIndex = "HH:mm:ss.SSS ".length();
        assertEquals(panes[6].getText(), currentRequest.toString().substring(localTimeCutoffIndex));
    }
}
