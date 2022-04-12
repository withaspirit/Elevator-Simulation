package elevatorsystem;

import javax.swing.*;
import java.util.ArrayList;

/**
 * FaultInjectorView is the GUI that helps to inject Door Stuck faults using a push button
 *
 * @author Julian
 */
public class FaultInjectorView {

    //For elevator subsystem
    private ArrayList<FaultButton> faultButtons;
    private JPanel buttonListPanel;
    private JPanel containerPanel;

    public FaultInjectorView(ArrayList<Elevator> elevatorList) {
        int numberOfElevators = elevatorList.size();
        //Initializing the buttons
        faultButtons = new ArrayList<>();
        buttonListPanel = new JPanel();
        for (int i = 0; i < numberOfElevators; i++) {
            faultButtons.add(new FaultButton(elevatorList.get(i)));
            buttonListPanel.add(faultButtons.get(i).getPanel());
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
