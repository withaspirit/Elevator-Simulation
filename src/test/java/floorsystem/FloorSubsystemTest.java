package floorsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.ApproachEvent;
import requests.ServiceRequest;
import systemwide.Direction;
import systemwide.Origin;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
     * Broken Test
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
        // Example: Elevator 1 approaching floor 2 and floor 2 is expecting elevator 1

        // Create ServiceRequest
        ServiceRequest serviceRequest = new ServiceRequest(LocalTime.now(), 2, Direction.UP, Origin.SCHEDULER);

        // Create ApproachEvent
        ApproachEvent approachEvent = new ApproachEvent(LocalTime.now(), 2, Direction.UP, 1, Origin.SCHEDULER);


    }

    @Test
    void testNotApproachingDestination() {
        // Test that the ApproachEvent does not tell elevator to stop at the specified floor
    }
}
