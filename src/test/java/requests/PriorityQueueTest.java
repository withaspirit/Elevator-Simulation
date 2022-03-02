package requests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import systemwide.Direction;
import systemwide.Origin;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * PriorityQueueTest demonstrates the utility of PriorityQueue with Comparators
 * and Comparable classes. PriorityBlockingQueue is used due to concurrency issues.
 * Note that the toString() method for PriorityQueue doesn't print the elements
 * as they appear in the list.
 *
 * Resources used:
 * https://howtodoinjava.com/java/collections/java-priorityqueue/
 * https://stackoverflow.com/a/10078729
 *
 * @author Liam Tripp
 */
public class PriorityQueueTest {

    private ArrayList<ServiceRequest> requests;
    private PriorityBlockingQueue<ServiceRequest> forwardQueue;
    private PriorityBlockingQueue<ServiceRequest> backwardsQueue;
    private final int NUM_REQUESTS = 9;

    @BeforeEach
    void setUp() {
        requests = new ArrayList<>();
        for (int i = 1; i <= NUM_REQUESTS; i++) {
            requests.add(new ServiceRequest(LocalTime.now(), i, Direction.DOWN, Origin.ELEVATOR_SYSTEM));
        }
        // randomize order of requests
        Collections.shuffle(requests);

        // source:
        Comparator<ServiceRequest> reverseRequestSorter = (sr1, sr2) -> -sr1.compareTo(sr2);

        forwardQueue = new PriorityBlockingQueue<>(requests);
        backwardsQueue = new PriorityBlockingQueue<>(NUM_REQUESTS, reverseRequestSorter);
        backwardsQueue.addAll(requests);
    }

    /**
     * Add ElevatorRequests to requests. Demonstrates compatibility with Inheritance.
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
            assertEquals(i, forwardQueue.remove().getFloorNumber());
        }
    }

    @Test
    void testBackwardQueueIsInOrder() {
        for (int i = backwardsQueue.size() - 1; i >= 0; i--) {
            assertEquals(i + 1, backwardsQueue.remove().getFloorNumber());
        }
    }

    @Test
    void testWithElevatorRequests() {
        addElevatorRequests();
        testBackwardQueueIsInOrder();
        testForwardQueueIsInOrder();
    }
}
