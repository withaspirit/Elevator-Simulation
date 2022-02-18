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
		
		testQueue.addFloor(3, Direction.UP.getName());
		testQueue.addFloor(1, Direction.UP.getName());
		testQueue.addFloor(4, Direction.UP.getName());
		testQueue.addFloor(8, Direction.UP.getName());
		testQueue.addFloor(6, Direction.UP.getName());
		
		testQueue.addFloor(3, Direction.DOWN.getName());
		testQueue.addFloor(1, Direction.DOWN.getName());
		testQueue.addFloor(4, Direction.DOWN.getName());
		testQueue.addFloor(8, Direction.DOWN.getName());
		testQueue.addFloor(6, Direction.DOWN.getName());
		
        // Test for proper ordering added in upwardRequests
        assertEquals(testQueue.visitNextFloor(Direction.UP.getName()), 1);
        assertEquals(testQueue.visitNextFloor(Direction.UP.getName()), 3);
        assertEquals(testQueue.visitNextFloor(Direction.UP.getName()), 4);
        assertEquals(testQueue.visitNextFloor(Direction.UP.getName()), 6);
        assertEquals(testQueue.visitNextFloor(Direction.UP.getName()), 8);
        
        //Test for proper ordering added in downwardRequests
        assertEquals(testQueue.visitNextFloor(Direction.DOWN.getName()), 8);
        assertEquals(testQueue.visitNextFloor(Direction.DOWN.getName()), 6);
        assertEquals(testQueue.visitNextFloor(Direction.DOWN.getName()), 4);
        assertEquals(testQueue.visitNextFloor(Direction.DOWN.getName()), 3);
        assertEquals(testQueue.visitNextFloor(Direction.DOWN.getName()), 1);
        
        //Test for invalid Direction
        try { 
        	testQueue.addFloor(6, "down");
        	fail();   //If not an Exception, then fail the test
        } catch (RuntimeException e) {
        }
      //Test for invalid floor number
        try { 
        	testQueue.addFloor(-6, Direction.DOWN.getName());
        	fail();	  //If not an Exception, then fail the test
        } catch (RuntimeException e) {
        }
	}

	@Test
	void testVisitNextFloor() {
		testQueue.addFloor(3, Direction.UP.getName());
		testQueue.addFloor(1, Direction.UP.getName());
		
		testQueue.addFloor(3, Direction.DOWN.getName());
		testQueue.addFloor(1, Direction.DOWN.getName());
		
		//Testing for visiting proper floor
        assertEquals(testQueue.visitNextFloor(Direction.UP.getName()), 1);
        assertEquals(testQueue.visitNextFloor(Direction.UP.getName()), 3);
        assertEquals(testQueue.visitNextFloor(Direction.DOWN.getName()), 3);
        assertEquals(testQueue.visitNextFloor(Direction.DOWN.getName()), 1);
        
        //Testing for emptying queue after dequeueing all floors
        assertEquals(testQueue.isEmpty() ,0);
        
        //Test for invalid Direction
        testQueue.addFloor(1, Direction.DOWN.getName());
        try { 
        	testQueue.visitNextFloor("down");
        	fail();   //If not an Exception, then fail the test
        } catch (RuntimeException e) {
        }
	}

	@Test 
	void testPeekNextFloor() {
		testQueue.addFloor(3, Direction.UP.getName());
		testQueue.addFloor(3, Direction.DOWN.getName());
		
		//Testing that it peeks the expected number
		assertEquals(testQueue.peekNextFloor(Direction.UP.getName()),3);
		assertEquals(testQueue.peekNextFloor(Direction.DOWN.getName()),3);
		
        //Test for invalid Direction
        try { 
        	testQueue.peekNextFloor("down");
        	fail();   //If not an Exception, then fail the test
        } catch (RuntimeException e) {
        }
	}
	
	@Test
	void testIsEmpty() {
		//Testing that it initializes empty
		assertEquals(testQueue.isEmpty(), 0);
		
		//Testing that only downwardRequest not empty
		testQueue.addFloor(3, Direction.DOWN.getName());
		assertEquals(testQueue.isEmpty(), 2);
		
		//Testing that neither is empty
		testQueue.addFloor(3, Direction.UP.getName());
		assertEquals(testQueue.isEmpty(), 3);
		
		//Testing that only upwardRequest not empty
		testQueue.visitNextFloor(Direction.DOWN.getName());
		assertEquals(testQueue.isEmpty(), 1);
		
	}
}
