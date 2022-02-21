package elevatorsystem;

import misc.InputFileReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.ServiceRequest;
import requests.SystemEvent;
import systemwide.BoundedBuffer;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests an Elevator's ServiceAlgorithm without transferring messages.
 * Uses inputs from a file.
 *
 * @author Liam Tripp, Brady Norton
 */
class ElevatorTest {

    private BoundedBuffer boundedBuffer;
    private ElevatorSubsystem elevatorSubsystem;
    private Elevator elevator;

    @BeforeEach
    void setUp() {
        boundedBuffer = new BoundedBuffer();
        elevatorSubsystem = new ElevatorSubsystem(boundedBuffer);
        elevator = new Elevator(1, elevatorSubsystem);
        elevatorSubsystem.addElevator(elevator);

        // setup requests to be read inputs
        InputFileReader inputFileReader = new InputFileReader();
        ArrayList<SystemEvent> eventList = inputFileReader.readInputFile(InputFileReader.INPUTS_FILENAME);
        for (SystemEvent event : eventList) {
            ServiceRequest serviceRequest = (ServiceRequest) event;
            elevator.addRequest(serviceRequest);
        }
        // disallowed message passing
        elevator.toggleMessageTransfer();
    }

    @Test
    void testElevatorGoesToCompletion() {
        while (elevator.getNumberOfRequests() != 0) {
            ServiceRequest serviceRequest = elevator.getNextRequest();
            elevator.processRequest(serviceRequest);
        }
        assertEquals(0, elevator.getNumberOfRequests());
    }

    @Test
    void stop() {
    }

    @Test
    void moveUp() {
    }

    @Test
    void moveDown() {
    }

    @Test
    void run() {
    }
}