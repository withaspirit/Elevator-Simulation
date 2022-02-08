/**
 * 
 */
package testsystem;

import elevatorsystem.ElevatorSubsystem;
import floorsystem.FloorSubsystem;
import scheduler.Scheduler;
import misc.BoundedBuffer;

/**
 * Test program to instantiate and start the subsystems threads
 * 
 * @author Julian
 *
 */
public class TestSystem {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Thread scheduler, elevatorSubsystem, floorSubsystem;
		BoundedBuffer ElevatorBuffer, FloorSubBuffer;

		ElevatorBuffer = new BoundedBuffer();
		FloorSubBuffer = new BoundedBuffer();

		scheduler = new Thread(new Scheduler(ElevatorBuffer, FloorSubBuffer), "Scheduler");
		elevatorSubsystem = new Thread(new ElevatorSubsystem(ElevatorBuffer), "Elevator Subsystem");
		floorSubsystem = new Thread(new FloorSubsystem(FloorSubBuffer), "Floor Subsystem");

		scheduler.start();
		elevatorSubsystem.start();
		floorSubsystem.start();
	}
}
