package elevatorsystem;


import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.ArrayList;
import javax.swing.*;

public class FaultInjector {

	//For elevator subsystem
    private ArrayList<FaultButton> faultButtons;
    private JPanel buttonListPanel;
    private JScrollPane scrollPane;
    private JPanel containerPanel;
    private ArrayList<Elevator> elevatorList;
	
	public FaultInjector(ArrayList<Elevator> elevatorList) {
        int numberOfElevators = elevatorList.size();
		
		this.faultButtons = new ArrayList<>();
        buttonListPanel = new JPanel(new GridLayout(numberOfElevators / 3, 3));
        for (int i = 0; i < numberOfElevators; i++) {
            faultButtons.add(new FaultButton(elevatorList.get(i)));
            buttonListPanel.add(faultButtons.get(i).getPanel());
        }

        JScrollPane scrollPane = new JScrollPane(buttonListPanel);
        scrollPane.setBorder(BorderFactory.createEtchedBorder());
        containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
        containerPanel.add(scrollPane);
        
        JFrame frame = new JFrame("Elevator Simulation");
        int height = Toolkit.getDefaultToolkit().getScreenSize().height - Toolkit.getDefaultToolkit().getScreenInsets(new JDialog().getGraphicsConfiguration()).bottom;
        int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        frame.setSize(width, height);
        frame.add(containerPanel);

        frame.setVisible(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
	}

	public static void main(String[] args) {
		//FaultInjector faultInjector = new FaultInjector(20);
	}
	
}
