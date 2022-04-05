package scheduler;

import elevatorsystem.MovementState;
import requests.ElevatorMonitor;
import systemwide.Direction;

/**
 * SimulatoinFrame demos the GUI of the simulation.
 *
 * @author Liam Tripp
 */
public class SimulationFrame {

    public static void main(String[] args) {
        ElevatorViewContainer elevatorViewContainer = new ElevatorViewContainer(20);
        Presenter presenter = new Presenter();
        presenter.addView(elevatorViewContainer);

        presenter.frameSetup();
        ElevatorMonitor elevatorMonitor = new ElevatorMonitor(0);
        elevatorMonitor.updateMonitor(new ElevatorMonitor(0, MovementState.ACTIVE, 2, Direction.DOWN, 0, true));
        presenter.updateElevatorView(elevatorMonitor);
    }
}
