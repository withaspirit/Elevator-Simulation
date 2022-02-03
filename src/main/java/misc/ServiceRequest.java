package misc;

import systemwide.Direction;

import java.time.LocalTime;

/**
 * ServiceRequest is an abstract event data structure for when a user requests an
 * Elevator's service.
 * 
 * @author Liam Tripp
 */
//FIXME: This could be refactored into an abstract class, but is an interface for now due to design uncertainty
public interface ServiceRequest {
	LocalTime getTime();
	int getFloorNumber();
	Direction getDirection();
}
