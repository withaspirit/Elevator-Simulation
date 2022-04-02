package scheduler;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * ElevatorViewContainer displays a list of ElevatorViews and provides methods
 * to access them.
 *
 * @author Liam Tripp
 */
public class ElevatorViewContainer {

    // TODO: some of these could easily be local variables
    private ArrayList<ElevatorView> elevatorViews;
    private JPanel elevatorListPanel;
    private JScrollPane scrollPane;
    private JPanel containerPanel;

    /**
     * Constructor for ElevatorViewContainer.
     */
    public ElevatorViewContainer() {
        this.elevatorViews = new ArrayList<>();
        // TODO: initialize ElevatorViews, add to elevatorListPanel

        elevatorListPanel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(elevatorListPanel);
        scrollPane.setBorder(BorderFactory.createEtchedBorder());
        containerPanel.add(scrollPane);
    }

    /**
     * Gets the list of ElevatorViews in the ElevatorViewContainer.
     *
     * @return the ElevatorViews contained in the ElevatorViewContainer's elevatorListPanel
     */
    public ArrayList<ElevatorView> getElevatorViews() {
        return elevatorViews;
    }

    /**
     * Returns an ElevatorView corresponding to an Elevator's Number.
     *
     * @param elevatorNumber the elevatorNumber of the Elevator
     * @return elevatorView corresponding to the ElevatorNumber.
     */
    public ElevatorView getElevatorView(int elevatorNumber) {
        if (elevatorNumber >= elevatorViews.size()) {
            String messageToPrint = "The number " + elevatorNumber + " is greater than the actual number of ElevatorViews, " + elevatorViews.size() + ".";
            throw new IllegalArgumentException(messageToPrint);
        }
        return elevatorViews.get(elevatorNumber);
    }
}
