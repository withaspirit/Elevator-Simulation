package elevatorsystem;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * FaultButton is the button class used to inject door stuck faults.
 *
 * @author Julian
 */
public class FaultButton implements ActionListener {

    //For each elevator
    private JPanel faultPanel;
    private Elevator elevator;
    private JToggleButton doorFaultButton;

    /**
     * FaultButton constructor with elevator reference
     *
     */
    public FaultButton(Elevator elevator) {
        this.elevator = elevator;
        int elevatorNumber = elevator.getElevatorNumber();
        faultPanel = new JPanel();
        doorFaultButton = new JToggleButton("Door Fault");

        faultPanel.add(doorFaultButton);
        doorFaultButton.addActionListener(this);

        Border border = BorderFactory.createLineBorder(Color.BLACK);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(border, "Elevator " + elevatorNumber);
        titledBorder.setTitleJustification(TitledBorder.CENTER);
        titledBorder.setTitlePosition(TitledBorder.ABOVE_TOP);
        faultPanel.setBorder(titledBorder);
    }

    /**
     * Returns the JPanel of FaultButton.
     *
     * @return the FaultButton's JPanel
     */
    public JPanel getPanel() {
        return faultPanel;
    }

    //Overriding actionPerformed() method
    @Override
    public void actionPerformed(ActionEvent e) {
        boolean malfunction = !elevator.doorsAreMalfunctioning();
        if (malfunction) {
            elevator.setDoorsMalfunctioning(true);
        }
        doorFaultButton.setSelected(false);
    }
}
