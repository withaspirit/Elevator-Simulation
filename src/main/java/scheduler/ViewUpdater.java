package scheduler;

import requests.ElevatorMonitor;

/**
 * ViewUpdater updates the View of the system with information from the Model.
 *
 * @author Liam Tripp
 */
// TODO: rename this class, the name is so bad
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
        ElevatorView elevatorView = elevatorViewContainer.getElevatorView(elevatorMonitor.getElevatorNumber() - 1);
        elevatorView.update(elevatorMonitor);
    }


}
