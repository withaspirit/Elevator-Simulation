package elevatorsystem;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.ElevatorRequest;
import systemwide.Direction;
import systemwide.Origin;

import java.time.LocalTime;

/**
 * FloorsQueueTest ensures that all the FloorsQueue methods are working properly.
 * The main methods tested are adding Requests and swapping the active queue.
 *
 * @author Julian, Liam Tripp
 */
class FloorsQueueTest {

	FloorsQueue testQueue;

	@BeforeEach
	void setUp() {
		testQueue = new FloorsQueue();
	}

	@Test
	void testAddRequestSameFloorSameDirection() {
		Direction requestDirection = Direction.UP;
		Direction serviceDirection = Direction.UP;
		int elevatorFloor = 1;
		int floorNumber = 1;

		ElevatorRequest elevatorRequest = new ElevatorRequest(LocalTime.now(), floorNumber, requestDirection, 3, Origin.ELEVATOR_SYSTEM);
		testQueue.addRequest(elevatorFloor, serviceDirection, elevatorRequest);
		assertFalse(testQueue.isCurrentQueueEmpty());
	}

	@Test
	void testAddRequestSameFloorOppositeDirection() {
		Direction requestDirection = Direction.UP;
		Direction serviceDirection = Direction.DOWN;
		int elevatorFloor = 1;
		int floorNumber = 1;

		ElevatorRequest elevatorRequest = new ElevatorRequest(LocalTime.now(), floorNumber, requestDirection, 3, Origin.ELEVATOR_SYSTEM);
		testQueue.addRequest(elevatorFloor, serviceDirection, elevatorRequest);
		assertFalse(testQueue.isOppositeQueueEmpty());
	}


	// Request: Up
	// ServiceDirection: Up
	// Request Location: Above Elevator
	// should be added to current queue
	@Test
	void testAddUpRequestAboveUpElevator() {
		Direction requestDirection = Direction.UP;
		Direction serviceDirection = Direction.UP;
		int elevatorFloor = 1;
		int floorNumber = 2;

		ElevatorRequest elevatorRequest = new ElevatorRequest(LocalTime.now(), floorNumber, requestDirection, 3, Origin.ELEVATOR_SYSTEM);
		testQueue.addRequest(elevatorFloor, serviceDirection, elevatorRequest);
		assertFalse(testQueue.isCurrentQueueEmpty());
	}

	// Request: Down
	// ServiceDirection: Down
	// Request Location: Below Elevator
	// should be added to current queue
	@Test
	void testAddDownRequestBelowDownElevator() {
		Direction requestDirection = Direction.DOWN;
		Direction serviceDirection = Direction.DOWN;
		int elevatorFloor = 4;
		int floorNumber = 3;

		ElevatorRequest elevatorRequest = new ElevatorRequest(LocalTime.now(), floorNumber, requestDirection, 1, Origin.ELEVATOR_SYSTEM);
		testQueue.addRequest(elevatorFloor, serviceDirection, elevatorRequest);
		assertFalse(testQueue.isCurrentQueueEmpty());
	}

	// Request: Down
	// ServiceDirection: Up
	// Request Location: Below Elevator
	// should be added to opposite queue
	@Test
	void testAddDownRequestBelowUpElevator() {
		Direction requestDirection = Direction.DOWN;
		Direction serviceDirection = Direction.UP;
		int elevatorFloor = 4;
		int floorNumber = 3;

		ElevatorRequest elevatorRequest = new ElevatorRequest(LocalTime.now(), floorNumber, requestDirection, 1, Origin.ELEVATOR_SYSTEM);
		testQueue.addRequest(elevatorFloor, serviceDirection, elevatorRequest);
		assertFalse(testQueue.isOppositeQueueEmpty());
	}

	// Request: Up
	// ServiceDirection: Down
	// Request Location: Above Elevator
	// should be added to opposite queue
	@Test
	void testAddUpRequestAboveDownElevator() {
		Direction requestDirection = Direction.UP;
		Direction serviceDirection = Direction.DOWN;
		int elevatorFloor = 2;
		int floorNumber = 3;

		ElevatorRequest elevatorRequest = new ElevatorRequest(LocalTime.now(), floorNumber, requestDirection, 4, Origin.ELEVATOR_SYSTEM);
		testQueue.addRequest(elevatorFloor, serviceDirection, elevatorRequest);
		assertFalse(testQueue.isOppositeQueueEmpty());
	}

	// Request: Down
	// ServiceDirection: Up
	// Request Location: Above Elevator
	// should be added to opposite queue
	@Test
	void testAddDownRequestAboveUpElevator() {
		Direction requestDirection = Direction.DOWN;
		Direction serviceDirection = Direction.UP;
		int elevatorFloor = 1;
		int floorNumber = 2;

		ElevatorRequest elevatorRequest = new ElevatorRequest(LocalTime.now(), floorNumber , requestDirection, 1, Origin.ELEVATOR_SYSTEM);
		testQueue.addRequest(elevatorFloor, serviceDirection, elevatorRequest);
		assertFalse(testQueue.isOppositeQueueEmpty());
	}

	// Request: Up
	// ServiceDirection: Down
	// Request Location: Below Elevator
	// should be added to opposite queue
	@Test
	void testAddUpRequestBelowDownElevator() {
		Direction requestDirection = Direction.UP;
		Direction serviceDirection = Direction.DOWN;
		int elevatorFloor = 4;
		int floorNumber  = 1;

		ElevatorRequest elevatorRequest = new ElevatorRequest(LocalTime.now(), floorNumber, requestDirection, 3, Origin.ELEVATOR_SYSTEM);
		testQueue.addRequest(elevatorFloor, serviceDirection, elevatorRequest);
		assertFalse(testQueue.isOppositeQueueEmpty());
	}

	// Request: Down
	// ServiceDirection: Down
	// Request Location: Above Elevator
	// should be added to missed queue
	@Test
	void testAddDownRequestAboveDownElevator() {
		Direction requestDirection = Direction.DOWN;
		Direction serviceDirection = Direction.DOWN;
		int elevatorFloor = 1;
		int floorNumber  = 3;

		ElevatorRequest elevatorRequest = new ElevatorRequest(LocalTime.now(), floorNumber, requestDirection, 1, Origin.ELEVATOR_SYSTEM);
		testQueue.addRequest(elevatorFloor, serviceDirection, elevatorRequest);
		assertFalse(testQueue.isMissedqueueEmpty());
	}

	// Request: Up
	// ServiceDirection: Up
	// Request Location: Below Elevator
	// should be added to missed queue
	@Test
	void testAddUpRequestBelowUpElevator() {
		Direction requestDirection = Direction.UP;
		Direction serviceDirection = Direction.UP;
		int elevatorFloor = 2;
		int floorNumber  = 1;

		ElevatorRequest elevatorRequest = new ElevatorRequest(LocalTime.now(), floorNumber, requestDirection, 3, Origin.ELEVATOR_SYSTEM);
		testQueue.addRequest(elevatorFloor, serviceDirection, elevatorRequest);
		assertFalse(testQueue.isMissedqueueEmpty());
	}

	@Test
	void testSwapQueueWithCurrentQueueNotEmpty() {
		testAddDownRequestBelowDownElevator();
		assertFalse(testQueue.swapQueues());
		assertFalse(testQueue.isCurrentQueueEmpty());
		assertTrue(testQueue.isOppositeQueueEmpty());
		assertTrue(testQueue.isMissedqueueEmpty());
	}

	@Test
	void testSwapQueueWithOppositeQueueNotEmpty() {
		// currentQueue empty, opposite queue has items
		testAddUpRequestBelowDownElevator();

		assertFalse(testQueue.isOppositeQueueEmpty());
		assertTrue(testQueue.swapQueues());
		assertFalse(testQueue.isCurrentQueueEmpty());
		assertTrue(testQueue.isOppositeQueueEmpty());
	}

	@Test
	void testSwapQueueWithMissedQueueNotEmptyAndOppositeQueueEmpty() {
		// current queue empty, missed has items
		// don't swap queues ebcause
		testAddDownRequestAboveDownElevator();

		assertFalse(testQueue.swapQueues());
		assertFalse(testQueue.isCurrentQueueEmpty());
		assertTrue(testQueue.isOppositeQueueEmpty());
		assertTrue(testQueue.isMissedqueueEmpty());
	}

	@Test
	void testSwapQueueWithMissedQueueNotEmptyAndOppositeQueueNotEmpty() {
		// current queue empty, missed has items
		// don't swap queues ebcause
		testAddUpRequestBelowUpElevator();
		testAddDownRequestAboveUpElevator();

		assertTrue(testQueue.swapQueues());
		assertFalse(testQueue.isCurrentQueueEmpty());
		assertFalse(testQueue.isOppositeQueueEmpty());
		assertTrue(testQueue.isMissedqueueEmpty());
	}

	@Test
	void testRemoveRequestsFromMissedQueueWhileElevatorUp() {
		// missed queue, up
		testAddDownRequestAboveDownElevator();
		testQueue.swapQueues(Direction.UP);
		assertTrue(testQueue.isMissedqueueEmpty());

		int floor1 = testQueue.removeRequest();
		int floor2 = testQueue.removeRequest();
		// test proper ordering
		assertTrue(floor1 < floor2);
		assertTrue(testQueue.isMissedqueueEmpty());
		assertTrue(testQueue.isCurrentQueueEmpty());
	}

	@Test
	void testAddFloor() {

		assertTrue(testQueue.isMissedqueueEmpty());
		testQueue.addFloor(2, 3, 4, Direction.UP); // Should be added to missed Requests
		testQueue.addFloor(1, 3, 5, Direction.UP); // Should be added to missed Requests
		assertFalse(testQueue.isMissedqueueEmpty());
		testQueue.addFloor(4, 3, 6, Direction.UP);
		testQueue.addFloor(9, 3, 10, Direction.UP);
		testQueue.addFloor(7, 3, 8, Direction.UP);

		// Test for proper ordering added in upwardRequests
		assertEquals(testQueue.visitNextFloor(Direction.UP), 4);
		assertEquals(testQueue.visitNextFloor(Direction.UP), 6);
		assertEquals(testQueue.visitNextFloor(Direction.UP), 7);
		assertEquals(testQueue.visitNextFloor(Direction.UP), 8);
		assertEquals(testQueue.visitNextFloor(Direction.UP), 9);
		assertEquals(testQueue.visitNextFloor(Direction.UP), 10);
		testQueue.swapQueues(Direction.UP);
		// Test for missed requests update
		assertEquals(testQueue.visitNextFloor(Direction.UP), 1);
		assertEquals(testQueue.visitNextFloor(Direction.UP), 2);
		assertEquals(testQueue.visitNextFloor(Direction.UP), 4);
		assertEquals(testQueue.visitNextFloor(Direction.UP), 5);

		testQueue.addFloor(3, 5, 0, Direction.DOWN);
		testQueue.addFloor(1, 5, 0, Direction.DOWN);
		testQueue.addFloor(4, 5, 0, Direction.DOWN);
		assertTrue(testQueue.isMissedqueueEmpty());
		testQueue.addFloor(8, 5, 4, Direction.DOWN); // Should be added to missed Requests
		testQueue.addFloor(6, 5, 5, Direction.DOWN); // Should be added to missed Requests
		assertFalse(testQueue.isMissedqueueEmpty());

		// Test for proper ordering added in downwardRequests
		assertEquals(testQueue.visitNextFloor(Direction.DOWN), 4);
		assertEquals(testQueue.visitNextFloor(Direction.DOWN), 3);
		assertEquals(testQueue.visitNextFloor(Direction.DOWN), 1);
		assertEquals(testQueue.visitNextFloor(Direction.DOWN), 0);
		assertEquals(testQueue.visitNextFloor(Direction.DOWN), 0);
		assertEquals(testQueue.visitNextFloor(Direction.DOWN), 0);
		// Test for missed requests update
		/*
		assertEquals(testQueue.visitNextFloor(Direction.DOWN), 8);
		assertEquals(testQueue.visitNextFloor(Direction.DOWN), 6);
		assertEquals(testQueue.visitNextFloor(Direction.DOWN), 5);
		assertEquals(testQueue.visitNextFloor(Direction.DOWN), 4);
		 */

		// Test for invalid floor number
		try {
			testQueue.addFloor(-6, 2, -8, Direction.DOWN);
			fail(); // If not an Exception, then fail the test
		} catch (RuntimeException e) {
		}
	}

	@Test
	void testVisitNextFloor() {
		testQueue.addFloor(3, 0, 6, Direction.UP);
		testQueue.addFloor(1, 0, 8, Direction.UP);

		testQueue.addFloor(3, 4, 2, Direction.DOWN);
		testQueue.addFloor(1, 4, 0, Direction.DOWN);

		// Testing for visiting proper floor
		assertEquals(testQueue.visitNextFloor(Direction.UP), 1);
		assertEquals(testQueue.visitNextFloor(Direction.UP), 3);
		assertEquals(testQueue.visitNextFloor(Direction.UP), 6);
		assertEquals(testQueue.visitNextFloor(Direction.UP), 8);
		assertEquals(testQueue.visitNextFloor(Direction.DOWN), 3);
		assertEquals(testQueue.visitNextFloor(Direction.DOWN), 2);
		assertEquals(testQueue.visitNextFloor(Direction.DOWN), 1);
		assertEquals(testQueue.visitNextFloor(Direction.DOWN), 0);

		// Testing for emptying queue after dequeueing all floors
		assertTrue(testQueue.isUpqueueEmpty());
		assertTrue(testQueue.isDownqueueEmpty());
	}

	@Test
	void testPeekNextFloor() {
		testQueue.addFloor(3, 0, 7, Direction.UP);
		// Testing that it peeks the expected number
		assertEquals(testQueue.peekNextFloor(Direction.UP), 3);
		testQueue.visitNextFloor(Direction.UP);
		testQueue.visitNextFloor(Direction.UP);
		testQueue.addFloor(3, 4, 2, Direction.DOWN);
		testQueue.swapQueues(Direction.UP);
		assertEquals(testQueue.peekNextFloor(Direction.DOWN), 3);
	}

	@Test
	void testSwapQueues() {
		testQueue.addFloor(2, 3, 4, Direction.UP); // Should be added to missed Requests
		testQueue.addFloor(1, 3, 5, Direction.UP); // Should be added to missed Requests
		testQueue.swapQueues(Direction.UP);
		assertEquals(testQueue.visitNextFloor(Direction.UP), 1);
		assertEquals(testQueue.visitNextFloor(Direction.UP), 2);
	}

	@Test
	void testisUpqueueEmpty() {
		// Testing that it initializes empty
		assertTrue(testQueue.isUpqueueEmpty());

		// Testing not empty after adding floor
		testQueue.addFloor(3, 0, 6, Direction.UP);
		assertFalse(testQueue.isUpqueueEmpty());

		// Testing not empty after adding floor
		testQueue.visitNextFloor(Direction.UP);
		testQueue.visitNextFloor(Direction.UP);
		assertTrue(testQueue.isUpqueueEmpty());
	}

	@Test
	void testisDownqueueEmpty() {
		// Testing that it initializes empty
		assertTrue(testQueue.isDownqueueEmpty());

		// Testing not empty after adding floor
		testQueue.addFloor(3, 4, 0, Direction.DOWN);
		assertFalse(testQueue.isDownqueueEmpty());

		// Testing not empty after adding floor
		testQueue.visitNextFloor(Direction.DOWN);
		testQueue.visitNextFloor(Direction.DOWN);
		assertTrue(testQueue.isDownqueueEmpty());
	}

	@Test
	void testisMissedqueueEmpty() {
		// Testing that it initializes empty
		assertTrue(testQueue.isMissedqueueEmpty());

		// Testing not empty after adding floor
		testQueue.addFloor(3, 4, 6, Direction.UP);
		assertFalse(testQueue.isMissedqueueEmpty());

		// Testing not empty after adding floor
		testQueue.swapQueues(Direction.UP);
		assertTrue(testQueue.isMissedqueueEmpty());
	}
}
