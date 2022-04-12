package elevatorsystem;

import java.awt.Color;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

/**
 *  FaultButton is the button class used to inject door stuck faults.
 * 
 * @author Julian
 * */
public class FaultButton implements ActionListener {

	//For each elevator
	private JPanel faultPanel;
	private JToggleButton doorFaultButton;
	private Elevator elevator;
	private String name;
	private boolean malfunction;
	
	public FaultButton(Elevator elevator, String name) {
		this.name = name;
		this.elevator = elevator;
		this.malfunction = false;
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
     * Returns the JPanel of ElevatorView.
     *
     * @return the ElevatorView's JPanel
     */
    public JPanel getPanel() {
        return faultPanel;
    }
	
    //Overriding actionPerformed() method
    @Override
    public void actionPerformed(ActionEvent e) {
    	malfunction = !malfunction;
    	if(name == Fault.DOOR_STUCK.getName()) {
    		elevator.setDoorsMalfunctioning(malfunction);
    	} else if (name == Fault.ELEVATOR_STUCK.getName()) {
    		elevator.setCartMalfunctioning(malfunction);
    	}
    	
    }
}
