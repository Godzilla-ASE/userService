# User Service

User Service is a backend service that handles user-related operations. 
Including:
- Create, login, logout and delete user
- Update users' profile
- Follow and unfollow a user
- Response with specific user or all users

## Prerequisites

- Java Development Kit (JDK) 8 or higher
- Apache Maven

## Configuration

1. Open the `application.properties` file in the `src/main/resources` directory.
2. Provide the necessary configuration values, such as database connection details.
   Make sure to replace the values with your actual database connection details.

The configuration properties are used for the following purposes:

- spring.datasource.url: Specifies the URL for the MySQL database connection.
- spring.datasource.username and spring.datasource.password: Provide the username and password for the database connection.
- spring.jpa.properties.hibernate.dialect: Sets the Hibernate dialect for MySQL.
- spring.jpa.hibernate.ddl-auto: Determines how Hibernate handles the database schema updates. The update value indicates that Hibernate will automatically update the schema based on the entity mappings.
- server.port: Specifies the port on which the application will run locally.
- Please ensure that you have a MySQL database running with the specified connection details before running the application.

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

## Testing
The testing includes unit tests and integration tests to ensure the quality and functionality of the code. 
The tests are written using the JUnit and Mockito frameworks.

### Unit Tests

Unit tests are written to test the individual components of the application. 
These tests focus on testing the functionality of these components in isolation, without relying on external dependencies.
In our unit testing:
- 89% Line coverage
- Test every interface in controller and methods in userService
- Test success and fail situations

### Integration Tests

Integration tests are written to test the interaction between different components of the application, 
such as testing the RESTful APIs and their integration with the database. 
These tests ensure that the components work correctly together. Make sure to run authServer before running integration test.
In our integration testing:
- Test sending the Http request to authentication server
- Test integration with the database, like creating new users

## Continuous Integration
We utilize SonarQube deployment to achieve continuous integration in our project, ensuring code quality and reliability. SonarQube is a powerful static code analysis tool that helps us identify potential issues, defects, and vulnerabilities in our code, while providing comprehensive code quality metrics.

In our continuous integration workflow, whenever code is committed to the version control repository, SonarQube automatically runs static code analysis and generates detailed reports on code quality and health. This allows us to identify and address potential issues early, ensuring code maintainability and scalability.

By leveraging SonarQube, we gain the following benefits:

- Code Quality Analysis: SonarQube provides comprehensive code quality metrics, including code complexity, code duplication, potential defects, and vulnerabilities.
- Instant Feedback: After each code commit, we can immediately review SonarQube reports to understand any issues in the code and address them promptly.
- Custom Rules: We can configure custom code rules based on project-specific requirements and apply them during static code analysis.
- Continuous Improvement: By periodically reviewing SonarQube reports, we can track the evolution of code quality and continuously improve the quality of our project's code.

## Author

ASE Godzilla group : Han Yang
