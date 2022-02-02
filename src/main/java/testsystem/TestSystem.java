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
		BoundedBuffer schedulerElevatorBuffer, schedulerFloorsubBuffer;

		schedulerElevatorBuffer = new BoundedBuffer();
		schedulerFloorsubBuffer = new BoundedBuffer();

		scheduler = new Thread(new Scheduler(schedulerElevatorBuffer, schedulerFloorsubBuffer), "Scheduler");
		elevatorSubsystem = new Thread(new ElevatorSubsystem(schedulerElevatorBuffer), "Elevator Subsystem");
		floorSubsystem = new Thread(new FloorSubsystem(schedulerFloorsubBuffer), "Floor Subsystem");

		scheduler.start();
		elevatorSubsystem.start();
		floorSubsystem.start();
	}
}
