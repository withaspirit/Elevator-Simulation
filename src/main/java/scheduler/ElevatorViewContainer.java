package scheduler;

import requests.ElevatorMonitor;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * ElevatorViewContainer displays a list of ElevatorViews and updates them when accessed.
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

        elevatorListPanel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(elevatorListPanel);
        scrollPane.setBorder(BorderFactory.createEtchedBorder());
        containerPanel.add(scrollPane);
    }

    public void receiveElevatorMonitor(ElevatorMonitor elevatorMonitor) {
        //elevatorViews.get(elevatorMonitor.getElevatorNumber() - 1).update(elevatorMonitor);
    }
}
