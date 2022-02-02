package misc;

import systemwide.Direction;

import java.time.LocalTime;

/**
 * ServiceRequest is an abstraction for ElevatorRequests and FloorRequests.
 * 
 * @author Liam Tripp
 *
 */
//FIXME: This could be refactored into an abstract class, but is an interface for now due to design uncertainty
public interface ServiceRequest {
	public LocalTime getTime();
	public int getFloorNumber();
	public Direction getDirection();
}
