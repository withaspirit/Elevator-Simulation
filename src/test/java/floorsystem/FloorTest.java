package floorsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.ApproachEvent;
import requests.ServiceRequest;
import systemwide.Direction;
import systemwide.Origin;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * FloorTest verifies the methods of the Floor class. Namely, the
 * modification of an ApproachEvent's properties.
 *
 * @author Liam Tripp
 */
public class FloorTest {

    private Floor floor;
    private ApproachEvent approachEvent;

    @BeforeEach
    void setUp() {
        FloorSubsystem floorSubsystem = new FloorSubsystem();
        floor = new Floor(1, floorSubsystem);
    }

    @Test
    void testAddRequestToSensor() {
        // Create Service Request
        // Scheduler created the request -> Sent request to FloorSystem -> FloorSubsystem sent it to the floor
        ServiceRequest serviceRequest = new ServiceRequest(LocalTime.now(), 1, Direction.UP, Origin.SCHEDULER);

        // Test that the floor initially has no requests
        System.out.println("Requests on floor " + floor.getFloorNumber() + ": " + floor.getNumberOfRequests());
        assertEquals(0, floor.getNumberOfRequests());

        // Add ServiceRequest
        floor.addRequestToSensor(serviceRequest);

        // Test for the floor to now have 1 request
        System.out.println("Requests on floor " + floor.getFloorNumber() + ": " + floor.getNumberOfRequests());
        assertEquals(1, floor.getNumberOfRequests());
    }

    @Test
    void testIsElevatorExpected() {
        // Create ServiceRequest and assign elevator 1 to it
        ServiceRequest serviceRequest = new ServiceRequest(LocalTime.now(), 1, Direction.UP, Origin.SCHEDULER);
        serviceRequest.setElevatorNumber(1);

        // Check that the floor isn't initially expecting elevator 1 traveling UP to it
        assertFalse(floor.isElevatorExpected(1, Direction.UP));

        // Add request to floor
        floor.addRequestToSensor(serviceRequest);

        // Check that the floor is now expecting elevator 1
        assertTrue(floor.isElevatorExpected(1, Direction.UP));

        // Check that the floor is not expecting elevator 2 traveling UP to it
        assertFalse(floor.isElevatorExpected(2, Direction.UP));

        // Remove request from floor (once request is completed)
        floor.removeRequestFromSensor(serviceRequest);

        // Check that the floor is no longer expecting elevator 1 traveling UP to it
        assertFalse(floor.isElevatorExpected(1, Direction.UP));
    }
 }
