package scheduler;

import requests.ElevatorMonitor;

import javax.swing.*;
import java.awt.*;

/**
 * Presenter updates the View of the system with information from the Model.
 *
 * @author Liam Tripp
 */
public class Presenter {

    private ElevatorViewContainer elevatorViewContainer;

    /**
     * Constructor for Presenter.
     */
    public Presenter() {

    }

    /**
     * Adds an ElevatorViewContainer to the Presenter.
     *
     * @param elevatorViewContainer the View component that displays the list of elevators
     */
    public void addView(ElevatorViewContainer elevatorViewContainer) {
        this.elevatorViewContainer = elevatorViewContainer;
    }

    // TODO: decide between Scheduler and ElevatorSubsystem / ElevatorMessenge
    //  for the model
    /*
    public void addModel() {

    }
     */

    /**
     * Updates the ElevatorView corresponding to the ElevatorMonitor's elevator number.
     *
     * @param elevatorMonitor the elevatorMonitor containing the status information of the Elevator
     */
    public void updateElevatorView(ElevatorMonitor elevatorMonitor) {
        ElevatorView elevatorView = elevatorViewContainer.getElevatorView(elevatorMonitor.getElevatorNumber()-1);
        elevatorView.update(elevatorMonitor);
    }

    /**
     * Initializes the GUI of the system and makes it viewable.
     */
    public void startGUI() {
        if (elevatorViewContainer == null) {
            throw new RuntimeException("ElevatorViewContainer must be instantiated and added to " + getClass().getSimpleName() + ".");
        }

        JFrame frame = new JFrame("Elevator Simulation");
        int height = Toolkit.getDefaultToolkit().getScreenSize().height - Toolkit.getDefaultToolkit().getScreenInsets(new JDialog().getGraphicsConfiguration()).bottom;
        int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        frame.setSize(width, height);
        frame.add(elevatorViewContainer.getPanel());

        frame.setVisible(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.pack();
    }
}
