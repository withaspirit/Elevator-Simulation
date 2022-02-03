package systemwide;

/**
 * Direction indicates direction in terms of up, down, and stopped.
 *
 * @author Liam Tripp
 *
 */
public enum Direction {
	UP("Up"),
	DOWN("Down"),
	STOP("Stop");

	private String name;

	/**
	 * The constructor for Direction.
	 *
	 * @param name the name of the Direction
	 */
	Direction(String name) {
		this.name = name;
	}

	/**
	 * Returns the name of the Direction.
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
}
