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
		BoundedBuffer elevatorSubsystemBuffer, floorSubsystemBuffer;

		elevatorSubsystemBuffer = new BoundedBuffer();
		floorSubsystemBuffer = new BoundedBuffer();

		scheduler = new Thread(new Scheduler(elevatorSubsystemBuffer, floorSubsystemBuffer), "Scheduler");
		elevatorSubsystem = new Thread(new ElevatorSubsystem(elevatorSubsystemBuffer), "Elevator Subsystem");
		floorSubsystem = new Thread(new FloorSubsystem(floorSubsystemBuffer), "Floor Subsystem");

		scheduler.start();
		elevatorSubsystem.start();
		floorSubsystem.start();
	}
}
