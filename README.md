# Ktor DDD Example

This is a showcase of @antonarhipov's [ktor-ddd-example](https://github.com/antonarhipov/ktor-ddd-example) project
with a full persistence model impelemented using ExoQuery. A docker-based Postgres database
is included if you want to use a fully persistent database instance (an embedded Postgres database is used by default).

Additionally, a `example_calls.http` file is included so you can easily try out the API endpoints in IntelliJ.

In the domain of this project there are two main aggregates: `Customer` and `Reminder`. The `Customer` aggregate
is more complex so it requires a separate persistence model with DAO classes `CustomerRow`, `NoteRow` and `ContactRow`
which assist in database insertion and retrieval. The `Reminder` aggregate is simpler and does not require
separate persistence classes.

## Technologies Used

- Kotlin
- ExoQuery (Persistence layer)
- Ktor (Web framework)
- kotlinx.serialization (JSON serialization)
- JUnit (Testing)

## Project Structure

The project follows DDD principles with a clear separation of domains:

```
src/
├── main/
│   └── kotlin/
│       ├── domain/
│       │   ├── customer/         # Customer domain
│       │   │   ├── Customer.kt   # Customer entity and value objects
│       │   │   ├── CustomerRepository.kt
│       │   │   ├── CustomerService.kt
│       │   │   ├── CustomerRoutes.kt
|       |   |   └── DAO.kt        # DAO for customer operations
│       │   └── reminder/         # Reminder domain
│       │       ├── Reminder.kt   # Reminder entity
│       │       ├── ReminderRepository.kt
│       │       ├── ReminderService.kt
│       │       └── ReminderRoutes.kt
│       ├── events/              # Domain events
│       ├── serialization/       # Custom serializers
│       ├── Application.kt       # Application entry point
│       └── Plugins.kt          # Ktor plugins configuration
└── test/
    └── kotlin/
        └── ApplicationTest.kt   # Integration tests
```

## API Endpoints

See the example_calls.http file for an easy way to test out the API endpoints in IntelliJ.

### Customer Endpoints

- `POST /customers` - Create a new customer
  ```json
  {
    "name": "John Doe"
  }
  ```

- `GET /customers/{id}` - Get customer by ID

- `POST /customers/{id}/contacts` - Add a contact to customer
  ```json
  {
    "name": "Jane Doe",
    "email": "jane@example.com",
    "phone": "123-456-7890"
  }
  ```

- `POST /customers/{id}/notes` - Add a note to customer
  ```json
  {
    "content": "Customer meeting scheduled"
  }
  ```

### Reminder Endpoints

- `POST /reminders` - Create a new reminder
  ```json
  {
    "customerId": "customer-id",
    "noteId": null,
    "remindAt": "2024-01-01T10:00:00",
    "message": "Follow up with customer"
  }
  ```

- `GET /reminders/{id}` - Get reminder by ID

- `GET /reminders/customer/{customerId}` - Get all reminders for a customer

## Running the Project

1. Clone the repository
2. Run the application:
   ```bash
   ./gradlew run
   ```
3. The server will start at `http://localhost:8080`

By default the application uses a embedded postgres database which will be recreated during server-startup.
In order to use a fully persistent dockerized postgres, go app `Application.kt` and uncomment the line:
```kotlin
val ctx = runBlocking { setupDockerDB(environment.config) }
```

You can then start the dockerized postgres with using `./start_database.sh` and stop it with `./stop_database.sh`.

## Running the Project from IntelliJ

If you want to run/debug the application from IntelliJ, run the file `ApplicationRunner.kt` as a Kotlin application.
It will start at `http://localhost:8080` the same was as the command line.

## Testing

Run the tests using:
```bash
./gradlew test
```

## Domain-Driven Design Concepts Demonstrated

- **Aggregates**: Customer as the main aggregate root
- **Value Objects**: CustomerId, ContactId, NoteId
- **Domain Events**: Contact and Note addition events
- **Repositories**: In-memory implementations for Customer and Reminder
- **Domain Services**: Business logic in CustomerService and ReminderService

## Project Features

- Clean separation of domains
- Event-driven architecture
- RESTful API design
- Proper error handling
- Comprehensive test coverage
- Type-safe serialization
