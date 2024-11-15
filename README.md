
---

# ToDo List API

## Overview

This project provides a RESTful API for managing a ToDo list application. It includes features such as user authentication, task management (CRUD operations), and task categorization using labels. The API is built with Spring Boot and Java 21, allowing users to securely manage their tasks, including creating, viewing, updating, and deleting them.

The API is deployed and available for public use at: [ToDo List API on Render](https://todolistapi-acpu.onrender.com).

## Features

- **User Authentication**: Secure login and logout with JWT tokens.
- **Task Management**: Create, read, update, and delete tasks.
- **Task Categorization**: Assign labels to tasks for better organization.
- **Team Support**: Share tasks with teams and manage teamwork tasks.
- **Task Status**: Track tasks with customizable statuses (e.g., Pending, In Progress, Completed).
- **Priorities**: Set task priorities (e.g., High, Medium, Low).

## Technologies Used

- **Backend Framework**: Spring Boot with Java 21
- **Database**: H2 (For development purposes; can be replaced with MySQL or PostgreSQL)
- **Security**: Spring Security with JWT for authentication
- **API Documentation**: Swagger for easy API interaction
- **Development Tools**: Maven for dependency management
- **Containerization**: Docker for containerizing the application
- **IDE**: Visual Studio Code

## Installation

Follow these steps to set up the project on your local machine.

### Prerequisites

Make sure you have the following installed:
- **Java 21**
- **Docker**
- **Maven**
- **IDE (e.g., Visual Studio Code)**

### Steps to Run the Application with Docker

1. Clone the repository:

   ```bash
   git clone https://github.com/RACHA011/todolistapi.git
   ```

2. Navigate to the project directory:

   ```bash
   cd todolistapi
   ```

3. Build the Docker image:

   ```bash
   docker build -t todolistapi .
   ```

4. Run the Docker container:

   ```bash
   docker run -p 8080:8080 todolistapi
   ```

   The application should now be accessible at `http://localhost:8080`.

### Running Without Docker (Optional)

If you prefer to run the application without Docker, follow the steps below:

1. Clone the repository:

   ```bash
   git clone https://github.com/RACHA011/todolistapi.git
   ```

2. Navigate to the project directory:

   ```bash
   cd todolistapi
   ```

3. Build the project using Maven:

   ```bash
   mvn clean install
   ```

4. Run the application:

   ```bash
   mvn spring-boot:run
   ```

   The application should now be running on `http://localhost:8080`.

## API Documentation

The API follows REST principles and allows users to interact with tasks and authentication securely.

### Authentication Endpoints

- **Login**: `POST /api/v1/auth/login`
  - Login a user and receive a JWT token.
  - Request: `{ "email": "user@example.com", "password": "password123" }`
  - Response: `200 OK`, JWT token returned.

- **Logout**: `POST /api/v1/auth/logout`
  - Logout the user and invalidate the token.
  - Response: `200 OK`, successful logout.

### Task Management Endpoints

- **View All Tasks**: `GET /api/v1/auth/task`
  - Retrieve all tasks for the authenticated user.
  - Response: `200 OK`, List of tasks.

- **Add Task**: `POST /api/v1/auth/task/add`
  - Create a new task.
  - Request: `{ "title": "New Task", "description": "Description", "dueDate": "2024-11-15", "priority": "High", "status": "Pending" }`
  - Response: `200 OK`, Task created.

- **Update Task**: `PUT /api/v1/auth/task/{task_id}/update`
  - Update an existing task.
  - Request: `{ "title": "Updated Task", "description": "Updated description", "dueDate": "2024-11-16", "priority": "Medium", "status": "In Progress" }`
  - Response: `200 OK`, Task updated.

- **Delete Task**: `DELETE /api/v1/auth/task/{task_id}/delete`
  - Delete a task.
  - Response: `200 OK`, Task deleted.

### Swagger UI

To explore and test the API directly, visit the Swagger UI at:

```
http://localhost:8080/swagger-ui.html
```

For the live deployed API on Render, visit:

```
https://todolistapi-acpu.onrender.com/swagger-ui.html
```

## Data Models

### Task

- **id**: Long (Auto-generated)
- **title**: String (Title of the task)
- **description**: String (Description of the task)
- **dueDate**: String (Due date in `YYYY-MM-DD` format)
- **priority**: String (High, Medium, Low)
- **status**: String (Pending, In Progress, Completed)
- **labels**: List<String> (Task labels for categorization)
- **teamProfileId**: Long (Team member ID if it's a teamwork task)

### Authentication

- **email**: String (User email for login)
- **password**: String (User password for authentication)
- **jwtToken**: String (JWT token for authenticated requests)

## Running the Tests

To run the tests for the project, use the following Maven command:

```bash
mvn test
```

## Contributing

Contributions are welcome! If you would like to contribute to this project, please fork the repository, create a new branch, and submit a pull request.

### Steps to contribute:
1. Fork the repository
2. Create a new branch: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -m "Add new feature"`
4. Push to the branch: `git push origin feature/your-feature`
5. Open a pull request.

## Acknowledgements

- **Spring Boot**: For providing the base framework.
- **Swagger**: For API documentation.
- **H2 Database**: For in-memory database support during development.

---
