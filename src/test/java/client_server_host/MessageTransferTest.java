package client_server_host;

import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import requests.ElevatorRequest;
import requests.FloorRequest;
import requests.SystemEvent;
import requests.ApproachEvent;
import systemwide.Direction;
import systemwide.Origin;

/**
 * MessageTransferTest ensures the encoding/decoding methods provided by the
 * MessageTransfer class are functional.
 * 
 * @author Julian
 */
public class MessageTransferTest {

	MessageTransfer msgTransfer; 
	ElevatorRequest elevatorRequest;
	FloorRequest floorRequest;
	ApproachEvent approachEvent;
	LocalTime timeNow;
	
	@BeforeEach
	void setUp() {
		int portNumber = 0;   //Any port number
		timeNow = LocalTime.now(); 
		msgTransfer = new MessageTransfer(portNumber);
		elevatorRequest = new ElevatorRequest(timeNow, 2, Direction.UP, 4, Origin.FLOOR_SYSTEM);
		floorRequest = new FloorRequest(timeNow, 7, Direction.DOWN, 0, Origin.SCHEDULER);
		approachEvent = new ApproachEvent(elevatorRequest, 3, 5);
	}
	
	@Test
	void testEncodingWithElevatorRequest() {
		byte[] requestIn;
		requestIn = msgTransfer.encodeObject(elevatorRequest);
		SystemEvent systemEventOut = (SystemEvent)msgTransfer.decodeObject(requestIn);
		
		//Test for correct class instance
		assertTrue(systemEventOut instanceof ElevatorRequest);
		
		//Test for proper attributes
		ElevatorRequest requestOut = (ElevatorRequest)systemEventOut;
		assertEquals(requestOut.getOrigin(), Origin.FLOOR_SYSTEM);
		assertEquals(requestOut.getTime(), timeNow);
		assertEquals(requestOut.getFloorNumber(), 2);
		assertEquals(requestOut.getDirection(), Direction.UP);
		assertEquals(requestOut.getDesiredFloor(), 4);
	}
	
	@Test
	void testEncodingWithFloorRequest() {
		byte[] requestIn;
		requestIn = msgTransfer.encodeObject(floorRequest);
		SystemEvent systemEventOut = (SystemEvent)msgTransfer.decodeObject(requestIn);
	
		//Test for correct class instance
		assertTrue(systemEventOut instanceof FloorRequest);
		
		//Test for proper attributes
		FloorRequest requestOut = (FloorRequest)systemEventOut;
		assertEquals(requestOut.getOrigin(), Origin.SCHEDULER);
		assertEquals(requestOut.getTime(), timeNow);
		assertEquals(requestOut.getFloorNumber(), 7);
		assertEquals(requestOut.getDirection(), Direction.DOWN);
		assertEquals(requestOut.getElevatorNumber(), 0);
	}
	
	@Test
	void testEncodingWithApproachEvent() {
		byte[] requestIn;
		requestIn = msgTransfer.encodeObject(approachEvent);
		SystemEvent systemEventOut = (SystemEvent)msgTransfer.decodeObject(requestIn);
	
		//Test for correct class instance
		assertTrue(systemEventOut instanceof ApproachEvent);
		
		//Test for proper attributes
		ApproachEvent requestOut = (ApproachEvent)systemEventOut;
		assertEquals(requestOut.getOrigin(), approachEvent.getOrigin());
		assertEquals(requestOut.getTime(), approachEvent.getTime());
		assertEquals(requestOut.getFloorNumber(), approachEvent.getFloorNumber());
		assertEquals(requestOut.getDirection(), approachEvent.getDirection());
		assertEquals(requestOut.getElevatorNumber(), approachEvent.getElevatorNumber());
	}
}
