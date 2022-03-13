package elevatorsystem;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.ElevatorRequest;
import systemwide.Direction;
import systemwide.Origin;

import java.time.LocalTime;

/**
 * RequestQueueTest ensures that all the RequestQueue methods are working properly.
 * The main methods tested are adding Requests and swapping the active queue.
 *
 * @author Julian, Liam Tripp
 */
class RequestQueueTest {

	RequestQueue testQueue;

	@BeforeEach
	void setUp() {
		testQueue = new RequestQueue();
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
		assertFalse(testQueue.isMissedQueueEmpty());
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
		assertFalse(testQueue.isMissedQueueEmpty());
	}

	@Test
	void testSwapQueueWithCurrentQueue() {
		// add to current queue
		testAddDownRequestBelowDownElevator();
		assertFalse(testQueue.swapQueues());
		assertFalse(testQueue.isCurrentQueueEmpty());
		assertTrue(testQueue.isOppositeQueueEmpty());
		assertTrue(testQueue.isMissedQueueEmpty());
	}

	@Test
	void testSwapQueueWithOppositeQueue() {
		// currentQueue empty, opposite queue has items
		testAddUpRequestBelowDownElevator();
		assertTrue(testQueue.swapQueues());
		assertFalse(testQueue.isCurrentQueueEmpty());
		assertTrue(testQueue.isOppositeQueueEmpty());
	}

	@Test
	void testSwapQueueWithMissedQueue() {
		//missed queue not empty
		testAddDownRequestAboveDownElevator();

		assertFalse(testQueue.swapQueues());
		assertFalse(testQueue.isCurrentQueueEmpty());
		assertTrue(testQueue.isMissedQueueEmpty());
	}

	@Test
	void testSwapQueueWithMissedQueueAndOppositeQueue() {
		// current queue empty, opposite missed not empty, missed queue not empty
		testAddUpRequestBelowUpElevator();
		testAddDownRequestAboveUpElevator();

		assertTrue(testQueue.swapQueues());
		assertFalse(testQueue.isCurrentQueueEmpty());
		assertFalse(testQueue.isOppositeQueueEmpty());
		assertTrue(testQueue.isMissedQueueEmpty());
	}
	
	@Test
	void testSwapQueueWithAllEmpty() {
		//All queues are empty
		assertFalse(testQueue.swapQueues());
		assertTrue(testQueue.isCurrentQueueEmpty());
		assertTrue(testQueue.isOppositeQueueEmpty());
		assertTrue(testQueue.isMissedQueueEmpty());
	}

	@Test
	void testSwapQueueWithCurrentQueueAndOppositeQueue() {
		//current queue not empty, opposite queue not empty
		testAddDownRequestBelowDownElevator();
		testAddUpRequestBelowDownElevator();
		
		assertFalse(testQueue.swapQueues());
		assertFalse(testQueue.isCurrentQueueEmpty());
		assertFalse(testQueue.isOppositeQueueEmpty());
		assertTrue(testQueue.isMissedQueueEmpty());
	}
	
	@Test
	void testSwapQueueWithMissedQueueAndCurrentQueue() {
		//current queue not empty, missed queue not empty
		testAddDownRequestBelowDownElevator();
		testAddDownRequestAboveDownElevator();
		
		assertFalse(testQueue.swapQueues());
		assertFalse(testQueue.isCurrentQueueEmpty());
		assertTrue(testQueue.isOppositeQueueEmpty());
		assertFalse(testQueue.isMissedQueueEmpty());
	}
	
	@Test
	void testSwapQueueWithAllNotEmpty() {
		//current queue not empty, missed queue not empty, opposite not empty
		testAddDownRequestBelowDownElevator();
		testAddDownRequestAboveDownElevator();
		testAddUpRequestBelowDownElevator();
		
		assertFalse(testQueue.swapQueues());
		assertFalse(testQueue.isCurrentQueueEmpty());
		assertFalse(testQueue.isOppositeQueueEmpty());
		assertFalse(testQueue.isMissedQueueEmpty());
	}
	
	@Test
	void testCurrentQueueHasCorrectRemovalOrder() {
		// requests in current default direction (up)
		Direction elevatorDirection = Direction.UP;
		int elevatorFloor = 1;
		Direction requestDirection = Direction.UP;
		int floorNumber1 = 2;
		int floorNumber2 = 3;
		int floorNumber3 = 4;
		int floorNumber4 = 5;
		ElevatorRequest elevatorRequest1 = new ElevatorRequest(LocalTime.now(), floorNumber1, requestDirection, floorNumber2, Origin.ELEVATOR_SYSTEM);
		ElevatorRequest elevatorRequest2 = new ElevatorRequest(LocalTime.now(), floorNumber3, requestDirection, floorNumber4, Origin.ELEVATOR_SYSTEM);

		testQueue.addRequest(elevatorFloor, elevatorDirection, elevatorRequest1);
		testQueue.addRequest(elevatorFloor, elevatorDirection, elevatorRequest2);

		// make sure current direction queue is not empty
		assertFalse(testQueue.isCurrentQueueEmpty());

		// ensure swap successful
		assertFalse(testQueue.swapQueues());
		assertTrue(testQueue.isOppositeQueueEmpty());
		assertFalse(testQueue.isCurrentQueueEmpty());

		// ensure integers are removed in forward order
		int floor1 = testQueue.removeRequest();
		assertTrue(floor1 < testQueue.peekNextRequest());
		int floor2 = testQueue.removeRequest();
		assertTrue(floor1 < floor2);
	}

	@Test
	void testOppositeQueueHasCorrectRemovalOrder() {
		// for opposite queue, items should be removed in reverse order
		// requests in opposite of default direction (up)
		int floorNumber1 = 1;
		int floorNumber2 = 2;
		int floorNumber3 = 3;
		int floorNumber4 = 4;
		ElevatorRequest elevatorRequest1 = new ElevatorRequest(LocalTime.now(), floorNumber4, Direction.DOWN, floorNumber2, Origin.ELEVATOR_SYSTEM);
		ElevatorRequest elevatorRequest2 = new ElevatorRequest(LocalTime.now(), floorNumber3, Direction.DOWN, floorNumber1, Origin.ELEVATOR_SYSTEM);

		int elevatorFloor = 5;
		Direction elevatorDirection = Direction.UP;
		testQueue.addRequest(elevatorFloor, elevatorDirection, elevatorRequest1);
		// make sure opposite order not empty
		assertFalse(testQueue.isOppositeQueueEmpty());
		// elevatorDirection = Direction.changeDirection(elevatorDirection);

		// ensure swap successful
		assertTrue(testQueue.swapQueues());
		assertTrue(testQueue.isOppositeQueueEmpty());
		assertFalse(testQueue.isCurrentQueueEmpty());

		// ensure integers are removed in reverse order
		int floor1 = testQueue.removeRequest();
		assertTrue(floor1 > testQueue.peekNextRequest());
		int floor2 = testQueue.removeRequest();
		assertTrue(floor1 > floor2);

		// ensure keep adding to the reverse order queue
		// assume serviceDirection has been reversed
		testQueue.addRequest(elevatorFloor, Direction.DOWN, elevatorRequest2);
		assertTrue(testQueue.removeRequest() > testQueue.removeRequest());
	}

	@Test
	void testCurrentAndMissedCorrectRemovalOrder() {

	}

	@Test
	void testOppositeAndMissedCorrectRemovalOrder() {

	}
}
