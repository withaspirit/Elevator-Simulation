package elevatorsystem;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FloorsQueueTest {

	FloorsQueue testQueue;
	
	@BeforeEach
	void setUp() throws Exception {
		 testQueue = new FloorsQueue();
	}

	@Test
	void testAddFloor() {
		
		testQueue.addFloor(3, "Up");
		testQueue.addFloor(1, "Up");
		testQueue.addFloor(4, "Up");
		testQueue.addFloor(8, "Up");
		testQueue.addFloor(6, "Up");
		
		testQueue.addFloor(3, "Down");
		testQueue.addFloor(1, "Down");
		testQueue.addFloor(4, "Down");
		testQueue.addFloor(8, "Down");
		testQueue.addFloor(6, "Down");
		
        // Test for proper ordering added in upwardRequests
        assertEquals(testQueue.visitNextFloor("Up"), 1);
        assertEquals(testQueue.visitNextFloor("Up"), 3);
        assertEquals(testQueue.visitNextFloor("Up"), 4);
        assertEquals(testQueue.visitNextFloor("Up"), 6);
        assertEquals(testQueue.visitNextFloor("Up"), 8);
        
        //Test for proper ordering added in downwardRequests
        assertEquals(testQueue.visitNextFloor("Down"), 8);
        assertEquals(testQueue.visitNextFloor("Down"), 6);
        assertEquals(testQueue.visitNextFloor("Down"), 4);
        assertEquals(testQueue.visitNextFloor("Down"), 3);
        assertEquals(testQueue.visitNextFloor("Down"), 1);
        
        //Test for invalid Direction
        try { 
        	testQueue.addFloor(6, "down");
        	fail();   //If not an Exception, then fail the test
        } catch (RuntimeException e) {
        }
      //Test for invalid floor number
        try { 
        	testQueue.addFloor(-6, "Down");
        	fail();	  //If not an Exception, then fail the test
        } catch (RuntimeException e) {
        }
	}

	@Test
	void testVisitNextFloor() {
		testQueue.addFloor(3, "Up");
		testQueue.addFloor(1, "Up");
		
		testQueue.addFloor(3, "Down");
		testQueue.addFloor(1, "Down");
		
		//Testing for visiting proper floor
        assertEquals(testQueue.visitNextFloor("Up"), 1);
        assertEquals(testQueue.visitNextFloor("Up"), 3);
        assertEquals(testQueue.visitNextFloor("Down"), 3);
        assertEquals(testQueue.visitNextFloor("Down"), 1);
        
        //Testing for emptying queue after dequeueing all floors
        assertEquals(testQueue.isEmpty() ,0);
        
        //Test for invalid Direction
        testQueue.addFloor(1, "Down");
        try { 
        	testQueue.visitNextFloor("down");
        	fail();   //If not an Exception, then fail the test
        } catch (RuntimeException e) {
        }
	}

	@Test 
	void testPeekNextFloor() {
		testQueue.addFloor(3, "Up");
		testQueue.addFloor(3, "Down");
		
		//Testing that it peeks the expected number
		assertEquals(testQueue.peekNextFloor("Up"),3);
		assertEquals(testQueue.peekNextFloor("Down"),3);
		
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
		testQueue.addFloor(3, "Down");
		assertEquals(testQueue.isEmpty(), 2);
		
		//Testing that neither is empty
		testQueue.addFloor(3, "Up");
		assertEquals(testQueue.isEmpty(), 3);
		
		//Testing that only upwardRequest not empty
		testQueue.visitNextFloor("Down");
		assertEquals(testQueue.isEmpty(), 1);
		
	}
}
