# Backend Task Management

## Prerequisites
- Java Development Kit (JDK 21)
- Apache Maven
- Docker and Docker Compose

## Building the Backend
Before building the Docker image, ensure the project is built and the required `.jar` file is generated. Follow these steps:

1. **Clean and Package the Project**
   Run the following command to build the project and generate the `.jar` file in the `target` directory:

   ```bash
   mvn clean package
   ```

2. **Verify the Build Output**
   Ensure the `target/` directory contains the file `backend-task-management-0.0.1-SNAPSHOT.jar` after the Maven build process.

## Running the backend

### Method 1: Running with Docker
1. Build the Docker image using Docker Compose:
   ```bash
   docker build -t backend-task-management .
   ```

2. Once the image is built successfully, you can run the backend service with:

   ```bash
   docker run -p 8080:8080 backend-task-management 
   ```


### Method 2: Running Spring Boot App

1. Run the Application
   After building the project with Maven, you can run the .jar file directly using:

   ```bash
    java -jar target/backend-task-management-0.0.1-SNAPSHOT.jar
   ```

2. Accessing the Backend 
   The application will be available at http://localhost:8080.

## Swagger UI
To view the API documentation and interact with your application through Swagger UI, you can navigate to:

       http://localhost:8080/swagger-ui/index.html#/

Swagger provides a user-friendly interface to test the API endpoints, see the documentation,

## Actuator - Health Check
Spring Boot Actuator is included to provide insights into the application's health and other metrics.
You can check the health of the application by accessing:

      http://localhost:8080/actuator/health

This endpoint will provide information on the health status of your application. 
It will return a status of UP if the application is healthy.

