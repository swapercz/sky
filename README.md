# UsersApplication

## Overview
The **UsersApplication** is a Spring Boot application written in **Java 21**. It uses **Maven** as the build tool, and the wrapper script (`mvnw`) is attached for convenience. No additional steps are required for execution unless you want to enable optional services such as **Grafana**, **Prometheus**, and **Zipkin**.

## Running the Application

### Default Execution
To start the application, simply run the following command using the Maven wrapper:
```bash
./mvnw spring-boot:run
```
Alternatively, you can directly run the main class:

```
UsersApplication
```

No additional action with compose needed.

### Execution with Additional Services
If you need to start the application along with **Grafana**, **Prometheus**, and **Zipkin**, use Docker Compose with the appropriate profile:
```bash
docker-compose -f compose.yaml --profile with-app up
```


## API Documentation

The application uses **Swagger** for API documentation. Once the application is running, you can access the Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

## Testing the API

In addition to Swagger, if you require more robust testing, a **Postman collection** is attached for your convenience. You can find it in the file `Sky Users.postman_collection.json`.


## Authentication

The application requires **Basic Authentication**. Two in-memory users have been registered for authentication:

- **User**: `user` / `user`
  - This user is intended to be used for **GET operations** (read-only access).

- **Admin**: `admin` / `admin`
  - This user is intended to be used for **data-changing operations** such as POST, PUT, DELETE, and PATCH.


## Profiles

If you wish to enable additional services (Grafana, Prometheus, Zipkin), make sure to activate the `with-app` profile using the Docker Compose command shown above.


## Monitoring and Tracing

When using the `with-app` profile, the following monitoring and tracing tools will be available:
- **Grafana** on `http://localhost:3000`
- **Prometheus** on `http://localhost:9090`
- **Zipkin** on `http://localhost:9411`

