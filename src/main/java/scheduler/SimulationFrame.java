package scheduler;

import elevatorsystem.MovementState;
import requests.ElevatorMonitor;
import systemwide.Direction;

import javax.swing.*;
import java.awt.*;

public class SimulationFrame {

    private ElevatorViewContainer elevatorViewContainer;
    private ViewUpdater viewUpdater;
    private JFrame frame;


    public SimulationFrame() {
        elevatorViewContainer = new ElevatorViewContainer(20);
        viewUpdater = new ViewUpdater();
        viewUpdater.addView(elevatorViewContainer);
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

    public void testElevatorMonitorUpdate(ElevatorMonitor elevatorMonitor) {
        viewUpdater.updateElevatorView(elevatorMonitor);
    }

    public static void main(String[] args) {
        SimulationFrame simulationFrame = new SimulationFrame();
        simulationFrame.frameSetup();
        ElevatorMonitor elevatorMonitor = new ElevatorMonitor(0);
        elevatorMonitor.updateMonitor(new ElevatorMonitor(0, MovementState.IDLE, 1, Direction.UP, 0, true));
        simulationFrame.testElevatorMonitorUpdate(elevatorMonitor);
    }
}
