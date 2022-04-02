package scheduler;

import javax.swing.*;
import java.awt.*;

public class SimulationFrame {

    private ElevatorViewContainer elevatorViewContainer;
    private ViewUpdater viewUpdater;
    private JFrame frame;


    public SimulationFrame() {
        elevatorViewContainer = new ElevatorViewContainer(2);
        viewUpdater = new ViewUpdater();
    }

    public void frameSetup() {
        frame = new JFrame("Elevator Simulation");
        int height = Toolkit.getDefaultToolkit().getScreenSize().height - Toolkit.getDefaultToolkit().getScreenInsets(new JDialog().getGraphicsConfiguration()).bottom;
        int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        frame.setSize(width, height);
        frame.add(elevatorViewContainer.getPanel());

        frame.setVisible(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    public static void main(String[] args) {
        SimulationFrame simulationFrame = new SimulationFrame();
        simulationFrame.frameSetup();
    }
}
