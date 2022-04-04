package scheduler;

import elevatorsystem.MovementState;
import requests.ElevatorMonitor;
import systemwide.Direction;

import javax.swing.*;
import java.awt.*;

public class SimulationFrame {

    private ElevatorViewContainer elevatorViewContainer;
    private Presenter presenter;
    private JFrame frame;


    public SimulationFrame() {
        elevatorViewContainer = new ElevatorViewContainer(20);
        presenter = new Presenter();
        presenter.addView(elevatorViewContainer);
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

    // TODO: this method should be moved to the model
    public void testElevatorMonitorUpdate(ElevatorMonitor elevatorMonitor) {
        presenter.updateElevatorView(elevatorMonitor);
    }

    public static void main(String[] args) {
        SimulationFrame simulationFrame = new SimulationFrame();
        simulationFrame.frameSetup();
        ElevatorMonitor elevatorMonitor = new ElevatorMonitor(0);
        elevatorMonitor.updateMonitor(new ElevatorMonitor(0, MovementState.ACTIVE, 2, Direction.DOWN, 0, true));
        simulationFrame.testElevatorMonitorUpdate(elevatorMonitor);
    }
}
