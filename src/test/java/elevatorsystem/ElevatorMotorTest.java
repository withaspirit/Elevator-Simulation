package elevatorsystem;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import systemwide.Direction;

/**
 * Test class for ElevatorMotor methods
 * 
 * @author Julian
 */
public class ElevatorMotorTest {

	ElevatorMotor motor;

	@BeforeEach
	void setUp() {
		motor = new ElevatorMotor();
	}

	@Test
	void testGetMovementState() {
		// Test initialization to Idle
		assertEquals(motor.getMovementState(), MovementState.IDLE);
	}

	@Test
	void testSetMovementState() {
		// Test setting to Active
		motor.setMovementState(MovementState.ACTIVE);
		assertEquals(motor.getMovementState(), MovementState.ACTIVE);
		// Test setting to Stuck
		motor.setMovementState(MovementState.STUCK);
		assertEquals(motor.getMovementState(), MovementState.STUCK);
	}

	@Test
	void testGetDirection() {
		// Test initialization to STOP direction
		assertEquals(motor.getDirection(), Direction.NONE);
	}

	@Test
	void testSetDirection() {
		// Test setting to Down direction
		motor.setDirection(Direction.DOWN);
		assertEquals(motor.getDirection(), Direction.DOWN);
		// Test setting to UP direction
		motor.setDirection(Direction.UP);
		assertEquals(motor.getDirection(), Direction.UP);
	}

	@Test
	void testMove() {
		// Testing upward movement
		assertEquals(motor.move(2, 4), 3);
		assertEquals(motor.getDirection(), Direction.UP);

		// Testing downward movement
		assertEquals(motor.move(4, 2), 3);
		assertEquals(motor.getDirection(), Direction.DOWN);

		// Testing reaching requested floor
		assertEquals(motor.move(3, 3), 3);
		assertEquals(motor.getDirection(), Direction.NONE);
	}

	@Test
	void testStop() {
		motor.move(2, 4);
		motor.setMovementState(MovementState.ACTIVE);
		motor.stop();
		assertEquals(motor.getDirection(), Direction.NONE);
		assertEquals(motor.getMovementState(), MovementState.IDLE);
	}

	@Test
	void testMoveUp() {
		motor.moveUp();
		assertEquals(motor.getDirection(), Direction.UP);
		assertEquals(motor.getMovementState(), MovementState.ACTIVE);
	}

	@Test
	void testMoveDown() {
		motor.moveDown();
		assertEquals(motor.getDirection(), Direction.DOWN);
		assertEquals(motor.getMovementState(), MovementState.ACTIVE);
	}

	@Test
	void testIsActive() {
		// Test not active
		assertFalse(motor.isActive());

		// Test not active
		motor.setMovementState(MovementState.ACTIVE);
		assertTrue(motor.isActive());
	}

	@Test
	void testIsIdle() {
		// Test Idle
		motor.setMovementState(MovementState.ACTIVE);
		motor.setMovementState(MovementState.IDLE);
		assertTrue(motor.isIdle());

		// Test not Idle
		motor.setMovementState(MovementState.ACTIVE);
		assertFalse(motor.isIdle());
	}
}
