# User Service

User Service is a backend service that handles user-related operations. 
Including 

## Prerequisites

- Java Development Kit (JDK) 8 or higher
- Apache Maven

## Configuration

1. Open the `application.properties` file in the `src/main/resources` directory.
2. Provide the necessary configuration values, such as database connection details.

## Usage

1. Start the User Service by running the following command:

```bash
./mvnw spring-boot:run
```


2. The service will be accessible at `http://localhost:8080`.

## API Endpoints

The User Service provides the following API endpoints:

- `GET /users`: Get all users.
- `GET /users/{id}`: Get a user by ID.
- `GET /users/name/{username}`: Get a user by Username.
- `POST /users`: Create a new user.
- `PUT /users/{id}`: Update a user.
- `DELETE /users/{id}`: Delete a user.
- `POST /users/login`: Login a user.
- `DELETE /users/logout/{userId}`: Logout a user.
- `POST /users/{userId}/follow/{followedId}`: Follow a user.
- `PUT /users/{userId}/follow/{followedId}`: Unfollow a user.

## Authentication

The User Service uses authentication service. To authenticate a request, include the `Authorization` header with the token.

## Author

ASE Godzilla group : Han Yang
