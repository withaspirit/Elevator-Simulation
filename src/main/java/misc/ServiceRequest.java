package misc;

import systemwide.Direction;

/**
 * ServiceRequest is an abstraction for ElevatorRequests and FloorRequests.
 * 
 * @author Liam Tripp
 *
 */
//FIXME: This could be refactored into an abstract class, but is an interface for now due to design uncertainty
public interface ServiceRequest {
	int getTime();
	int getFloorNumber();
	Direction getDirection();
}
