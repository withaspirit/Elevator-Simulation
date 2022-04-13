package elevatorsystem;

import javax.swing.*;
import java.util.ArrayList;

/**
 * FaultInjectorView is the GUI that helps to inject Door Stuck faults using a push button
 *
 * @author Julian
 */
public class FaultInjectorView {

    private ArrayList<FaultButton> doorFaultButtons;
    private ArrayList<FaultButton> cartFaultButtons;
    private JPanel buttonListPanel;
    private JPanel containerPanel;

    /**
     * Class constructor with reference to elevator list. 
     *
     */
    public FaultInjectorView(ArrayList<Elevator> elevatorList) {
        int numberOfElevators = elevatorList.size();
        //Initializing the buttons
        this.doorFaultButtons = new ArrayList<>();
        this.cartFaultButtons = new ArrayList<>();
        buttonListPanel = new JPanel();
        for (int i = 0; i < numberOfElevators; i++) {
            doorFaultButtons.add(new FaultButton(elevatorList.get(i), Fault.DOOR_STUCK.getName()));
            cartFaultButtons.add(new FaultButton(elevatorList.get(i), Fault.ELEVATOR_STUCK.getName()));
            buttonListPanel.add(doorFaultButtons.get(i).getPanel());
            buttonListPanel.add(cartFaultButtons.get(i).getPanel());
        }

        JScrollPane scrollPane = new JScrollPane(buttonListPanel);
        scrollPane.setBorder(BorderFactory.createEtchedBorder());
        containerPanel = new JPanel();
        containerPanel.add(scrollPane);

        JFrame frame = new JFrame("Elevator Simulation");
        frame.add(containerPanel);
        frame.pack();
        frame.setVisible(true);
    }
}
