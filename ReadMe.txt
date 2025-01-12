HOW TO RUN:
Simply run the project from TrainingApplication.java class the db is configured in application.properties file
(Optional: You can Run Docker on same port as connected db)
Then the Implemented controllers will work properly on localhost::8081 (I assigned 8081 port make sure to have that port free) after that run postman and test the application requests there;
HOW TO TEST:
Simply right click src/test/java folder and choose Run 'Tests in 'Java"
after the core java logic testing there is an additional way to test it using the postman and the endpoint that are available in controller classes; simply run postman and enter the endpoint with localhost::8081 at the beginning.