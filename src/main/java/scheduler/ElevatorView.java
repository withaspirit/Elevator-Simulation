package scheduler;

import requests.ElevatorMonitor;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * ElevatorView presents the status information of an Elevator.
 *
 * @author Liam Tripp
 */
public class ElevatorView {

    private JPanel elevatorPanel;
    private JPanel statusPanelContainer;
    private JPanel[] statusPanels;
    private JTextPane[] statusPanes;
    private final static int NUMBER_OF_STATUS_PANES = 6;

    /**
     * Constructor for ElevatorView.
     *
     * @param elevatorNumber the number of the Elevator that this View corresponds to
     */
    public ElevatorView(int elevatorNumber) {
        elevatorPanel = new JPanel();
        this.statusPanelContainer = new JPanel(new GridLayout(1, NUMBER_OF_STATUS_PANES));
        elevatorPanel.add(statusPanelContainer);
        this.statusPanels = new JPanel[NUMBER_OF_STATUS_PANES];
        this.statusPanes = new JTextPane[NUMBER_OF_STATUS_PANES];

        // TODO: elevatorPanel contains the statusPanelContainer, also has the border and title
        //    (distinguishing from statusPanelContainer allows more features to be added later)
        //  StatusPanelContainer contains the statusPanels
        //  StatusPanels contain a JTextPane and an information JLabel
        //  We want direct access to each JTextPane to let us update them directly

        for (int i = 0; i < NUMBER_OF_STATUS_PANES; i++) {
            statusPanes[i] = new JTextPane();
            statusPanes[i].setEditable(false);
            statusPanels[i] = new JPanel();
            statusPanelContainer.add(statusPanels[i]);

            String labelText = "";
            // these values are hard coded; not very good practice
            if (i == 0) {
                labelText = "Current Floor:";
            } else if (i == 1) {
                labelText = "Service Direction:";
            } else if (i == 2) {
                labelText = "Movement State:";
            } else if (i == 3) {
                labelText = "Movement Direction:";
            } else if (i == 4) {
                labelText = "Door State:";
            } else if (i == 5) {
                labelText = "Fault:";
            }
            // TODO: add currentRequest?
            JLabel label = new JLabel();
            label.setText(labelText);
            statusPanels[i].add(label);

            statusPanels[i].add(statusPanes[i]);
        }
        // FIXME: this is awkward; should we pass elevator number or ElevatorMonitor
        //  to ElevatorView's constructor?
        update(new ElevatorMonitor(elevatorNumber));

        // GUI stuff
        // https://docs.oracle.com/javase/tutorial/uiswing/components/border.html
        // give elevatorPanel a title reading "Elevator X" above in center
        Border border = BorderFactory.createLineBorder(Color.BLACK);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(border, "Elevator " + elevatorNumber);
        titledBorder.setTitleJustification(TitledBorder.CENTER);
        titledBorder.setTitlePosition(TitledBorder.ABOVE_TOP);
        elevatorPanel.setBorder(titledBorder);
    }

    /**
     * Returns the JPanel of ElevatorView.
     *
     * @return the ElevatorView's JPanel
     */
    public JPanel getPanel() {
        return elevatorPanel;
    }

    /**
     * Updates the ElevatorView with information from an ElevatorMonitor.
     *
     * @param elevatorMonitor contains the status information of an Elevator
     */
    public void update(ElevatorMonitor elevatorMonitor) {

        // TODO: ElevatorMonitor: add MovementDirection, DoorState, and Fault
        for (int i = 0; i < NUMBER_OF_STATUS_PANES; i++) {
            String statusPaneText = "";
            // these values are hard coded; not very good practice
            if (i == 0) {
                statusPaneText = Integer.toString(elevatorMonitor.getCurrentFloor());
            } else if (i == 1) {
                statusPaneText = elevatorMonitor.getDirection().getName();
            } else if (i == 2) {
                statusPaneText = elevatorMonitor.getState().getName();
            } else if (i == 3) {
                statusPaneText = "Up";
            } else if (i == 4) {
                statusPaneText = "Open";
            } else if (i == 5) {
                statusPaneText = "None";
            }
            statusPanes[i].setText(statusPaneText);
        }
        elevatorPanel.repaint();
        elevatorPanel.revalidate();
    }
}
