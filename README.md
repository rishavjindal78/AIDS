AIDS (Autonomous Integrated Deployment)
====

Autonomous Integrated Deployment Software
This software is intended to automate the deployment of build related tasks, for example, A typical scenario could 
consist of below steps -
 1. Take SVN update on machine X
 2. Build pom.xml using maven on machine X
 3. Distribute the war to different machines A, B & C
 4. Restart the Tomcat Services from command Line
 5. Send Email notification for the latest deployment
 
This Software consists of two parts -
1. Server
2. Agent

###Role of Server
1. Server is the central application which provides user interface for all the configuration. 
2. Server schedules the jobs and sends them to the Agents for execution.
3. Agents can be added/deleted to the server dynamically
4. Tasks can be created on the server. Task consists of smaller steps called as taskSteps.
5. Support to run the TaskStep serially or in parallel.
6. View Run history for the individual tasks
7. Schedule the Tasks as per requirements

###Role of Agent
1. Agent executes the actual task step sent by the server, results are pushed back to the server upon completion.
2. Agent can run only limited concurrent tasks at a given point in time, other tasks will have to wait for free slots.
3. All the Task execution related logs are sent back to the server.

