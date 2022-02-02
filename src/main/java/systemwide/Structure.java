package systemwide;

/**
 * 
 * 
 * @author Liam Tripp
 */
public class Structure {
	
	private final int numberOfFloors;
	private final int numberOfElevators;
	
	// NOTE: should these immutable constants be accessed globally or passed along? 
	// passing along creates dependencies
	public Structure(int numberOfFloors, int numberOfElevators) {
		this.numberOfFloors = numberOfFloors;
		this.numberOfElevators = numberOfElevators;
	}
}
