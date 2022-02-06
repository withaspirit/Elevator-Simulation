package misc;

import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import requests.ServiceRequest;
import systemwide.Direction;
import systemwide.Origin;

/**
 * Test class for BoundedBuffer methods
 * 
 * @author Julian
 */
public class BoundedBufferTest {

	BoundedBuffer buffer;
	ServiceRequest request1, request2;
	Origin origin;
	
	/**
	 *Initializes the objects for testing
	 * 
	 */
	@BeforeEach
	void setUp() {
		buffer = new BoundedBuffer();
		request1 = new ServiceRequest(LocalTime.NOON, 1, Direction.UP, Origin.FLOOR_SYSTEM);
		request2 = new ServiceRequest(LocalTime.NOON, 2, Direction.DOWN, Origin.FLOOR_SYSTEM);
		origin = Origin.FLOOR_SYSTEM;
	}

	@Test
	void testGetSize() {
		// Test size 0 when buffer created
		assertTrue(buffer.getSize() == 0);

		// Test size 1 after one service is added
		buffer.addLast(request1, origin);
		assertTrue(buffer.getSize() == 1);

		// Test size 5 when reaching limit
		buffer.addLast(request1, origin);
		buffer.addLast(request1, origin);
		buffer.addLast(request1, origin);
		buffer.addLast(request1, origin);
		assertTrue(buffer.getSize() == 5);

		// Test size 4 when removing a request
		buffer.removeFirst(Origin.SCHEDULER);
		assertTrue(buffer.getSize() == 4);
	}

	@Test
	void testRemoveFirst() {
		// test that it adds and removes the proper request
		buffer.addLast(request1, origin);
		buffer.addLast(request2, origin);
		assertEquals(request1, buffer.removeFirst(Origin.SCHEDULER));
		assertEquals(request2, buffer.removeFirst(Origin.SCHEDULER));
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
