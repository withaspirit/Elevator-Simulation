package scheduler;

import GUI.ElevatorView;
import GUI.ElevatorViewContainer;
import GUI.Presenter;
import elevatorsystem.Doors;
import elevatorsystem.Fault;
import elevatorsystem.MovementState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.ElevatorMonitor;
import systemwide.Direction;

import javax.swing.*;

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
        elevatorViewContainer = new ElevatorViewContainer(4, 20);
    }

    @Test
    void testUpdateElevatorView() {
        presenter.addView(elevatorViewContainer);

        //Updating first view
        int elevatorNumber = 0;
        int currentFloor = 2;
        Direction currentDirection = Direction.DOWN;
        MovementState movementState = MovementState.ACTIVE;
        Direction movementDirection = Direction.UP;
        Doors.State doorsState = Doors.State.OPEN;
        Fault fault = Fault.NONE;

        ElevatorMonitor elevatorMonitor = new ElevatorMonitor(elevatorNumber, currentFloor, currentDirection, movementState, movementDirection, doorsState, fault, false, 0);
        presenter.updateElevatorView(elevatorMonitor);

        ElevatorView elevatorView = elevatorViewContainer.getElevatorView(elevatorMonitor.getElevatorNumber());
        JTextPane[] panes = elevatorView.getStatusPanes();
        assertEquals(panes[0].getText(), Integer.toString(currentFloor));
        assertEquals(panes[1].getText(), currentDirection.toString());
        assertEquals(panes[2].getText(), movementState.getName());
        assertEquals(panes[3].getText(), movementDirection.getName());
        assertEquals(panes[4].getText(), doorsState.toString());
        assertEquals(panes[5].getText(), fault.getName());


        //Updating a second view for a different elevator
        elevatorNumber = 3;
        currentFloor = 1;
        currentDirection = Direction.UP;
        movementState = MovementState.ACTIVE;
        movementDirection = Direction.DOWN;
        doorsState = Doors.State.OPEN;
        fault = Fault.ELEVATOR_STUCK;

        elevatorMonitor = new ElevatorMonitor(elevatorNumber, currentFloor, currentDirection, movementState, movementDirection, doorsState, fault, false, 0);
        presenter.updateElevatorView(elevatorMonitor);

        elevatorView = elevatorViewContainer.getElevatorView(elevatorMonitor.getElevatorNumber());
        panes = elevatorView.getStatusPanes();
        assertEquals(panes[0].getText(), Integer.toString(currentFloor));
        assertEquals(panes[1].getText(), currentDirection.toString());
        assertEquals(panes[2].getText(), movementState.getName());
        assertEquals(panes[3].getText(), movementDirection.getName());
        assertEquals(panes[4].getText(), doorsState.toString());
        assertEquals(panes[5].getText(), fault.getName());
    }
}
