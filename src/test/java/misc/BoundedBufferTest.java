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

	
	/**
	 *Initializes the objects for testing
	 * 
	 */
	@BeforeEach
	void setUp() {
		buffer = new BoundedBuffer();
		request1 = new ServiceRequest(LocalTime.NOON, 1, Direction.UP);
		request2 = new ServiceRequest(LocalTime.NOON, 2, Direction.DOWN);
	}

	@Test
	void testGetSize() {

		// Test size 0 when buffer created
		assertTrue(buffer.getSize() == 0);

		// Test size 1 after one service is added
		buffer.addLast(request1);
		assertTrue(buffer.getSize() == 1);

		// Test size 5 when reaching limit
		buffer.addLast(request1);
		buffer.addLast(request1);
		buffer.addLast(request1);
		buffer.addLast(request1);
		assertTrue(buffer.getSize() == 5);

		// Test size 4 when removing a request
		buffer.removeFirst();
		assertTrue(buffer.getSize() == 4);
	}

	@Test
	void testRemoveFirst() {
		// test that it adds and removes the proper request
		buffer.addLast(request1);
		buffer.addLast(request2);
		assertEquals(request1, buffer.removeFirst());
		assertEquals(request2, buffer.removeFirst());
	}

	@Test
	void testIsEmpty() {
		// test empty
		assertTrue(buffer.isEmpty());

		// test not empty with 1 request in buffer
		buffer.addLast(request1);
		assertFalse(buffer.isEmpty());

		// test not empty with 2 requests in buffer
		buffer.addLast(request1);
		assertFalse(buffer.isEmpty());
	}
}
