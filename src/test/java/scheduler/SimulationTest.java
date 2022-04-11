package scheduler;

import client_server_host.Port;
import elevatorsystem.Elevator;
import elevatorsystem.ElevatorSubsystem;
import floorsystem.FloorSubsystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import systemwide.Structure;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SimulationTest ensures that the entire simulation runs to completion.
 *
 * @author Liam Tripp
 */
public class SimulationTest {

    private Scheduler schedulerFloorsToElevators;
    private Scheduler schedulerElevatorsToFloors;
    private ElevatorSubsystem elevatorSubsystem;
    private FloorSubsystem floorSubsystem;
    private Structure structure;
    private int elevatorTime;
    private int doorsTime;

    @BeforeEach
    void setup() {
        elevatorTime = 100;
        doorsTime = 100;
        structure = new Structure(20, 4, elevatorTime, doorsTime);
        schedulerFloorsToElevators = new Scheduler(Port.CLIENT_TO_SERVER.getNumber());
        schedulerElevatorsToFloors = new Scheduler(Port.SERVER_TO_CLIENT.getNumber());

        for (int i = 0; i < structure.getNumberOfElevators(); i++) {
            schedulerFloorsToElevators.addElevatorMonitor(i + 1);
        }

        // initialize elevators
        elevatorSubsystem = new ElevatorSubsystem();
        try {
            schedulerFloorsToElevators.enableSystem(structure, InetAddress.getLocalHost(), Port.SERVER.getNumber());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        elevatorSubsystem.initializeElevators(elevatorSubsystem.receiveStructure());
        Thread elevatorSubsystemThread = new Thread(elevatorSubsystem, elevatorSubsystem.getClass().getSimpleName());
        elevatorSubsystemThread.start();
        System.out.println("ElevatorSubsystem initialized");
        elevatorSubsystem.initializeElevatorThreads();

        // initialize floor subsystem
        floorSubsystem = new FloorSubsystem();
        try {
            schedulerElevatorsToFloors.enableSystem(structure, InetAddress.getLocalHost(), Port.CLIENT.getNumber());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        floorSubsystem.initializeFloors(floorSubsystem.receiveStructure().getNumberOfFloors());
        System.out.println("Floors initialized");
        Thread floorSubsystemThread = new Thread(floorSubsystem, floorSubsystem.getClass().getSimpleName());
        floorSubsystemThread.start();

        // initialize Scheduler and entire system
        new Thread(schedulerFloorsToElevators, "Scheduler: FloorToElevator").start();
        new Thread(schedulerElevatorsToFloors, "Scheduler: ElevatorToFloor").start();
    }

    /**
     * Tests that all requests are served.
     */
    @Test
    void testSimulationRunsToCompletion() {
        while (schedulerElevatorsToFloors.getSystemStatus().activated() ||
                schedulerFloorsToElevators.getSystemStatus().activated() ||
                floorSubsystem.getSystemStatus().activated() ||
                elevatorSubsystem.getSystemStatus().activated()) {
        }

        for (Elevator elevator : elevatorSubsystem.getElevatorList()) {
            assertTrue(elevator.hasNoRequests());
        }
        assertEquals(0, floorSubsystem.getEventListSize());
        assertTrue(schedulerElevatorsToFloors.getIntermediateHost().queueIsEmpty());
        assertTrue(schedulerFloorsToElevators.getIntermediateHost().queueIsEmpty());
    }
}
