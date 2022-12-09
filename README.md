# Vending Machine API

This project provides the ability to perform operations on a vending machine. It uses Spring Boot, Maven and Java 11 to create a web service. An API Token should be provided for authorization, at the moment it can be get by registering a new user and signing in. 

The Spring Boot project follows a layered architecture in which there are 3 main layers: Presentation, Service(Business), Persistence Layer. This is followed by a database that stores and performs database operations. 
Below are some of what was implemented in the project
-   Aspect-Oriented Programming is used for the logging logic at each method entry and return join points.
-   All exceptions are handled in a custom exception handler.
-   Swagger is used for API resource documentation.
-   MySQL is used to store all the data. A Docker container is used to run the MySQL database instance.
-   The project includes unit test cases.
-   Java 11 is being used.

# Run Vending Machine API

The project consists of the API and a MYSQL database. To be able to run them both locally please follow the steps below.

### Docker

In the project root folder,  `docker-compose.yml`  is created to be able to run MySQL instance.
Run:  `docker-compose up`

Once the docker container is up we can proceed with the API.

### Spring boot API

-  Make sure you are using java 11 (sdkman is a good help to switch between java versions)
-   `mvn package`
-   `mvn install`
-   `mvn spring-boot:run`

-   The application API should be up and running. You can run  `mvn test`  to run unit tests.

**Please note that  `mvn`  can be replaced with  `./mvnw`  for all the commands**

The project can also be imported into your favorite IDE and execute the run from there (I have used eclipse).

To access swagger documentation:  `http://localhost:8080/swagger-ui.html`

# Test the Vending Machine API
  
After the database docker container and the application are up and running please follow the steps below in order to test. In the project, there's file called `Vending Machine.postman_collection.json` in `PostManCollection` folder, it contains needed Postman Requests to be made for the testing. It can be imported into Postman, and the tests can be performed. Please note that most of the endpoints needs to be provided by authentication token (which can be retrieved by signing in after registration).
  