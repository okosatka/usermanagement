# About project

JDK 17

## How To Run
Build project
```bash
gw clean build
```

Start services
```bash
docker compose -f compose-all.yaml up --build
```
Stop services
```bash
docker compose -f compose-all.yaml down
```

## Links

### API base url
```
http://localhost:8080/api
```

### Prometheus
```
http://localhost:9090
```
### Zipkin
```
http://localhost:9411
```

## Simple example flow

```bash
# Create user (anonymous)
curl -X POST http://localhost:8080/api/users?username=testuser&password=password&email=test%40dot.com

# Get user (actual user)
curl -u "testuser:password" -X GET http://localhost:8080/api/users/testuser

# Get all users (admin)
curl -u "admin:password" -X GET http://localhost:8080/api/admin/users

# Create project (admin)
curl -u "admin:password" -X POST http://localhost:8080/api/projects?projectId=project123&name=New%20Project&owner=testuser

# Get owner projects (anyone authenticated)
curl -u "testuser:password" -X GET http://localhost:8080/api/projects/owners/testuser
```

# API

This API allows users to manage projects and users. Below are examples of how to interact with the API using `curl`.

### Base URL
```
http://localhost:8080/api
```
---

## Users

### Get All Users (Admin Only)

```bash
curl -u "admin:password" -X GET http://localhost:8080/api/admin/users \
     -H "Content-Type: application/json"
```

### Get a Specific User

```bash
curl -u "username:password" -X GET http://localhost:8080/api/users/{username}
```

**Example:**
```bash
curl -u "testuser:password" -X GET http://localhost:8080/api/users/testuser
```

### Create a New User

```bash
curl -X POST http://localhost:8080/api/users?username={username}&password={password}&email={email}
```

**Example:**
```bash
curl -X POST http://localhost:8080/api/users?username=testuser&password=password&email=test%40dot.com
```

### Update a User's Email

```bash
curl -u "username:password" -X PUT http://localhost:8080/api/users/{username}?email={email} 
```

**Example:**
```bash
curl -u "testuser:password" -X PUT http://localhost:8080/api/users/testuser?email=any%40dot.com
```

### Delete a User

```bash
curl -u "username:password" -X DELETE http://localhost:8080/api/users/{username} \
     -H "Content-Type: application/json"
```

**Example:**
```bash
curl -u "testuser:password" -X DELETE http://localhost:8080/api/users/testuser
```

---

## Projects

### Get All Projects

```bash
curl -X GET http://localhost:8080/api/projects \
     -H "Content-Type: application/json"
```

### Get Projects by Owner

```bash
curl -X GET http://localhost:8080/api/projects/owners/{owner} \
     -H "Content-Type: application/json"
```

**Example:**
```bash
curl -X GET http://localhost:8080/api/projects/owners/johndoe \
     -H "Content-Type: application/json"
```

### Get a Specific Project by ID and Owner

```bash
curl -X GET http://localhost:8080/api/projects/{projectId}/ownership/{username} \
     -H "Content-Type: application/json"
```

**Example:**
```bash
curl -X GET http://localhost:8080/api/projects/project123/ownership/johndoe \
     -H "Content-Type: application/json"
```

### Create a New Project

```bash
curl -u "admin:password" -X POST http://localhost:8080/api/projects \
     -H "Content-Type: application/json" \
     -d '{"projectId": "project123", "name": "New Project", "owner": "johndoe"}'
```

---

## Error Handling

For any invalid requests, the API will return an error with an appropriate HTTP status code:

- `404 Not Found`: When a requested resource does not exist.
- `409 Conflict`: When attempting to create a resource that already exists.
