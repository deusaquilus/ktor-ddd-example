# Ktor DDD Example

This is a demonstration project showcasing Domain-Driven Design (DDD) concepts implemented with Ktor framework. The project implements a mini-CRM system with customers and reminders functionality.

## Technologies Used

- Kotlin
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
│       │   │   └── CustomerRoutes.kt
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

### Customer Endpoints

- `POST /customers` - Create a new customer
  ```json
  {
    "name": "John Doe",
    "email": "john@example.com"
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
    "customerId": {"value": "customer-uuid"},
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