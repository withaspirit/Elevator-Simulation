package floorsystem;

import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.ApproachEvent;
import requests.ServiceRequest;
import systemwide.Direction;
import systemwide.Origin;

import java.net.SocketException;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * FloorSubsystemTest verifies that FloorSubsystem selects the correct floor
 * in the FloorSubsystem's list of floors.
 *
 * @author Liam Tripp
 */
public class FloorSubsystemTest {

    private FloorSubsystem floorSubsystem;

    @BeforeEach
    void setUp() {
        floorSubsystem = new FloorSubsystem();
        int numberOfFloors = 10;
        for (int i = 1; i <= numberOfFloors; i++) {
            Floor floor = new Floor(i, floorSubsystem);
            floorSubsystem.addFloor(floor);
        }
    }

    /**
     * Being seperated into 2 tests (testApproachingDestination & testNotApproachingDestination)
     *
    @Test
    void testCorrectFloorSelectedByGetListMethod() {
        // Test that the floor specified by an ApproachRequest is selected
        int floorNumber = 1;
        int elevatorNumber = 1;
        ApproachEvent approachEvent = new ApproachEvent(LocalTime.now(), floorNumber,
                Direction.UP, elevatorNumber, Origin.SCHEDULER);

        floorSubsystem.processApproachEvent(approachEvent);
        assertTrue(approachEvent.elevatorMayStop());
    }
     **/

    @Test
    void testGetSpecificFloor() {
        // Create a copy of floor 2
        Floor actualFloor = floorSubsystem.getSpecificFloor(2);

        // Test that the correct floor was selected from the list of floors
        assertEquals(2, actualFloor.getFloorNumber());
    }

    @Test
    void testAddServiceRequestToFloor() {
        // Create a ServiceRequest
        ServiceRequest serviceRequest = new ServiceRequest(LocalTime.now(), 2, Direction.UP, Origin.SCHEDULER);

        // Add ServiceRequest to appropriate floor
        floorSubsystem.addServiceRequestToFloor(serviceRequest);

        // Check that the request was added to the floors list of requests
        assertEquals(1, floorSubsystem.getSpecificFloor(2).getNumberOfRequests());
    }

    @Test
    void testProcessApproachEvent() {
        // Test that event list has a size of 7 (from input file) before processing ApproachEvent
        assertEquals(7, floorSubsystem.getEventListSize());

        // Create and process new ApproachEvent (elevator 1 is approaching floor 2)
        ApproachEvent approachEvent = new ApproachEvent(LocalTime.now(), 2, Direction.UP, 1, Origin.SCHEDULER);
        floorSubsystem.processApproachEvent(approachEvent);

        // Test that the correct ApproachEvent was added
        assertEquals(approachEvent, floorSubsystem.getNextEventListApproachEvent());

        // Test that the ApproachEvent was processed and added to the eventList
        assertEquals(8, floorSubsystem.getEventListSize());
    }

    @Test
    void testApproachingDestination() {
        // Test that the ApproachEvent tells elevator to stop at the specified floor
        // Example: Elevator 1 approaching floor 2 from Floor 1
        // This assumes that Elevator 1 is assigned to the ServiceRequest added to Floor 2's ArrivalSensor requestsOnFloor list

        // Create ServiceRequest and assign elevator 1 to it
        ServiceRequest serviceRequest = new ServiceRequest(LocalTime.now(), 2, Direction.UP, Origin.SCHEDULER);
        serviceRequest.setElevatorNumber(1);

        // Create ApproachEvent
        ApproachEvent approachEvent = new ApproachEvent(LocalTime.now(), 2, Direction.UP, 1, Origin.SCHEDULER);

        // Test that the floor initially has no requests
        System.out.println("Floor " + floorSubsystem.getSpecificFloor(2).getFloorNumber() + "'s Requests: " + floorSubsystem.getSpecificFloor(2).getNumberOfRequests());
        assertEquals(0, floorSubsystem.getSpecificFloor(2).getNumberOfRequests());

        // Add ServiceRequest to Floor
        floorSubsystem.addServiceRequestToFloor(serviceRequest);

        // Test that ServiceRequest is properly added to floor
        System.out.println("Floor " + floorSubsystem.getSpecificFloor(2).getFloorNumber() + "'s Requests: " + floorSubsystem.getSpecificFloor(2).getNumberOfRequests());
        assertEquals(1, floorSubsystem.getSpecificFloor(2).getNumberOfRequests());

        // Check that elevator 1 was assigned
        assertTrue(floorSubsystem.getSpecificFloor(2).isElevatorExpected(1, Direction.UP));

        // Process ApproachEvent
        // Send ApproachEvent to the Floor, Floor sends ApproachEvent to its ArrivalSensor to compare with its requestsOnFloor list
        floorSubsystem.processApproachEvent(approachEvent);
        System.out.println("Approach Event Should Stop: " + approachEvent.elevatorMayStop());

        // Check that the ApproachEvent to be sent was changed to indicate that the elevator is approaching the destination floor
        // This is the ApproachEvent that's about to be sent back to the elevator from eventList
        assertTrue(floorSubsystem.getNextEventListApproachEvent().elevatorMayStop());

        // This tests whether the created ApproachEvent was actually modified
        // Somewhat redundant as it's just another way to show the previous assert
        assertTrue(approachEvent.elevatorMayStop());
    }

    @Test
    void testNotApproachingDestination() {
        // Test that the ApproachEvent does not tell elevator to stop at the specified floor
        // Example: Elevator 1 approaching Floor 2 and when it's assigned to a ServiceRequest on floor 3
        //

        // Create ServiceRequest and assign elevator 1 to it
        ServiceRequest serviceRequest = new ServiceRequest(LocalTime.now(), 3, Direction.UP, Origin.SCHEDULER);
        serviceRequest.setElevatorNumber(1);

        // Add ServiceRequest to Floor 3
        floorSubsystem.addServiceRequestToFloor(serviceRequest);

        // Create ApproachEvent
        ApproachEvent approachEvent = new ApproachEvent(LocalTime.now(), 2, Direction.UP, 1, Origin.SCHEDULER);

        // Process ApproachEvent
        floorSubsystem.processApproachEvent(approachEvent);

        // Check if ApproachEvent adjusted properly
        assertFalse(approachEvent.elevatorMayStop());
    }
}
