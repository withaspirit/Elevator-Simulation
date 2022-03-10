package floorsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.ApproachEvent;
import systemwide.BoundedBuffer;
import systemwide.Direction;
import systemwide.Origin;

import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
        BoundedBuffer buffer = new BoundedBuffer();
        floorSubsystem = new FloorSubsystem(buffer);

        int numberOfFloors = 10;
        for (int i = 1; i <= numberOfFloors; i++) {
            Floor floor = new Floor(i, floorSubsystem);
            floorSubsystem.addFloor(floor);
        }
    }

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
}
