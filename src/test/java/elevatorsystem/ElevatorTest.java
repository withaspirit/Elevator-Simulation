package elevatorsystem;

import misc.InputFileReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.ElevatorRequest;
import requests.ServiceRequest;
import requests.SystemEvent;
import systemwide.BoundedBuffer;
import systemwide.Origin;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests an Elevator's ServiceAlgorithm without transferring messages.
 * Uses inputs from a file.
 *
 * @author Liam Tripp, Brady Norton, Ryan Dash
 */
class ElevatorTest {

    private BoundedBuffer boundedBuffer;
    private ElevatorSubsystem elevatorSubsystem;
    private Elevator elevator;

    @BeforeEach
    void setUp() {
        boundedBuffer = new BoundedBuffer();
        elevatorSubsystem = new ElevatorSubsystem(boundedBuffer, 1);

        // setup requests to be read inputs
        InputFileReader inputFileReader = new InputFileReader();
        ArrayList<SystemEvent> eventList = inputFileReader.readInputFile(InputFileReader.INPUTS_FILENAME);
        for (SystemEvent event : eventList) {
            ElevatorRequest elevatorRequest = (ElevatorRequest) event;
            // this set origin is defensive in case origin ends up affecting elevator request
            elevatorRequest.setOrigin(Origin.ELEVATOR_SYSTEM);
            elevatorSubsystem.addRequest(elevatorRequest);
        }
        // disallowed message passing
        elevator.toggleMessageTransfer();
    }

//    @Test
//    void testElevatorGoesToCompletion() {
//        while (elevatorSubsystem.getNumberOfRequests() != 0) {
//            ServiceRequest serviceRequest = (ServiceRequest) elevatorSubsystem.getNextRequest();
//            System.out.println("Request: " + serviceRequest);
//            elevator.processRequest(serviceRequest);
//        }
//        assertEquals(0, elevator.getNumberOfRequests());
//    }

//    @Test
//    void stop() {
//
//    }
//
//    @Test
//    void moveUp() {
//    }
//
//    @Test
//    void moveDown() {
//    }
//
//    @Test
//    void run() {
//    }
}