package scheduler;

import client_server_host.IntermediateHost;
import client_server_host.Port;
import client_server_host.RequestMessage;
import elevatorsystem.MovementState;
import requests.*;
import systemwide.Direction;
import systemwide.Origin;
import systemwide.Structure;
import systemwide.SystemStatus;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.lang.System;

/**
 * Scheduler handles the requests from all system components.
 *
 * @author Liam Tripp, Julian, Ryan Dash
 */
public class Scheduler implements Runnable {

	private static ArrayList<ElevatorMonitor> elevatorMonitorList;
	private final IntermediateHost intermediateHost;
	private static Presenter presenter;
	private final SystemStatus systemStatus;
	private final Timer timer;
	private TimerTask timerTask;
	private long startTime = -1;
	private final int timerTimeOut = 7000; // milliseconds
	private static int schedulerThreadsTerminated;

	/**
	 * Constructor for Scheduler.
	 *
	 * @param portNumber the port number associated with the class's DatagramSocket
	 */
	public Scheduler(int portNumber) {
		elevatorMonitorList = new ArrayList<>();
		intermediateHost = new IntermediateHost(portNumber);
		systemStatus = new SystemStatus(false);
		timer = new Timer();
		presenter = null;
		schedulerThreadsTerminated = 0;
	}

	/**
	 * Add ElevatorMonitor to elevatorMonitorList.
	 *
	 * @param elevatorNumber an elevator number corresponding to an elevator
	 */
	public void addElevatorMonitor(int elevatorNumber) {
		elevatorMonitorList.add(new ElevatorMonitor(elevatorNumber));
	}

	/**
	 * Sets the Scheduler's presenter to a valid presenter.
	 * This will allow for output to the GUI's view.
	 *
	 * @param presenter a Presenter
	 */
	public void setPresenter(Presenter presenter){
		Scheduler.presenter = presenter;
	}

	/**
	 * Get the current static instance of elevatorMonitorList containing a list of elevator monitors.
	 *
	 * @return a list of elevator monitors
	 */
	public static ArrayList<ElevatorMonitor> getElevatorMonitorList() {
		return elevatorMonitorList;
	}

	/**
	 * Gets the SystemStatus of the System.
	 *
	 * @return the SystemStatus of the System
	 */
	public SystemStatus getSystemStatus() {
		return systemStatus;
	}

	/**
	 * Takes a DatagramPacket from the IntermediateHost and processes it.
	 * If it's data (i.e. contains a SystemEvent), it is processed by Scheduler.
	 * Otherwise, it's a request for data.
	 */
	private void receiveAndProcessPacket() {
			DatagramPacket receivePacket = intermediateHost.receivePacket();
			Object object = intermediateHost.convertToObject(receivePacket);

			// respond to Data Request
			if (object instanceof String) {
				Object dataObject;

				// queue is not empty, return data
				// otherwise, send dummy message notifying empty status
				if (!intermediateHost.queueIsEmpty()) {
					if (this.startTime == -1) {
						this.startTime = System.nanoTime();
						System.out.print("time started with string");
					}
					
					dataObject = intermediateHost.getPacketFromQueue();

					if (dataObject instanceof ElevatorRequest elevatorRequest) {
						int chosenElevator = chooseElevator(elevatorRequest);
						elevatorRequest.setElevatorNumber(chosenElevator);

						String messageToPrint = LocalTime.now() + "\n";
						messageToPrint += "Scheduler assigned to Elevator #" + chosenElevator + " the " +
								elevatorRequest.getClass().getSimpleName() + ": "  + elevatorRequest + ".\n";
						System.out.println(messageToPrint);
					}
					//Resets the inactivity timer when there's activity.
					resetTimer();
				} else {
					dataObject = RequestMessage.EMPTYQUEUE.getMessage();
				}
				// send the object right away
				intermediateHost.sendObject(dataObject, receivePacket.getAddress(), receivePacket.getPort());

			} else if (object instanceof SystemEvent systemEvent) {
				if (this.startTime == -1) {
					this.startTime = System.nanoTime();
					System.out.print("time started with string");
				}
				
				intermediateHost.acknowledgeDataReception(receivePacket);
				processData(systemEvent);
				//Resets the inactivity timer when there's activity.
				resetTimer();
			} 
	}

	/**
	 * Process data that Scheduler's DatagramSocket has received.
	 * Create a new packet and manipulate it according to the packet's Origin.
	 *
	 * @param event a systemEvent to be processed
	 */
	public void processData(SystemEvent event) {

		if (event instanceof ElevatorMonitor elevatorMonitor){
			elevatorMonitorList.get(elevatorMonitor.getElevatorNumber()-1).updateMonitor(elevatorMonitor);
			if (presenter != null){
				presenter.updateElevatorView(elevatorMonitor);
			}
		} else {
			event.setOrigin(Origin.changeOrigin(event.getOrigin()));
			intermediateHost.addEventToQueue(event);
		}
	}

	/**
	 * Enables Scheduler and the other systems.
	 *
	 * @param structure contains the information to initialize the other systems
	 * @param inetAddress the IP address of the destination
	 * @param portNumber the port the packet is being sent to
	 */
	public void enableSystem(Structure structure, InetAddress inetAddress, int portNumber) {
		systemStatus.setSystemActivated(true);
		intermediateHost.sendObject(structure, inetAddress, portNumber);
	}

	/**
	 * Returns an elevator number corresponding to an elevator that is
	 * best suited to perform the given ElevatorRequest based on
	 * expected time to fulfill the request and direction of elevator.
	 *
	 * @param elevatorRequest an ElevatorRequest
	 * @return a number corresponding to an elevator
	 */
	public int chooseElevator(ElevatorRequest elevatorRequest) {

		double elevatorBestExpectedTime = 0.0;
		// Best elevator is an elevator traveling in path that collides with request floor
		double elevatorOkExpectedTime = 0.0;
		// Ok elevator is an elevator that is traveling in the other direction
		double elevatorWorstExpectedTime = 0.0;
		// Worst elevator is an elevator that is traveling in the same direction but missed the request
		int chosenBestElevator = 0;
		int chosenOkElevator = 0;
		int chosenWorstElevator = 0;
		for (ElevatorMonitor monitor : elevatorMonitorList) {

			MovementState state = monitor.getState();
			Direction requestDirection = elevatorRequest.getDirection();
			double tempExpectedTime = monitor.getQueueTime();
			int currentFloor = monitor.getCurrentFloor();
			int desiredFloor = elevatorRequest.getDesiredFloor();
			int elevatorNumber = monitor.getElevatorNumber();
			Direction currentDirection = monitor.getDirection();

			if (currentDirection == Direction.UP){
				currentFloor += 1;
			} else if (currentDirection == Direction.DOWN){
				currentFloor -=1;
			}

			if (monitor.getHasNoRequests()) {
				return elevatorNumber;

			} else if (state == MovementState.STUCK) {
				System.err.println("Elevator#" + elevatorNumber + " is stuck");

			} else if (monitor.getDirection() == requestDirection) {
				if (elevatorBestExpectedTime == 0 || elevatorBestExpectedTime > tempExpectedTime) {
					if (requestDirection == Direction.DOWN && currentFloor > desiredFloor) {
						//check if request is in path current floor > directed floor going down
						elevatorBestExpectedTime = tempExpectedTime;
						chosenBestElevator = elevatorNumber;

					} else if (requestDirection == Direction.UP && currentFloor < desiredFloor) {
						//check if request is in path current floor < directed floor going up
						elevatorBestExpectedTime = tempExpectedTime;
						chosenBestElevator = elevatorNumber;

					} else if (elevatorOkExpectedTime == 0 || elevatorOkExpectedTime > tempExpectedTime){
						//if request is in the correct direction but not in path of elevator
						elevatorWorstExpectedTime = tempExpectedTime;
						chosenWorstElevator = elevatorNumber;
					}
				}
			} else {
				if (elevatorWorstExpectedTime == 0 || elevatorWorstExpectedTime > tempExpectedTime) {
					//if the elevator traveling in the wrong direction
					elevatorOkExpectedTime = tempExpectedTime;
					chosenOkElevator = elevatorNumber;
				}
			}
		}
		if (chosenBestElevator == 0) {
			if (chosenOkElevator == 0){
				chosenBestElevator = chosenWorstElevator;
			} else {
				chosenBestElevator = chosenOkElevator;
			}
		}
		return chosenBestElevator;
	}

	/**
	 * Resets the inactivity timer to show that the scheduler did work 
	 */
	public void resetTimer() {
		if (timerTask == null || timerTask.cancel()) {
			timerTask = new TimerTask() {
				@Override
				public void run() {
					long timeElapsed = (System.nanoTime() - startTime) / 1000000 - timerTimeOut;
					System.out.println(Thread.currentThread().getName() + " took " + timeElapsed + " milliseconds to complete.");
					timer.cancel();
					schedulerThreadsTerminated++;
				}
			};
			timer.schedule(timerTask, timerTimeOut);
		}
	}
	
	/**
	 * Simple message requesting and sending between subsystems.
	 * Scheduler
	 * Sends: ApproachEvent, FloorRequest, ElevatorRequest
	 * Receives: ApproachEvent, ElevatorRequest, ElevatorMonitor
	 */
	public void run() {
    
		//Starts the inactivity timer and performance measurement
		//this.startTime = System.nanoTime();
		resetTimer();

		// TODO: replace with systemActivated
		while (schedulerThreadsTerminated < 2) {
			receiveAndProcessPacket();
		}
		System.out.println(Thread.currentThread().getName() + " terminated");
	}

	public static void main(String[] args) {
		Structure structure = new Structure(10, 2, 1000, 1000);

		ElevatorViewContainer elevatorViewContainer = new ElevatorViewContainer(structure.getNumberOfElevators());
		Presenter presenter = new Presenter();
		presenter.addView(elevatorViewContainer);
		presenter.startGUI();

		Scheduler schedulerClient = new Scheduler(Port.CLIENT_TO_SERVER.getNumber());
		Scheduler schedulerServer = new Scheduler(Port.SERVER_TO_CLIENT.getNumber());

		schedulerClient.setPresenter(presenter);

		for (int i = 0; i < structure.getNumberOfElevators(); i++) {
			schedulerClient.addElevatorMonitor(i + 1);
		}

		try {
			schedulerClient.enableSystem(structure, InetAddress.getLocalHost(), Port.SERVER.getNumber());
			schedulerServer.enableSystem(structure, InetAddress.getLocalHost(), Port.CLIENT.getNumber());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		new Thread(schedulerClient, schedulerClient.getClass().getSimpleName()).start();
		new Thread(schedulerServer, schedulerServer.getClass().getSimpleName()).start();
	}
}
