# SYSC-3303-Project

## Description

This is a group project for designing, creating, and simulating a multithreaded elevator system.

## Team (Group 4)

  - [Ryan Dash](https://github.com/ryandash)
  - [Ramit Mahajan](https://github.com/RamitMahajan)
  - [Brady Norton](https://github.com/Bnortron)
  - [Julian Obando Velez](https://github.com/julian-carleton)
  - [Liam Tripp](https://github.com/cyberphoria)

## Instructions

This project requires at least [JDK17](https://www.oracle.com/java/technologies/downloads/) to run. Note that this project is set up as a Maven project. In Eclipse, it requires the [M2Eclipse](https://www.eclipse.org/m2e/) plugin. Most Eclipse downloads already include M2Eclipse, but if your system does not have it, download instructions can be found [here](https://stackoverflow.com/a/13640110).

<details>
  <summary>Downloading a Release / Iteration Code</summary>
    <br>

1. Choose the tag for the iteration. For example v0.1 refers to iteration 1.  
  
![Picture1](https://user-images.githubusercontent.com/71390371/152629966-a56e28e7-1c0d-4dca-a3f3-d64325755f05.png)

2. Go to the Code tab and Download Zip
  
![Picture2](https://user-images.githubusercontent.com/71390371/152629981-84ec3fa3-29d4-42db-82a5-b4ed0a5f4e82.png)

3. Unzip the folder and import the project into the IDE.
4. Proceed to step 9. of "Editing."
  
</details>

<details>
  <summary>Installation</summary>

#### Eclipse:

1. Download the ZIP file for the project. (In GitHub, found under "Code" button).
2. Extract the ZIP file. Remember where you put the extracted folder.
3. Open Eclipse. In the upper left corner, select File -> Import -> Maven -> Existing Maven Project. Click "Next" to continue.
4. [See "Import Maven Projects"] On the new popup screen, for "Root Directory," select the extracted project folder. Ignore the folder within the extracted project folder.

  <details>
  <summary>Show "Import Maven Projects"</summary>

  ![Import Maven Project](https://user-images.githubusercontent.com/61635007/161658503-5c94a77e-a862-4493-b24d-2ecfe9fbe226.png)

  </details>

5. Once the root directory is selected, in Eclipse, activate the "Advanced" dropdown. For the "Name Template" options, select [groupId].[artifactId]-[version].
6. Make sure "Resolve Workplace Projects" is checked under "Advanced."
7. Check the box where the project is. Select "Finish." The project should be added to the Project Explorer in Eclipse.
8. At this point there may be unresolved dependencies. To resolve this, in the Project Explorer, right click the project folder, or "pom.xml." From the context menu that pops up, select Maven -> Update Maven Project.
9. [See "Update Maven Project"]. A popup menu appears. Ensure the project checkbox is selected. Ensure the three checkboxes at the bottom of the popup menu are also checked.

  <details>
  <summary>Show "Update Maven Project"</summary>

  ![Update_Maven_Project](https://user-images.githubusercontent.com/61635007/161658707-fa88dcad-5d5e-4871-abc7-fd34c2e69011.png)

  </details>

10. In the popup menu, select "Finish." This downloads all dependencies from Maven automatically. They are locally stored in the directory "C:\Users\\[your name]\\.m2"
11. You should now be able to run the project.

</details>

<details>
  <summary>Editing</summary>
  <br>

This is for importing the project and its entire branch history.

#### Eclipse:
  
1. Open Eclipse. Open the File menu and select "Import". This will open the "Import" window. From there select "Git"->"Projects from Git" as the import wizard and press Next>
2. From the next window, select "Clone URI" as the repository source and press Next>
3. Enter the URL of the git repository in this window which can be found by pressing the "Code" button and selecting the preferred connection protocol on the project's GitHub page
4. Paste the information obtained from the project page into the window. It may prefill some of the information in the window. Enter any required information
5. Due to an update in GitHub, account authentication with Eclipse via HTTPS might not work. It is not officially supported for security reasons. To overcome that error, follow the guide [here](https://stackoverflow.com/a/68802292)
6. After over coming the error, press Next> and it will show you the branches of the repository, do not make any changes to the default selected branches.
7. It will open the Local Destination window in which you can select the location of the folder where you want clone the repository. Press Next> 
8. In the next window, select "Import as general project" as the wizard from import and press Next>. This will load the project
9. From the project explorer window, right click the project folder and from the popup menu, select "Configure", then "Convert to Maven Project". This will convert the project into a Maven project.

</details>

<details>
  <summary>Testing</summary>

#### In Eclipse:

1. Ensure the project is loaded as a Maven project (instructions contained in Installation if downloaded via ZIP, or in the Editing section if connected to repository via Git)
2. Locate the test directory "src/test/java" in the workspace
3. Right click on the directory and select "Run As" -> "JUnit Test". This runs all the unit tests

Tests: 
- InputFileReaderTest: tests related to reading the JSON input file
- SchedulerTest: tests related to passing data between the systems
- DirectionTest: tests the Direction enum's getDirectionByName function
- ElevatorMotorTest: tests for the proper updating of states in the elevator motor class
- ElevatorSelectionTest: tests selecting idle elevators and tests adding more requests to active elevators using the selection algorithm. ElevatorSelectionTest must be run independently of other tests as it uses multiple threads with ports to test selecting an appropriate elevator and the port are used in previous tests causing the error "Address already in use: bind" to occur
- ElevatorFaultTest: tests the fault-handling behavior of the Elevator for the faults: Doors Interrupted, Doors Stuck, Elevator Interrupted, Elevator Stuck
- RequestQueueTest: tests that the RequestQueue adds ServiceRequests to the correct list and that requests are added and removed in the correct order
- MessageTransferTest: tests that objects are encoded/decoded properly, and that DatagramPackets are transferred between DatagramSockets
- FloorTest: tests that the ArrivalSensor correctly modifies an ApproachEvent
- FloorSubsystemTest: tests that the correct Floor is selected when an ApproachEvent is received
- PresenterTest: tests that presenter updates the view with the proper values and integration with the system
- SimulationTest ensures that the entire simulation, without the GUI, runs to completion multiple times
  
</details>

<details>
  <summary>Running</summary>

#### Description

The program is run as multiple separate programs with the classes Scheduler, ElevatorSubsystem, and FloorSubsystem. The multiple programs can be started manually or automatically. To start it manually, run the main methods of the following classes in order: ElevatorSubsystem, FloorSubsystem and Scheduler. Running them all automatically with a single button press depends on the IDE used. See instructions below for details. 

#### Eclipse

- Set the Run Configuration to run these classes in order: ElevatorSubsystem, FloorSubsystem, and Scheduler.

#### IntelliJ

- As IntelliJ does not allow ordered run configurations, the Multirun plugin is used. 

Multirun Instructions:
1. To install Multirun, click the Setting icon in the top right corner of IntelliJ. Select plugins. 
2. Search for Multirun in the plugins list. If it does not show up, there should be an option to search aftermarket plugins which you can click. 
3. Click the install button.
4. Multirun should now be installed and ready to use.
5. The run option should now be available in IntelliJ's run configurations.

</details>

## Iterations

This section contains information about each of the iteration submissions for this project. If images in any of the iterations look blurry when opened in browser, try zooming in. Alternatively, download and open them. They will appear clearly.

- ## Iteration 1

  <details>
    <summary>Display</summary>

  ### Description

  This iteration of the project implements a multi-threaded system where all active subsystems, the Elevator Subsystem, the Floor Subsystem, and the Scheduler, act as both [Consumers and Producers](https://en.wikipedia.org/wiki/Producer%E2%80%93consumer_problem). Two buffers exist to achieve this, one for message passing between Scheduler and Elevator Subsystem and another for between Floor Subsystem and Scheduler. 

  ### Contributions

  | Member | Coding | Documentation | Misc
  | ------ | ------ | ------------- | ----
  | Ryan Dash | InputFileReader, JSON files, JSON File to data structure conversion, Message Transfer Implementation and Bug Fixes | Project Requirements Summary, UML Diagram Contributions | Code Review
  | Ramit Mahajan | Data Structure abstraction for the Request Systems / Subsystems | README Editing Instructions |
  | Brady Norton | Message transfer tests, InputFileReaderTest | README Testing + Installation Instructions, UML Sequence Diagram | Code Review
  | Julian Obando Velez | Message Transfer, Bounded Buffer, Bounded Buffer Test | UML Diagram Feedback, GitHub Releases | Code Review
  | Liam Tripp | Project Skeleton, Data Structures, InputFileReader, Direction, Message Transfer, Unit Testing | README Design, Early Design Diagrams, Design Document, Requirements Analysis | Discord Server, Google Drive, GitHub repo, Code reviews, Group lead, Instruction documents + videos 
  
  #### UML Class Diagram
  ![UML Class Diagram](https://user-images.githubusercontent.com/61635007/152667157-df45fbf8-6c48-430f-b47d-c82156e23872.png)

  #### UML Sequence Diagram

  ![UML Sequence Diagram](https://user-images.githubusercontent.com/61635007/154827908-c74e2fc4-68de-45b6-9b32-b8b85e857fe9.png)
  
  </details>

- ## Iteration 2
  <details>
    <summary>Display</summary>

  ### Description

  This iteration implements Elevator Movement and the order in which Elevators serve ServiceRequests. Note that a bug occurs when the FloorSubsystem runs out of Requests to send, as the other Runnable systems are left waiting for FloorSubsystem to send something it doesn't have. A solution could be to implement one buffer instead of two.

  <details>
    <summary>Show Long Description</summary>

  - The ElevatorSubsystem acts as an ElevatorController. It acts as intermediary between Elevators and the Scheduler. It also selects which elevator takes a request. It sends ApproachEvents and receives ElevatorRequests and ApproachEvents. 

  - A SystemEvent class was created as a parent for all messages. This is because each message has a Thread from which they originated and a Time at which they occurred.

  - The Scheduler is an intermediary between the ElevatorSubsystem and the FloorSubsystem. It can receive any type of SystemEvent.

  - The FloorSubsystem sends the ElevatorRequests obtained from the input file. It also receives ElevatorRequests back and sends ApproachEvents. All of which are sent through the scheduler.   

  - The elevator receives new requests from the elevator Subsystem to perform actions on other parts of the elevator. With the current implementation a list of requests is stored in the elevator for any type on new request. Requests that are stored in the elevator are sent to the FloorQueue, elevator motor, or handled by the elevator if they involve the elevator's status. Once a request is complete, the elevator uses the elevatorSubsystem to send information to the floorSubsystem and necessary information involving the request.

   - ApproachEvent is a SystemEvent with a true/false value indicating whether an Elevator should stop at a Floor. The ApproachEvents are passed from Elevator to FloorSubsystem each time an Elevator is about to stop at a FLoor. An ArrivalSensor in Floor confirms whether the elevator should stop. The ApproachEvent is then sent back to the Elevator, which proceeds depending on whether the ApproachEvent allows it to stop.

  - The Elevator Motor simulates movement and keeps track of the Elevator's direction and state of movement.
  
  - The FloorsQueue is the data structure used to store the floors to visit by an elevator. It uses two priority queues, one in ascending order and one in descending order for the floors to visit in the corresponding direction. Also, it has an extra queue, which temporarily saves the floors that were missed when going in a direction and swaps them to this direction queue when this queue has visited all of its floors.

  - The ElevatorSelectAlgorithm is an algorithm to select the best elevator to perform a new elevator request. The current implementation first checks for idle elevators and makes them perform requests. If all elevators are active then it will prioritize elevators based on expected completion of each elevator's queue time, the direction that the elevator is traveling, and if the new request is in between the current floor and destination floor of each elevator.
    
  - The ElevatorServiceAlgorithm is an algorithm to perform appropriate actions for each type of request that the elevator receives.
    
  </details>

  ### Contributions

  | Member | Coding | Documentation | Misc 
  | ------ | ------ | ------------- | ----
  | Ryan Dash | ElevatorSelectAlgorithm, ElevatorServiceAlgorithm | ElevatorServiceAlgorithm State Machine Diagram, UML Class Diagram, README | Code Review, Design Consultation
  | Ramit Mahajan | Arrival Sensor | UML Class Diagram, README | Code Review
  | Brady Norton | MovementState, ElevatorMotor, Elevator Properties, Elevator Movement | Elevator Movement State Machine Diagram, README| Code Review
  | Julian Obando Velez | FloorsQueue, FloorsQueueTest, ElevatorMotorTest | GitHub Release, README | Code Review
  | Liam Tripp | MovementState, ApproachEvent, SystemEvent, ApproachEvent Integration with ElevatorMovement | UML Class Diagram, Rough ElevatorMovement State Machine Diagram, README | Requirements Analysis, System Design, Delegating Tasks, Code Review
  
  #### UML Class Diagram
  ![image](https://user-images.githubusercontent.com/56605453/154828075-8269786d-84cd-4a64-8c7a-4cdaa294ca0e.png)

  #### UML State Machine Diagram for Service Algorithm

  ![Elevator_Service_Algorithm drawio](https://user-images.githubusercontent.com/56605453/154823993-ff5cb3f7-f500-4696-9f78-be6f628d8068.png)

  #### UML State Machine Diagram for Movement Algorithm

  ![Iteration_2_-_Elevator_State_Machine](https://user-images.githubusercontent.com/56605453/154823989-936bc6f0-0ebe-435c-99ae-941525b7de60.png)

  </details>

- ## Iteration 3
  <details>
    <summary>Display</summary>

  ### Description

  In this iteration, UDP data transfer between the systems is implemented. The simulation can now run multiple elevators.

    #### Major Changes
    - Simulation works for multiple elevators
    - Elevator Selection Algorithm: ElevatorSubsystem chooses which elevator serves a given request
    - Fixed BoundedBuffer glitch from Iteration 2, changed to UnboundedBuffer
    - Message Transfer: Use Client/Server scheme as seen in Assignments 2 and 3
    - UDP messages to transfer data
    - Add Doors class to Elevators
    - Integrated FloorsQueue with Elevator Movement Algorithm
    - More unit tests for FloorsQueue, Floors and FloorSubsystem, 
    - Finalized Elevator and ElevatorMotor properties update
    <br>
  
  <details>
    <summary>Show Long Description</summary>

    * Added serviceDirection to Elevator to distinguish between the direction the Elevator is moving (i.e. ElevatorMotor's direction) and what direction the Elevator is servicing requests in. 

    * Created Client/Server scheme like Assignment 2 and 3 of this class. MessageTransfer class holds DatagramSockets and a Queue of datagramPackets. 

    * The Client and IntermediateHost class each have a MessageTransfer. ElevatorSubsystem and FloorSubsystem, and Scheduler interact with the two classes each. 

    * For UDP data transfer, there are two Scheduler threads, one for sending messages from FloorSubsystem to ElevatorSubsytem, and another for vice-versa. Both FloorSubsystem and ElevatorSubsystem are still threads. Elevators are also threads.

    * The Client systems either request data or send data. FloorSubsystem's client requests and receives data from ElevatorToFloorHost. It sends data to FloorToElevatorHost. ElevatorSubystem requests and receives data from FloorToElevatorHost. It sends data to ElevatorToFloorHost.

    * To see output in the console, or to see how many times the elevator moves, search "moved"

    * MessageTransfer is the class that wraps the methods to handle packets for UDP communication, such as sending, receiving, queueing, decoding/encoding and printing the results of each message transfer.  

    * To solve the deadlock issues from Iteration 2, sending and receiving with the BoundedBuffer was changed from a busy-waiting scheme to an infinite loop checking a conditional statement. Although this prevents deadlock and allows the program to run successfully, it also causes considerable lag. 

    * To fix size issues with BoundedBuffer, an unbounded list was implemented - ConcurrentLinkedDeque, essentially an UnboundedBuffer.
  
    * Added ElevatorMonitor to Scheduler to allow the scheduler to quickly access all elevator data. An ElevatorMonitor is stored for each elevator in the scheduler.
  
    * Each Elevator monitor is updated by the elevator subsystem after a request that changes the properties and contents of the elevator has completed.

  </details>

  ### Contributions

  | Member | Coding | Documentation | Misc 
  | ------ | ------ | ------------- | ----
  | Ryan Dash | Elevator Selection Algorithm. Client, FloorSubsystem, ElevatorSubsystem Implementation, ElevatorSelectionTest | Diagram Review | Code review
  | Ramit Mahajan | Integrating Doors class | UML Diagram, README | Code review
  | Brady Norton | Elevator Movement Algorithm, Elevator Movement Properties Modification, Integrating Floors Queue into Movement, Movement Tests | Movement Design | Code review
  | Julian Obando Velez | Message Encoding/Decoding, Client for UDP, JUnit testing | Diagram Review | TA contact, Code review
  | Liam Tripp | Elevator Movement + FloorsQueue updates and Integration, Message passing bug fix, UnboundedBuffer, ApproachEvent Integration, MessageTransfer, Client-Host outline, Scheduler-Host Integration, FloorTest, RequestQueueTest | Design, Work Breakdown Structure, Dependency Diagram, UML Sequence Diagram, UML CLass Diagram | Code review

  ### Diagrams
   
  #### UML Class Diagram
  ![image](https://user-images.githubusercontent.com/61635007/158045772-5fb02a0e-ba15-4c39-bc07-6cc19efa0b91.png)
 
  #### Sequence Diagram: UDP DataTransfer of Data from FloorSubsystem to ElevatorSubsystem

  ![Iteration_3_DataTransferFloorToElevator_Sequence](https://user-images.githubusercontent.com/61635007/158044089-0322f422-9c0a-46de-a1d9-f903cd41e765.png)

  </details>

- ## Iteration 4
  <details>
    <summary>Display</summary>

  ### Description

  In this iteration, fault detection and handling is implemented. The simulation now shows faults for elevators.

  #### Major Changes
  - Added configuration files to automate running multiple main methods with a single button in IntelliJ
  - Introduced Fault Handling for Elevator
  - Removed BoundedBuffer, BoundedBufferTest
  - Fixed elevator selection algorithm to meet requirements
  <br>

  <details>
    <summary>Show Long Description</summary>
    <br>
  
    * Faults: There are four different types of Faults. It is assumed only one can occur at a time. All are hard faults except DOORS_INTERRUPTED, which is a soft fault. For the hard faults, the Elevator shuts down. For the soft faults, the Elevator is corrected so that it may continue. It is assumed that opening the doors is uninterruptable and that Doors may only be opened or closed when the Elevator is stopped. There is no fault handling for when a packet is lost, as that was not in the Iteration requirements itself. 
      - ELEVATOR_STUCK occurs when an Elevator gets stuck between Floors (when Moving) or gets stuck at a Floor (when stopped). 
      - ARRIVAL_SENSOR_FAIL occurs when the ArrivalSensor at a Floor fails to return an ApproachEvent to Scheduler before Elevator's movement timer has expired.
      - DOORS_STUCK occurs when the Doors malfunction while opening or closing.
      - DOORS_INTERRUPTED occurs when the Doors are interrupted while closing. 
    * Faults are tested using the ElevatorFaultTest file.
    * Added multirun configuration as well as FloorSubsystem, ElevatorSubsystem, and Scheduler configurations to allow multiple main methods to be run at once without needing to run each main method one at a time. This allows for fast testing in IntelliJ. This is not required to run multiple main methods in Eclipse as Eclipse already has this functionality built in.
    * Moved Elevator Selection to Scheduler and reworked IntermediateHost to allow for selection of elevators to work properly
    * Note that there is currently an unhandled case where an Elevator is at floor 1 and moving to floor 3. If it receives an request to move to floor 2 just before it is about to pass floor 2, it might not have enough time to stop or send and receive an approachEvent. This problem has yet to be dealt with.
  </details>

  ### Contributions

  | Member | Coding | Documentation | Misc 
  | ------ | ------ | ------------- | ----
  | Ryan Dash | Moved elevator selection to Scheduler, Reworked IntermediateHost for Elevator Selection, Improved Elevator Monitors | Updating README | Code Review
  | Ramit Mahajan | Doors Upgrade, Doors State Changes in Elevator | UML Class Diagram | Code Review 
  | Brady Norton | ArrivalSensor Integration, ApproachEvent Changes | README Contribution | Code Review, Some Fault Type Ideas
  | Julian Obando Velez | | Timing Diagrams | Code Review
  | Liam Tripp | ElevatorFaultTest, Fault enum, Elevator Faults, Elevator Movement Tests, changed RequestQueue from PriorityQueue to TreeSet, Improved Console Output Statements, Movement bug fixes | Work Breakdown Structure, Updated Movement State Machine Diagram, Updating README | Code Review

  ### Diagrams

  #### UML Class Diagram

  ![UMLClassDiagram](https://user-images.githubusercontent.com/61635007/160321686-72ed3f7e-c35d-4d6e-a65b-0a8bcfc80e01.png)

  #### UML State Machine Diagram
  - Elevator Movement (With Faults)
  ![ElevatorMovement](https://user-images.githubusercontent.com/61635007/160426651-d9931d82-27a4-408d-95ab-5f08ccd2b4c3.png)
  
  #### Timing Diagrams

  - Arrival Sensor Fault
  ![ArrivalSensorFault](https://user-images.githubusercontent.com/71390371/160315145-06c438b2-cb96-4d46-9060-d0d52dbae82b.PNG)

  - Elevator Stuck Fault
  ![ElevatorStuckFault](https://user-images.githubusercontent.com/71390371/160318124-d13e65a2-c7a1-47b4-abfb-22ea892e0bb2.PNG)
  
  - Door Stuck Fault
  ![DoorFault](https://user-images.githubusercontent.com/71390371/160315213-693b2eb4-a16a-410b-8327-489baa8ecb12.PNG)

  </details>

- ## Iteration 5
  <details>
    <summary>Display</summary>

  ### Description

  In this iteration, a GUI was implemented to display Elevator information in real time. Measurements were also done to determine the performance of the Scheduler. Methods to initialize and terminate the system were also added. Iteration and general requirements not met in previous iterations were addressed. 

  <details>
    <summary>Show Long Description</summary>
    <br>
    
    * GUI Design Pattern: The design pattern that was selected is the [Model-Presenter-View](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93presenter) pattern, with the Scheduler for FloorSubsystemToElevatorSubsystem acting as the Model. The Presenter is static in Scheduler, so it's shared by both. ElevatorView is the Panel for displaying each elevator while ElevatorViewContainer contains all the ElevatorViews. Presenter updates an ElevatorView when it's passed an ElevatorMonitor from Scheduler.
    <br>
    
    ![GUI](https://user-images.githubusercontent.com/61635007/163075152-23db6387-42a7-49d9-8973-f9499136c20e.png)    

    * Changes to faults: As seen above, the window for the Fault buttons are separate from the Elevator window. This is because there was not enough time to add the buttons directly to each ElevatorView. There were also concerns about data concurrency between the ElevatorView and Elevator if it the buttons to trigger faults were in the Scheduler. The Fault window was generated in the ElevatorSubsystem accordingly.
   The two door faults were reduced to one, as seen below. Soft faults are handled by acknowledging of the fault in the system and clearing it, so that the system can continue its operation. Hard faults are handled by shutting down the elevator altogether and emptying out its requests queue.

      - ELEVATOR_STUCK: hard fault that occurs when an Elevator gets stuck between Floors (when Moving) or gets stuck at a Floor (when stopped). Triggered by pressing an "Elevator Stuck" button in the GUI.
      - ARRIVAL_SENSOR_FAIL: hard fault that occurs when the ArrivalSensor at a Floor fails to return an ApproachEvent to Scheduler before Elevator's movement timer has expired.
      - DOORS_STUCK: soft fault that occurs when the Doors malfunction while opening or closing. Triggered by pushing a "Door Stuck" button in the GUI.

    * Simulation Initialization and Termination: The simulation is initialized using information contained in the Structure class. ElevatorSubsystem and FloorSubsystem are initialized and wait for the Scheduler to pass them a Structure. The Structure is initialized in Scheduler's main method. Each of the two Scheduler threads, one for passing information between ElevatorSubsystem and FloorSubsystem, the other vice-versa, pass Structure to FloorSubsystem and ElevatorSubsystem, respectively. 

   * Simulation Termination: Introduced conditions to terminate the Threads of the Simulation. This was done with a SystemStatus class for Scheduler, ElevatorSubsystem, FloorSubsystem, and each of the Elevators. The termination condition of the threads is when SystemStatus.activated() is false, except for Scheduler, which requires both Scheduler threads to be inactive. A Scheduler's termination is achieved by its Timer expiring. Each Scheduler sends a termination message to the System it communicates with and then terminates itself. The systems are then terminated by receiving the message, which indicates to the SystemStatus that the class’s thread should end.
    
  </details>

  ### Contributions

  | Member | Coding | Documentation | Misc 
  | ------ | ------ | ------------- | ----
  | Ryan Dash | Fix ElevatorSelectionTest, Fix elevator door status updating incorrectly, Unimplemented: (Many-to-two Elevator-to-Scheduler, Elevator and Floor Buttons, Faults in inputs.json, Elevator Action Requests for Door and Lamp) | UML Class Diagram | Brainstorming, Code Review
  | Ramit Mahajan | Updated ElevatorMotor | UML Class Diagram | Code Review
  | Brady Norton | Updating ElevatorMonitor Properties, Added Current Request to GUI, (Unfinished) ArrivalSensor Integration Testing, Fixing FloorSubsystem and Floor Tests | README | Code Review
  | Julian Obando Velez | Fault Injection GUI, Fault Handling, Implemented Performance Instrumentation | Final Project Presentation, Video Recording, Performance Testing README, Faults README | Scheduler Performance Testing and Measurements, Brainstorming, Code Review
  | Liam Tripp | Presenter, ElevatorView, ElevatorViewContainer, RequestQueue with ServiceRequests, System Initialization and Termination, SimulationTest, Refactor Elevator, Refactor Scheduler (Unfinished) Give Elevator an ElevatorMonitor | UML Class Diagram, README Reflection, Iteration 5 Requirements Analysis and Work Breakdown Structure, System Design README Installation instructions, README, Final Project Presentation | Brainstorming, Code Review
  
  ### Diagrams

  #### UML Class Diagram

  ![UML Class Diagram](https://user-images.githubusercontent.com/61635007/163095931-c9f438ef-46c2-4290-9ba9-9e798060d626.png)

  #### UML State Machine Diagram
  - Elevator Movement (With faults)
  ![Elevator Movement State Machine Diagram](https://user-images.githubusercontent.com/61635007/163073011-82bdddf8-4c09-477f-abd6-da9f8a81f000.png)

  <br>

  <details>
    <summary>Reflection</summary>
    <br>

    This project is mostly a success as it meets almost all of the iteration requirements and most of the general requirements. 

    ### Successes

    The fundamental requirements for each iteration were prioritized and completed by the project deadline.

    The UML Class Diagram is the most complete diagram in the project. Virtually all methods and classes have Javadocs and consistent formatting. The commit history on GitHub is easy to read due to established contribution standards. The README also has a strong design. Its contents illustrate the visual and written communication skills of the team members. The RequestQueue class in particular well-design and tested.

    ### Areas for Improvement

    #### Design

    The Elevator has too much responsibility. As discussed in [#184](/../../issues/184), one solution was putting a RequestQueue for each Elevator in the Scheduler. That could increase Scheduler's awareness of each Elevator's current and future state. It would also solve the data concurrency problem between the Elevators and ElevatorViews and be more faithful to the general requirements.

    A state machine pattern for the Elevator was not implemented due to the system designers being busy with other parts of the project. Increased collaboration and shared responsibility for the design amongst group members could have helped alleviate pressure on designers.

    The ArrivalSensor was not properly integrated into the simulation. There were also bugs not addressed in time for iteration submission, as seen in [#43](/../../issues/43). Finally, the GUI for the Elevator lacks buttons to trigger the ELEVATOR_STUCK and ARRIVAL_SENSOR_FAIL faults. Better time management and completing the project objectives at least a day before the deadline could have left time to address these unresolved issues.

    #### Team

    A consistent problem throughout the project was team members not completing coding work until the day of the deadline. This could be solved by members being proactive and engaged with the project instead of passive, or by more deadlines being set. The deadlines would require more involvement from the team during the design phase. Proactive members would allow for ongoing development and issues to be addressed earlier rather than later.

    There was also a problem where proactive members did more work than passive members. Passive members did work close to the iteration submission dates where it was often too late to make major design decisions. This was often due to a lack of set deadlines. An attempt to solve the issue of members crunching before the deadline was made by creating Work Breakdown Structures (WBS) starting from Iteration 2, as seen in [#54](/../../issues/54), [#75](/../../issues/75), [#105](/../../issues/105), and [#151](/../../issues/151). Most of the work to be done for the project was laid out in these documents. It was partially effective as it increased the visibility of the work to be done. However, there was a lack of feedback and discussion around these WBSs. It was only partially effective in prompting team members to self-assign work and complete it as soon as possible in the way it was intended. Increasing the involvement of team members in the project could remedy this issue.

  </details>
  
  <details>
    <summary>Performance Testing</summary>
    <br>

    #### Testing Description

    The performance of the system is measured based on the time that the scheduler takes to handle all the requests that it receives from the input file. This was implemented by saving the start time and end time, and then comparing them to each other. The start time is measured as soon as the scheduler system is started, while the end time is recorded when the scheduler handles the last request. 

    However, knowing the last request is not trivial, so it was necessary to implement an inactivity timer. This timer checks for inactivity in the scheduler to determine when it has finished. Every time the scheduler does work it resets the timer, however, if the timer reaches a time out time it assumes that the scheduler is finished and records this time as the end time. Finally, the total performance time is calculated by subtracting the start time and timeout time from the end time.     

    Elapsed time=end time-start time-timeout

    #### Where were the measuring instruments placed?

    - Start time: Measured just before the system goes live. 
    - End time: Measured as soon as the timer goes off.
    - Reset: Every time the scheduler finishes a task. 

    #### Measurements

    The system was measured using two of these timers, one timer per thread communicating with the elevator subsystem and floor subsystem. The longer measurement is used for calculations since this is the one that reflects the actual last activity of the scheduler. And the system was not inputted with fault during these measurements.

    Also, the measurements were taken for the inputs of: 

    1. 2000 ms time between floors 
    1. 500 ms time to open or close doors
    1. Four elevators
    1. 20 floors

    #### Calculations:

    The calculations were made using Excel MS. 

    - Mean was calculated using =AVERAGE() function
    - Standard Deviation was calculated using =STDEV.S()
    - Confidential interval value for 95% was calculated using the formula: 

          mean±(std.deviation*z_(95%))/(√(# samples)),   where  z_(95%) = 1.96 (constant value)     
    - Time to process a request = Total elapsed time / 17 requests 

    #### Measurements

    |Trial #|Elapsed Time msecs|
    | :- | :- |
    |1|82216|
    |2|82212|
    |3|82217|
    |4|82208|
    |5|82226|
    |6|82242|
    |7|82215|
    |8|82221|
    |9|82237|
    |10|82231|
    |11|82219|
    |12|82254|

    #### Results

    |Mean|82225|
    | :- | -: |
    |Std. Deviation|14|
    |Interval Value (95%)|8|

    - The total time it takes to process all request is (82225 +- 8) ms, with 95% confidence.
    - The time it takes to process a request is (4836 +- 0.47) ms, with 95% confidence.
  
  </details>
</details>
