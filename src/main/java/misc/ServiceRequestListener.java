package misc;

/**
 * ServiceRequestListener allows ____ and ____ to respond to ServiceRequests.
 * 
 * @author Liam Tripp
 */
public interface ServiceRequestListener {
	public void handleElevatorRequest(ElevatorRequest elevatorRequest);
	public void handleFloorRequest(FloorRequest floorRequest);
}
