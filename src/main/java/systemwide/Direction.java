package systemwide;

import java.util.HashMap;
import java.util.Locale;

/**
 * Direction indicates vertical direction in terms of up, down, and stopped.
 * 
 * @author Liam Tripp
 *
 */
public enum Direction {
	UP("Up"),
	DOWN("Down"),
	STOPPED("Stop");

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
			return valueOf(name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase());
		} catch (IllegalArgumentException iae) {
			System.out.println("Direction does not exist");
			iae.printStackTrace();
			return null;
		}
	}
}
