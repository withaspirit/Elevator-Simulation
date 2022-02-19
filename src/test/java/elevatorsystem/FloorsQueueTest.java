package elevatorsystem;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import systemwide.Direction;

class FloorsQueueTest {

	FloorsQueue testQueue;

	@BeforeEach
	void setUp() throws Exception {
		testQueue = new FloorsQueue();
	}

	@Test
	void testAddFloor() {

		assertTrue(testQueue.isMissedqueueEmpty());
		testQueue.addFloor(2, 3, Direction.UP); // Should be added to missed Requests
		testQueue.addFloor(1, 3, Direction.UP); // Should be added to missed Requests
		assertFalse(testQueue.isMissedqueueEmpty());
		testQueue.addFloor(4, 3, Direction.UP);
		testQueue.addFloor(8, 3, Direction.UP);
		testQueue.addFloor(6, 3, Direction.UP);

		// Test for proper ordering added in upwardRequests
		assertEquals(testQueue.visitNextFloor(Direction.UP), 4);
		assertEquals(testQueue.visitNextFloor(Direction.UP), 6);
		assertEquals(testQueue.visitNextFloor(Direction.UP), 8);
		// Test for missed requests update
		assertEquals(testQueue.visitNextFloor(Direction.UP), 1);
		assertEquals(testQueue.visitNextFloor(Direction.UP), 2);

		testQueue.addFloor(3, 5, Direction.DOWN);
		testQueue.addFloor(1, 5, Direction.DOWN);
		testQueue.addFloor(4, 5, Direction.DOWN);
		assertTrue(testQueue.isMissedqueueEmpty());
		testQueue.addFloor(8, 5, Direction.DOWN); // Should be added to missed Requests
		testQueue.addFloor(6, 5, Direction.DOWN); // Should be added to missed Requests
		assertFalse(testQueue.isMissedqueueEmpty());

		// Test for proper ordering added in downwardRequests
		assertEquals(testQueue.visitNextFloor(Direction.DOWN), 4);
		assertEquals(testQueue.visitNextFloor(Direction.DOWN), 3);
		assertEquals(testQueue.visitNextFloor(Direction.DOWN), 1);
		// Test for missed requests update
		assertEquals(testQueue.visitNextFloor(Direction.DOWN), 8);
		assertEquals(testQueue.visitNextFloor(Direction.DOWN), 6);

		// Test for invalid floor number
		try {
			testQueue.addFloor(-6, 2, Direction.DOWN);
			fail(); // If not an Exception, then fail the test
		} catch (RuntimeException e) {
		}
	}

	@Test
	void testVisitNextFloor() {
		testQueue.addFloor(3, 0, Direction.UP);
		testQueue.addFloor(1, 0, Direction.UP);

		testQueue.addFloor(3, 4, Direction.DOWN);
		testQueue.addFloor(1, 4, Direction.DOWN);

		// Testing for visiting proper floor
		assertEquals(testQueue.visitNextFloor(Direction.UP), 1);
		assertEquals(testQueue.visitNextFloor(Direction.UP), 3);
		assertEquals(testQueue.visitNextFloor(Direction.DOWN), 3);
		assertEquals(testQueue.visitNextFloor(Direction.DOWN), 1);

		// Testing for emptying queue after dequeueing all floors
		assertTrue(testQueue.isUpqueueEmpty());
		assertTrue(testQueue.isDownqueueEmpty());
	}

	@Test
	void testPeekNextFloor() {
		testQueue.addFloor(3, 0, Direction.UP);
		testQueue.addFloor(3, 4, Direction.DOWN);

		// Testing that it peeks the expected number
		assertEquals(testQueue.peekNextFloor(Direction.UP), 3);
		assertEquals(testQueue.peekNextFloor(Direction.DOWN), 3);
	}

	@Test
	void testSwapQueues() {
		testQueue.addFloor(2, 3, Direction.UP); // Should be added to missed Requests
		testQueue.addFloor(1, 3, Direction.UP); // Should be added to missed Requests
		testQueue.swapQueues(Direction.UP);
		assertEquals(testQueue.visitNextFloor(Direction.UP), 1);
		assertEquals(testQueue.visitNextFloor(Direction.UP), 2);
	}

	@Test
	void testisUpqueueEmpty() {
		// Testing that it initializes empty
		assertTrue(testQueue.isUpqueueEmpty());

		// Testing not empty after adding floor
		testQueue.addFloor(3, 0, Direction.UP);
		assertFalse(testQueue.isUpqueueEmpty());

		// Testing not empty after adding floor
		testQueue.visitNextFloor(Direction.UP);
		assertTrue(testQueue.isUpqueueEmpty());
	}

	@Test
	void testisDownqueueEmpty() {
		// Testing that it initializes empty
		assertTrue(testQueue.isDownqueueEmpty());

		// Testing not empty after adding floor
		testQueue.addFloor(3, 4, Direction.DOWN);
		assertFalse(testQueue.isDownqueueEmpty());

		// Testing not empty after adding floor
		testQueue.visitNextFloor(Direction.DOWN);
		assertTrue(testQueue.isDownqueueEmpty());
	}

	@Test
	void testisMissedqueueEmpty() {
		// Testing that it initializes empty
		assertTrue(testQueue.isMissedqueueEmpty());

		// Testing not empty after adding floor
		testQueue.addFloor(3, 4, Direction.UP);
		assertFalse(testQueue.isMissedqueueEmpty());

		// Testing not empty after adding floor
		testQueue.swapQueues(Direction.UP);
		assertTrue(testQueue.isMissedqueueEmpty());
	}
}
