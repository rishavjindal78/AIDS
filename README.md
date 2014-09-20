AIDS
====

Autonomous Integrated Deployment Software


Consists of two parts -

1. Server
2. Agent

###Role of Server
1. Server is the central application which provides user interface for all the configuration. 
2. Server schedules the jobs and sends them to the Agents for execution.
3. Agents can be added/deleted to the server dynamically
4. Tasks can be created on the server. Task consists of smaller steps called as taskSteps.
5. Support to run the TaskStep serially or in parallel.

###Role of Agent
1. Agent executes the actual task step sent by the server, results are pushed back to the server upon completion.
2. Agent can run only limited concurrent tasks at a given point in time, other tasks will have to wait for free slots.
3. All the Task execution related logs are sent back to the server.

