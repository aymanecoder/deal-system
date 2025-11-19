# FX Deals Data Warehouse System

A Spring Boot application for importing and analyzing Foreign Exchange (FX) deals for Bloomberg. This system accepts CSV files containing deal details, validates them, and persists them into a PostgreSQL database.

## Features

- **CSV File Import**: Upload CSV files containing FX deal information
- **Data Validation**: Comprehensive validation of deal data including:
  - Deal ID uniqueness check
  - Currency code validation (ISO 4217)
  - Date/time format validation
  - Amount format and value validation
- **Duplicate Prevention**: System prevents importing the same file twice
- **No Rollback Policy**: All processed rows (valid and invalid) are saved to the database
- **Transaction Logging**: Complete audit trail of all import operations
- **Accumulative Deal Counts**: Automatic tracking of deal counts per currency
- **Web Interface**: User-friendly Thymeleaf-based web interface for file upload and summary viewing

## Technology Stack

- **Backend**: Spring Boot 3.5.7
- **Frontend**: Thymeleaf, Bootstrap 5.3.2
- **Database**: PostgreSQL 15
- **Build Tool**: Maven
- **Java Version**: 17

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose (for PostgreSQL)
- Make (optional, for using Makefile commands)

## Project Structure

```
deal-system/
├── src/
│   ├── main/
│   │   ├── java/com/example/dealsystem/
│   │   │   ├── controller/          # REST controllers
│   │   │   ├── domain/              # Entity models
│   │   │   ├── repository/          # Data access layer
│   │   │   ├── service/             # Business logic
│   │   │   └── DealSystemApplication.java
│   │   └── resources/
│   │       ├── templates/           # Thymeleaf templates
│   │       └── application.properties
│   └── test/                        # Unit tests
├── compose.yaml                     # Docker Compose configuration
├── Makefile                         # Build and run commands
├── pom.xml                          # Maven dependencies
└── deals.csv                        # Sample CSV file for testing
```

## Database Schema

The system uses Liquibase for database schema management. All database changes are version-controlled through Liquibase changelog files located in `src/main/resources/db/changelog/`.

The system uses the following main tables:

- **valid_deal**: Stores successfully validated deals
- **invalid_deal**: Stores deals that failed validation with error messages
- **transaction_log**: Tracks all file import operations
- **accumulative_deal_count**: Maintains cumulative deal counts per currency

### Database Migration

Liquibase automatically manages database schema changes. On application startup, Liquibase will:
- Check the current database schema version
- Apply any pending changelog files
- Track all changes in the `databasechangelog` and `databasechangeloglock` tables

## CSV File Format

The CSV file must contain the following columns (in order):

1. `deal_id` - Unique identifier for the deal (string)
2. `from_currency` - ISO 4217 currency code for the source currency (3-letter code)
3. `to_currency` - ISO 4217 currency code for the target currency (3-letter code)
4. `date_time` - Deal timestamp in format: `yyyy-MM-dd HH:mm:ss`
5. `amount` - Deal amount in the ordering currency (decimal number, must be > 0)

### Sample CSV File

A sample CSV file `deals.csv` is provided in the root directory for testing.

### Example CSV:

```csv
deal_id,from_currency,to_currency,date_time,amount
DEAL001,USD,EUR,2024-01-15 10:30:00,1000.50
DEAL002,GBP,USD,2024-01-15 11:15:00,2500.75
```

## Getting Started

### 1. Start PostgreSQL Database

Using Docker Compose:

```bash
docker compose up -d
```

Or using Make:

```bash
make docker-up
```

This will start a PostgreSQL container with:
- Database: `deals_data`
- User: `deals_user`
- Password: `deals_password`
- Port: `5432`

### 2. Build the Project

```bash
./mvnw clean package
```

Or using Make:

```bash
make build
```

### 3. Run the Application

```bash
./mvnw spring-boot:run
```

Or using Make:

```bash
make run
```

Or to start both database and application:

```bash
make start
```

The application will be available at: `http://localhost:8080`

### 4. Access the Web Interface

- **Upload Page**: http://localhost:8080/upload
- **Summary Page**: http://localhost:8080/summary

## Usage

### Uploading a CSV File

1. Navigate to http://localhost:8080/upload
2. Click "Choose File" and select your CSV file
3. Click "Upload"
4. The system will process the file and display a success message

### Viewing Import Summary

1. Navigate to http://localhost:8080/summary
2. Enter the file name in the search box
3. Click "Search"
4. View the import summary including:
   - Status (COMPLETED, PROCESSING, or FAILED)
   - Number of valid deals imported
   - Number of invalid deals
   - Processing duration
   - Start and completion times

## Validation Rules

The system validates each deal row for:

1. **Deal ID**: Must be present and unique (not already in database)
2. **From Currency**: Must be a valid ISO 4217 currency code
3. **To Currency**: Must be a valid ISO 4217 currency code
4. **Date/Time**: Must be in format `yyyy-MM-dd HH:mm:ss`
5. **Amount**: Must be a valid decimal number greater than zero

### Supported Currency Codes

USD, EUR, GBP, JPY, AUD, CAD, CHF, CNY, NZD, SEK, NOK, DKK, SGD, HKD, INR, KRW, MXN, BRL, ZAR, RUB

## Testing

Run all tests:

```bash
./mvnw test
```

Or using Make:

```bash
make test
```

### Test Coverage

The project includes unit tests for:
- Deal validation logic
- Deal service operations
- Transaction log service
- Currency code validation
- Repository operations

## Makefile Commands

The project includes a Makefile for streamlined operations:

```bash
make help          # Show all available commands
make build         # Build the project
make run           # Run the application
make test          # Run tests
make clean         # Clean build artifacts
make docker-up     # Start PostgreSQL
make docker-down   # Stop PostgreSQL
make docker-logs   # View Docker logs
make db-reset      # Reset database (WARNING: deletes all data)
make start         # Start database and application
make stop          # Stop database and application
make all           # Clean, build, and test
```

## Configuration

Application configuration is in `src/main/resources/application.properties`:

- **Server Port**: 8080
- **Database**: PostgreSQL (configured in `compose.yaml`)
- **File Upload Limit**: 100MB
- **Database Migration**: Liquibase (schema managed through changelog files)
- **Logging**: Logback (configured in `logback-spring.xml`)

### Logging Configuration

The application uses Logback for logging management. The configuration file `logback-spring.xml` includes:
- Console logging with colored output
- MDC (Mapped Diagnostic Context) support for transaction log IDs
- Appropriate log levels for different packages
- Liquibase logging configuration

## Error Handling

The system implements comprehensive error handling:

- **File Upload Errors**: Displayed on the upload page
- **Validation Errors**: Stored in `invalid_deal` table with error messages
- **Transaction Errors**: Logged in `transaction_log` table
- **Duplicate File Prevention**: System checks if file was already imported

## Logging

The application uses SLF4J with the following log levels:

- Application: INFO
- Spring Framework: INFO
- Hibernate: WARN

Logs are output to the console with timestamps.

## Performance Considerations

- Batch processing for database operations
- Indexed database columns for faster queries
- Efficient CSV parsing using Apache Commons CSV
- Transaction management to ensure data consistency

## Development

### Adding New Currency Codes

Edit `src/main/java/com/example/dealsystem/domain/CurrencyCode.java` and add the new currency to the enum.

### Modifying Validation Rules

Edit `src/main/java/com/example/dealsystem/service/deals/DealsValidator.java`.

## Troubleshooting

### Database Connection Issues

1. Ensure PostgreSQL is running: `docker compose ps`
2. Check database credentials in `application.properties`
3. Verify port 5432 is not in use by another service

### File Upload Fails

1. Check file size (max 100MB)
2. Verify CSV format matches expected structure
3. Check application logs for detailed error messages

### Port Already in Use

Change the server port in `application.properties`:
```
server.port=8082
```

## License

This project is developed for educational/demonstration purposes.

## Author

Developed as part of a data warehouse system for Bloomberg FX deals analysis.

## Contributing

This is a demonstration project. For production use, consider:
- Adding authentication and authorization
- Implementing rate limiting
- Adding more comprehensive error handling
- Implementing batch processing for very large files
- Adding monitoring and alerting
- Implementing data export functionality

