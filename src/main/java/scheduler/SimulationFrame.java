package scheduler;

import systemwide.Structure;

/**
 * SimulationFrame demos the GUI of the simulation.
 *
 * @author Liam Tripp
 */
public class SimulationFrame {

    public static void main(String[] args) {
        Structure structure = new Structure(10, 2, 1000, 1000);

        ElevatorViewContainer elevatorViewContainer = new ElevatorViewContainer(structure.getNumberOfElevators());
        Presenter presenter = new Presenter();
        presenter.addView(elevatorViewContainer);
        presenter.startGUI();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        structure.initializeStructure(presenter);
    }
}
