## Running the Project with Docker

This project provides a Docker-based workflow for building and running the Java Spring Boot application using Docker Compose. Below are the project-specific instructions and requirements:

### Project-Specific Docker Requirements
- **Java Version:** Uses `eclipse-temurin:21-jdk` for building and `eclipse-temurin:21-jre` for running (Java 21 is required).
- **Build Tool:** The Gradle wrapper is included and used for building the application inside the container.
- **Non-root User:** The runtime container runs as a non-root user (`appuser`) for enhanced security.
- **JVM Options:** Container-aware memory settings are enabled by default for optimal performance.

### Environment Variables
- No required environment variables are set by default in the Dockerfile or Docker Compose file.
- To provide custom environment variables (e.g., for Spring Boot configuration), create a `.env` file in the `reading_app` directory and uncomment the `env_file` line in `compose.yaml`.

### Build and Run Instructions
1. **Build and Start the Application:**
   ```sh
   docker compose up --build
   ```
   This command will build the Docker image and start the application container.

2. **Accessing the Application:**
   - The application will be available at [http://localhost:8080](http://localhost:8080) by default.

### Ports
- **8080:** The application exposes port 8080 (default for Spring Boot), mapped to the same port on the host.

### Special Configuration
- The Docker Compose file defines a dedicated network (`reading_app_net`) for the service. No external dependencies are configured by default, but you can add them as needed.
- If you need to add environment variables, external services, or change the port, update the `compose.yaml` accordingly.

---

*This section was updated to reflect the current Docker-based setup for building and running the project.*
