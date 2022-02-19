# SYSC-3303-Project

## Description

This is a group project for designing, creating, and simulating an elevator system.

## Team (Group 4)

  - [Ryan Dash](https://github.com/ryandash)
  - [Ramit Mahajan](https://github.com/RamitMahajan)
  - [Brady Norton](https://github.com/Bnortron)
  - [Julian Obando Velez](https://github.com/julian-carleton)
  - [Liam Tripp](https://github.com/cyberphoria)

## Instructions

Note that this project is set up as a Maven project.

<details>
  <summary>Downloading a Release / Iteration Code</summary>

1. Choose the tag for the iteration. For example v0.1 refers to iteration 1.  
  
![Picture1](https://user-images.githubusercontent.com/71390371/152629966-a56e28e7-1c0d-4dca-a3f3-d64325755f05.png)

2. Go to the Code tab and Download Zip
  
![Picture2](https://user-images.githubusercontent.com/71390371/152629981-84ec3fa3-29d4-42db-82a5-b4ed0a5f4e82.png)

3. Unzip the folder and import the project into the IDE.
  
</details>

<details>
  <summary>Installation</summary>

1.	Download and extract the ZIP folder
2. 	Open up Eclipse and select file
3. 	Click "Open Projects from File System" and select the project folder
4. 	Select Finish and then build the project
5. 	To begin the simulation, navigate to the systemwide directory
6. 	src -> main -> java -> systemwide
7. 	Run Structure.java

</details>

<details>
  <summary>Editing</summary>

#### In Eclipse:
  
1. Open the File menu and select "Import". This will open the "Import" window. From there select "Git"->"Projects from Git" as the import wizard and press Next>
2. From the next window, select "Clone URI" as the repository source and press Next>
3. Enter the URL of the git repository in this window which can be found by pressing the "Code" button and selecting the preferred connection protocol on the project's GitHub page
4. Paste the information obtained from the project page into the window. It may prefill some of the information in the window. Enter any required information
5. Due to an update in GitHub, account authentication with Eclipse via HTTPS might not work. It is not offically supported for security reasons. To overcome that error, follow the guide [here](https://stackoverflow.com/a/68802292)
6. After over coming the erorr, press Next> and it will show you the branches of the repository, do not make any changes to the default selected branches.
7. It will open the Local Destination window in which you can select the location of the folder where you want clone the repository. Press Next> 
8. In the next window, Select "Import as general project" as the wizard from import and press Next>. This will load the project
9. From the project explorer window, right click the project folder and from the popup menu, select "Configure", then "Convert to Maven Project". This will convert the project into a Maven project

</details>

<details>
  <summary>Testing</summary>

#### In Eclipse:

1. Ensure the project is loaded as a Maven project (instructions contained in Editing section)
2. Locate the test directory in the workspace
3. Right click on the directory and select "Run As" -> "JUnit Test". This runs all the unit tests
4. InputReaderTest.java does tests related to reading the json input file
5. SchedulerTest.java does tests related to passing data between the systems
6. DirectionTest does tests related to the Direction enum search function
7. BoundedBufferTest does tests related tothe BoundedBuffer methods for Thread-Safe messaging

</details>

## Iterations

- ## Iteration 1

  <details>
    <summary>Display</summary>

  ### Description

  This iteration of the project implements a multi-threaded system where all active subsystems, the Elevator Subsystem, the Floor Subsystem, and the Scheduler, act as both [Consumers and Producers](https://en.wikipedia.org/wiki/Producer%E2%80%93consumer_problem). Two buffers exist to achieve this, one for message passing between Scheduler and Elevator Subsystem and another for between Floor Subsystem and Scheduler. 

  ### Contributions

  | Member | Coding | Documentation | Misc
  | ------ | ------ | ------------- |----
  | Ryan Dash | InputFileReader, JSON files, JSON File to data structure conversion, Message Transfer Implementation and Bug Fixes | Project Requirements Summary, UML Diagram Contributions | Code Review
  | Ramit Mahajan | Data Structure abstraction for the Request Systems / Subsystems | README Editing Instructions |
  | Brady Norton | Message transfer tests, InputFileReaderTest | README Testing + Installation Instructions, UML Sequence Diagram | Code Review
  | Julian Obando Velez | Message Transfer, Bounded Buffer, Bounded Buffer Test | UML Diagram Feedback, GitHub Releases  | Code Review
  | Liam Tripp | Project Skeleton, Data Structures, InputFileReader, Direction, Message Transfer, Unit Testing | README Design, Early Design Diagrams, Design Document, Requirements Analysis | Discord Server, Google Drive, GitHub repo, Code reviews, Group lead, Instruction documents + videos 
  
  #### UML Class Diagram
  ![UML Class Diagram](https://user-images.githubusercontent.com/61635007/152667157-df45fbf8-6c48-430f-b47d-c82156e23872.png)

  #### UML Sequence Diagram

  ![UML Sequence Diagram](https://user-images.githubusercontent.com/61635007/152667164-26f422be-fdd9-4bfb-81eb-9de66fbc9595.png)

  </details>

- ## Iteration 2
  <details>
    <summary>Display</summary>

  ### Description

  This iteration... 

  The ElevatorSubsystem acts as an ElevatorController. It acts as intermediary between Elevators and the Scheduler. It also selects which Elevator takes a request. It sends ApproachEvents and receives ElevatorRequests and ApproachEvents. 

  A SystemEvent class was created as a parent for all messages. This is because each message has a Thread from which they originated and a Time at which they occurred.

  The Scheduler is an intermediary between the ElevatorSubsystem and the FloorSubsystem. It can receive any type of SystemEvent.

  [FloorSubsystem Description].

  [ApproachEvent Description]

  [Elevator Description]

  [ElevatorMotor Description]

  ### Contributions

  | Member | Coding | Documentation | Misc 
  |---------------------------------------------------|------------------------------------------------| ------------- |----
  | Ryan Dash | ElevatorSelectAlgorithm, ElevatorServiceAlgorithm | ElevatorServiceAlgorithm State Machine Diagram | Code Review, Design Consultation
  | Ramit Mahajan | Arrival Sensor | |
  | Brady Norton | MovementState, ElevatorMotor, Elevator Properties, Elevator Movement | Elevator Movement State Machine Diagram |
  | Julian Obando Velez | FloorsQueue, FloorsQueueTest, ElevatorMotorTest | | | 
  | Liam Tripp | MovementState, Elevator, ApproachEvent, SystemEvent |  | Requirements Analysis, System Design, Delegating Tasks, Code Review

  </details>
