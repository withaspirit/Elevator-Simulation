package floorsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.ApproachEvent;
import systemwide.Direction;
import systemwide.Origin;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void testApproachEventNotChanged() {
        int floorNumber = 2;
        approachEvent = new ApproachEvent(LocalTime.now(), floorNumber, Direction.UP, 1, Origin.FLOOR_SYSTEM);
        floor.receiveApproachEvent(approachEvent);
        assertFalse(approachEvent.elevatorMayStop());
    }

    @Test
    void testApproachEventChanged() {
        int floorNumber = 1;
        approachEvent = new ApproachEvent(LocalTime.now(), floorNumber, Direction.UP, 1, Origin.FLOOR_SYSTEM);
        floor.receiveApproachEvent(approachEvent);
        assertTrue(approachEvent.elevatorMayStop());
    }
 }
