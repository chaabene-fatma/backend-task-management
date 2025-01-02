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

## Docker Instructions

### Building the Docker Image
1. Build the Docker image using Docker Compose:

   ```bash
   docker-compose build
   ```


## Running the Backend
Once the image is built successfully, you can run the backend service with:

```bash
docker-compose up
```

