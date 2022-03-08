package systemwide;

/**
 * Direction indicates direction in terms of up, down, and stopped.
 *
 * @author Liam Tripp
 */
public enum Direction {
	UP("Up"),
	DOWN("Down"),
	NONE("None");

	private final String name;

	/**
	 * The constructor for Direction.
	 *
	 * @param name the name of the Direction
	 */
	Direction(String name) {
		this.name = name;
	}

	/**
	 * Returns the name of a Direction as a String.
	 *
	 * @return name the name of the Direction
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the Direction with the specified name.
	 *
	 * @param name the name of the direction
	 * @return the Direction with the specified name
	 */
	public static Direction getDirection(String name) {
		try {
			return valueOf(name.trim().toUpperCase());
		} catch (IllegalArgumentException iae) {
			System.out.println("Direction does not exist");
			iae.printStackTrace();
			return null;
		}
	}

	/**
	 * Changes the provided direction from Up to Down and vice-versa.
	 *
	 * @param direction the provided direction
	 * @return direction the opposite direction of the provided direction
	 */
	public static Direction swapDirection(Direction direction) {
		if (direction == Direction.UP) {
			direction = Direction.DOWN;
		} else if (direction == Direction.DOWN) {
			direction = Direction.UP;
		} else {
			throw new IllegalArgumentException("Direction provided is not valid.");
		}
		return direction;
	}
}
