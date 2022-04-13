package elevatorsystem;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * FaultButton is the button class used to inject faults.
 *
 * @author Julian
 */
public class FaultButton implements ActionListener {

    private JPanel faultPanel;
    private JToggleButton doorFaultButton;
    private Elevator elevator;
    private String name;

    /**
     * FaultButton constructor with elevator reference
     *
     * @param
     * @param
     */
    public FaultButton(Elevator elevator, String name) {
        this.name = name;
        this.elevator = elevator;
        int elevatorNumber = elevator.getElevatorNumber();
        faultPanel = new JPanel();
        doorFaultButton = new JToggleButton(name);

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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (name.equals(Fault.DOOR_STUCK.getName())) {
            boolean doorIsMalfunctioning = !elevator.doorsAreMalfunctioning();
            if (doorIsMalfunctioning) {
                elevator.setDoorsMalfunctioning(true);
            }
        } else if (name.equals(Fault.ELEVATOR_STUCK.getName())) {
            boolean cartIsMalfunctioning = !elevator.cartIsMalfunctioning();
            if (cartIsMalfunctioning) {
                elevator.setCartMalfunctioning(true);
            }
        }
		doorFaultButton.setSelected(false);
	}
}
