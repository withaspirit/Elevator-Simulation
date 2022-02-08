package misc;

import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import systemwide.Direction;

/**
 * Test class for BoundedBuffer methods
 * 
 * @author Julian
 */
public class BoundedBufferTest {

	BoundedBuffer buffer;
	ServiceRequest request1, request2;
	Thread origin, temp;
	
	/**
	 *Initializes the objects for testing
	 * 
	 */
	@BeforeEach
	void setUp() {
		buffer = new BoundedBuffer();
		origin = Thread.currentThread();
		temp = new Thread();
		request1 = new ServiceRequest(LocalTime.NOON, 1, Direction.UP, origin);
		request2 = new ServiceRequest(LocalTime.NOON, 2, Direction.DOWN, origin);
	}

	@Test
	void testGetSize() {
		// Test size 0 when buffer created
		assertEquals(0, buffer.getSize());

		// Test size 1 after one service is added
		buffer.addLast(request1, origin);
		assertEquals(1, buffer.getSize());

		// Test size 5 when reaching limit
		buffer.addLast(request1, origin);
		buffer.addLast(request1, origin);
		buffer.addLast(request1, origin);
		buffer.addLast(request1, origin);
		assertEquals(5, buffer.getSize());

		// Test size 4 when removing a request
		buffer.removeFirst(temp);
		assertEquals(4, buffer.getSize());
	}

	@Test
	void testRemoveFirst() {
		// test that it adds and removes the proper request
		buffer.addLast(request1, origin);
		buffer.addLast(request2, origin);
		assertEquals(request1, buffer.removeFirst(temp));
		assertEquals(request2, buffer.removeFirst(temp));
	}

	@Test
	void testIsEmpty() {
		// test empty
		assertTrue(buffer.isEmpty());

		// test not empty with 1 request in buffer
		buffer.addLast(request1, origin);
		assertFalse(buffer.isEmpty());

		// test not empty with 2 requests in buffer
		buffer.addLast(request1, origin);
		assertFalse(buffer.isEmpty());
	}
}
