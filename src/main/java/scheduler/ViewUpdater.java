package scheduler;

import requests.ElevatorMonitor;

/**
 * ViewUpdater updates the View of the system with information from the Model.
 *
 * @author Liam Tripp
 */
public class ViewUpdater {

    private ElevatorViewContainer elevatorViewContainer;

    /**
     * Constructor for ViewUpdater.
     */
    public ViewUpdater() {

    }

    /**
     * Adds an ElevatorViewContainer to the ViewUpdater.
     *
     * @param elevatorViewContainer the View component that displays the list of elevators
     */
    public void addView(ElevatorViewContainer elevatorViewContainer) {
        this.elevatorViewContainer = elevatorViewContainer;
    }

    /*
    public void addModel() {

    }
     */

    public void receiveElevatorMonitor(ElevatorMonitor elevatorMonitor) {

    }


}
