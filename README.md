# Knapsack-Optimizer-service (KOS)
Implement a Knapsack solution

Purpose : To implement a Knapsack solution - Given a set of items, each with a weight and a value, determine the number of each item to include in a collection so that the total weight is less than or equal to a given limit and the total value is as large as possible. It derives its name from the problem faced by someone who is constrained by a fixed-size knapsack and must fill it with the most valuable items.

High level design : User feeds the data to the system which includes the knapsack capacity, weights arrays and values array. The program will fetch the total values worth weight also the weights indices which can be inserted in the knapsack of the mentioned capacity.

Low level design : 

Step 1 - The user sends the data in the body as a POST request to a rest endpoint.
Step 2 - The endpoint will store the data in the in-memory H2 database, with the status as 'submitted' and a uniquely generated taskId.
Step 3 - There is a scheduler job which runs every 1 minute (configurable) to fetch all the records from the database which has a status as 'submitted'.And does two things (step 4 and step 5).
Step 4 - Change the status to 'started' of all the above fetched records and pesrsist to the database.
Step 5 - After step 4 is completed, data from step 3 is processed parallelly, and the knapsack optimizer is called to get the output. Now the status is changed to completed and along with the solution, data is persisted.

There is another endpoint which will receive the taskId as query param and will return the output from the database based on the taskId. 

The in-memory H2 databse has three cloumns namely KOS_TASK(unique taskId), KOS_STATUS(keep track of the status) KOS_SOLUTION(clob field to store the entire json to be sent to the UI)

Tech stack, database and other tools:

Java 8,
Springboot,
Rest Api,
Maven,
Spring data Jpa,
Junit Test,
H2 database,
Dockerfile


