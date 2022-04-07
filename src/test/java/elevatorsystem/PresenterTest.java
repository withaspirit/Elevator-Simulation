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
    	
    	int elevatorNubmer = 0;
        int currentFloor = 2;
        Direction currentDirection = Direction.DOWN;
        MovementState movementState = MovementState.ACTIVE;
        Direction movementDirection = Direction.UP;
        Doors.State doorsState = Doors.State.OPEN;
        Fault fault = Fault.NONE;
    	
        ElevatorMonitor elevatorMonitor = new ElevatorMonitor(0, 2, Direction.DOWN, MovementState.ACTIVE, Direction.UP, Doors.State.OPEN, Fault.NONE, false, 0);
    	presenter.updateElevatorView(elevatorMonitor);
    	
    	ElevatorView elevatorView = elevatorViewContainer.getElevatorView(elevatorMonitor.getElevatorNumber());
    	JTextPane[] panes = elevatorView.getStatusPanes();
    	assertEquals(panes[0].getText(), Integer.toString(currentFloor));
    	assertEquals(panes[1].getText(), currentDirection.toString());
    	assertEquals(panes[2].getText(), movementState.toString());
    	//assertEquals(panes[3].getText(), movementDirection.toString());
    	//assertEquals(panes[4].getText(), doorsState.toString());
    	//assertEquals(panes[5].getText(), fault.toString());
    }
}
