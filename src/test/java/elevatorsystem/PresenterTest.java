package elevatorsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import scheduler.Presenter;
import scheduler.ElevatorView;
import scheduler.ElevatorViewContainer;
import systemwide.Direction;
import requests.ElevatorMonitor;
import javax.swing.JTextPane;


public class PresenterTest {

	Presenter presenter;
	ElevatorViewContainer elevatorViewContainer;
	
    @BeforeEach
    void setUp() {
    	 presenter = new Presenter();
    	 elevatorViewContainer = new ElevatorViewContainer(20);
    }
    
    @Test
    void updatingViewTest(){
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
