package requests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import systemwide.Direction;
import systemwide.Origin;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * TreeSetTest demonstrates the utility of TreeSet with Comparators and
 * Comparable classes.
 *
 * Resource used: https://howtodoinjava.com/java/collections/java-priorityqueue/
 *
 * @author Liam Tripp
 */
public class TreeSetTest {

    private ArrayList<ServiceRequest> requests;
    private TreeSet<ServiceRequest> forwardQueue;
    private TreeSet<ServiceRequest> backwardsQueue;
    private final int NUM_REQUESTS = 9;

    @BeforeEach
    void setUp() {
        requests = new ArrayList<>();
        for (int i = 1; i <= NUM_REQUESTS; i++) {
            requests.add(new ServiceRequest(LocalTime.now(), i,
                    Direction.DOWN, Origin.ELEVATOR_SYSTEM));
        }
        // randomize order of requests
        Collections.shuffle(requests);
        forwardQueue = new TreeSet<>(requests);
        backwardsQueue = new TreeSet<>(Collections.reverseOrder());
        backwardsQueue.addAll(requests);
    }

    /**
     * Add ElevatorRequests to requests. Demonstrates Comparable
     * compatibility with inheritance.
     */
    void addElevatorRequests() {
        for (int i = 10; i <= NUM_REQUESTS + 10; i++) {
            if (i % 2 == 0) {
                requests.add(new ElevatorRequest(LocalTime.now(), i, Direction.UP, i + 1, Origin.ELEVATOR_SYSTEM));
            }
        }
    }

    @Test
    void testForwardQueueIsInOrder() {
        for (int i = 1; i <= forwardQueue.size(); i++) {
            assertEquals(i, forwardQueue.pollFirst().getFloorNumber());
        }
    }

    @Test
    void testBackwardQueueIsInOrder() {
        for (int i = backwardsQueue.size() - 1; i >= 0; i--) {
            assertEquals(i + 1, backwardsQueue.pollFirst().getFloorNumber());
        }
    }

    @Test
    void testTreeSetWithElevatorRequests() {
        addElevatorRequests();
        testBackwardQueueIsInOrder();
        testForwardQueueIsInOrder();
    }
}
