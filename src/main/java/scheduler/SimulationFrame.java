package scheduler;

import elevatorsystem.Doors;
import elevatorsystem.Fault;
import elevatorsystem.MovementState;
import requests.ElevatorMonitor;
import systemwide.Direction;

/**
 * SimulationFrame demos the GUI of the simulation.
 *
 * @author Liam Tripp
 */
public class SimulationFrame {

    public static void main(String[] args) {
        ElevatorViewContainer elevatorViewContainer = new ElevatorViewContainer(20);
        Presenter presenter = new Presenter();
        presenter.addView(elevatorViewContainer);

        presenter.startGUI();

        ElevatorMonitor elevatorMonitor = new ElevatorMonitor(0);
        elevatorMonitor.updateMonitor(new ElevatorMonitor(0, 2, Direction.DOWN, MovementState.ACTIVE, Direction.UP, Doors.State.OPEN, Fault.NONE, false, 0));
        presenter.updateElevatorView(elevatorMonitor);
    }
}
