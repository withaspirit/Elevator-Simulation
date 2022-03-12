package systemwide;

import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import requests.ServiceRequest;

/**
 * Test class for BoundedBuffer methods
 * 
 * @author Julian, Ryan Dash
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
		request1 = new ServiceRequest(LocalTime.NOON, 1, Direction.UP, Origin.FLOOR_SYSTEM);
		request2 = new ServiceRequest(LocalTime.NOON, 2, Direction.DOWN, Origin.FLOOR_SYSTEM);
	}

	@Test
	void testGetSize() {
		// Test size 0 when buffer created
		assertEquals(0, buffer.getSize());

		// Test size 1 after one service is added
		buffer.addLast(request1, Origin.FLOOR_SYSTEM);
		assertEquals(1, buffer.getSize());

		// Test size 5 when reaching limit
		buffer.addLast(request1, Origin.FLOOR_SYSTEM);
		buffer.addLast(request1, Origin.FLOOR_SYSTEM);
		buffer.addLast(request1, Origin.FLOOR_SYSTEM);
		buffer.addLast(request1, Origin.FLOOR_SYSTEM);
		assertEquals(5, buffer.getSize());

		// Test size 4 when removing a request
		buffer.removeFirst(Origin.SCHEDULER);
		assertEquals(4, buffer.getSize());
	}

	@Test
	void testRemoveFirst() {
		// test that it adds and removes the proper request
		buffer.addLast(request1, Origin.FLOOR_SYSTEM);
		buffer.addLast(request2, Origin.FLOOR_SYSTEM);
		assertEquals(request1, buffer.removeFirst(Origin.ELEVATOR_SYSTEM));
		assertEquals(request2, buffer.removeFirst(Origin.ELEVATOR_SYSTEM));
	}

	@Test
	void testIsEmpty() {
		// test empty
		assertTrue(buffer.isEmpty());

		// test not empty with 1 request in buffer
		buffer.addLast(request1, Origin.SCHEDULER);
		assertFalse(buffer.isEmpty());

		// test not empty with 2 requests in buffer
		buffer.addLast(request1, Origin.SCHEDULER);
		assertFalse(buffer.isEmpty());
	}
}
