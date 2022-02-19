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

		testQueue.addFloor(3, Direction.UP);
		testQueue.addFloor(1, Direction.UP);
		testQueue.addFloor(4, Direction.UP);
		testQueue.addFloor(8, Direction.UP);
		testQueue.addFloor(6, Direction.UP);

		testQueue.addFloor(3, Direction.DOWN);
		testQueue.addFloor(1, Direction.DOWN);
		testQueue.addFloor(4, Direction.DOWN);
		testQueue.addFloor(8, Direction.DOWN);
		testQueue.addFloor(6, Direction.DOWN);

		// Test for proper ordering added in upwardRequests
		assertEquals(testQueue.visitNextFloor(Direction.UP), 1);
		assertEquals(testQueue.visitNextFloor(Direction.UP), 3);
		assertEquals(testQueue.visitNextFloor(Direction.UP), 4);
		assertEquals(testQueue.visitNextFloor(Direction.UP), 6);
		assertEquals(testQueue.visitNextFloor(Direction.UP), 8);

		// Test for proper ordering added in downwardRequests
		assertEquals(testQueue.visitNextFloor(Direction.DOWN), 8);
		assertEquals(testQueue.visitNextFloor(Direction.DOWN), 6);
		assertEquals(testQueue.visitNextFloor(Direction.DOWN), 4);
		assertEquals(testQueue.visitNextFloor(Direction.DOWN), 3);
		assertEquals(testQueue.visitNextFloor(Direction.DOWN), 1);

		// Test for invalid floor number
		try {
			testQueue.addFloor(-6, Direction.DOWN);
			fail(); // If not an Exception, then fail the test
		} catch (RuntimeException e) {
		}
	}

	@Test
	void testVisitNextFloor() {
		testQueue.addFloor(3, Direction.UP);
		testQueue.addFloor(1, Direction.UP);

		testQueue.addFloor(3, Direction.DOWN);
		testQueue.addFloor(1, Direction.DOWN);

		// Testing for visiting proper floor
		assertEquals(testQueue.visitNextFloor(Direction.UP), 1);
		assertEquals(testQueue.visitNextFloor(Direction.UP), 3);
		assertEquals(testQueue.visitNextFloor(Direction.DOWN), 3);
		assertEquals(testQueue.visitNextFloor(Direction.DOWN), 1);

		// Testing for emptying queue after dequeueing all floors
		assertEquals(testQueue.isEmpty(), 0);
	}

	@Test
	void testPeekNextFloor() {
		testQueue.addFloor(3, Direction.UP);
		testQueue.addFloor(3, Direction.DOWN);

		// Testing that it peeks the expected number
		assertEquals(testQueue.peekNextFloor(Direction.UP), 3);
		assertEquals(testQueue.peekNextFloor(Direction.DOWN), 3);
	}

	@Test
	void testIsEmpty() {
		// Testing that it initializes empty
		assertEquals(testQueue.isEmpty(), 0);

		// Testing that only downwardRequest not empty
		testQueue.addFloor(3, Direction.DOWN);
		assertEquals(testQueue.isEmpty(), 2);

		// Testing that neither is empty
		testQueue.addFloor(3, Direction.UP);
		assertEquals(testQueue.isEmpty(), 3);

		// Testing that only upwardRequest not empty
		testQueue.visitNextFloor(Direction.DOWN);
		assertEquals(testQueue.isEmpty(), 1);

	}
}
