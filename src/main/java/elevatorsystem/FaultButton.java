package elevatorsystem;

import java.awt.Color;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class FaultButton implements ActionListener {

	//For each elevator
	private JPanel faultPanel;
	private JToggleButton doorFaultButton;
	private int elevatorNumber;
	
	public FaultButton(int elevatorNumber) {
		elevatorNumber = elevatorNumber;
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
    	System.out.println("Button was pressed");
    }
}
