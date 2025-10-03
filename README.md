# Rent-Car-Bot

## Description

Rentcar Backend is a Telegram bot backend built using the WebHook method, connected to a robust and extensible API built with Java Spring Boot to power a car rental web application. This bot serves as an interface for users to interact with the car rental services via Telegram, including browsing available cars, making bookings, managing favorites, and more.

## Features

- **Telegram Bot Integration**: Handles user interactions via Telegram using webhooks for real-time responses.
- **User Management**: Manages Telegram users with roles (e.g., user, admin) and steps for conversation flow.
- **Car Rental Operations**: 
  - Browse available cars
  - View car details
  - Make bookings
  - Manage favorites
  - Submit reviews
  - Handle payments and penalties
- **Multi-role Support**: Separate services for user and admin functionalities.
- **Microservice Architecture**: Communicates with external services via Feign clients (e.g., Car Service, Auth Service, Booking Service).
- **Data Persistence**: Uses PostgreSQL for storing Telegram user data and Redis for caching.
- **Error Handling**: Comprehensive exception handling and logging.
- **Internationalization**: Supports multiple languages through text services.

## Technologies Used

- **Java 17**
- **Spring Boot 3.5.3**: Framework for building the application
- **Spring Data JPA**: For database interactions
- **Spring Cloud OpenFeign**: For declarative REST client
- **PostgreSQL**: Database for persistent data
- **Redis**: For caching and session management
- **TelegramBots API**: For Telegram bot functionality
- **MapStruct**: For object mapping
- **Lombok**: For reducing boilerplate code
- **Maven**: Build tool

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL database
- Redis server
- Telegram Bot Token (from BotFather)

## Installation and Setup

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd Rent-Car-Bot
   ```

2. **Configure environment variables:**
   Create a `.env` file or set environment variables for the following:
   - `DB_HOSTNAME`: PostgreSQL host
   - `DB_PORT`: PostgreSQL port (default: 5432)
   - `DB_NAME`: Database name
   - `DB_USERNAME`: Database username
   - `DB_PASSWORD`: Database password
   - `REDIS_HOST`: Redis host
   - `REDIS_PORT`: Redis port (default: 6379)
   - `BOT_TOKEN`: Telegram bot token from BotFather
   - `BOT_USERNAME`: Telegram bot username
   - `WEBHOOK_URL`: URL for webhook (e.g., your server URL)
   - `SERVICE_URL`: Base URL for external services
   - `RENT_CAR_HOST`: Host for the car rental service
   - `RENT_CAR_PORT`: Port for the car rental service

3. **Build the project:**
   ```bash
   mvn clean install
   ```

## Running the Application

1. **Start the application:**
   ```bash
   mvn spring-boot:run
   ```

   The application will start on port 8090 (configurable in `application.yml`).

2. **Set up Telegram webhook:**
   Ensure your `WEBHOOK_URL` is accessible and points to `/webhook` endpoint.

3. **Access the bot:**
   Start a conversation with your Telegram bot using the username.

## Configuration

The application uses `application.yml` for configuration. Key settings include:

- **Database**: Configured via environment variables for PostgreSQL.
- **Redis**: For caching, configured via environment variables.
- **Telegram**: Bot token, username, and webhook path.
- **Feign**: Timeout settings for external service calls.
- **Services**: URLs for microservices.

## Project Structure

```
src/main/java/uz/dev/rentcarbot/
├── config/                 # Configuration classes (Redis, Telegram Bot)
├── controller/             # Webhook controller
├── entity/                 # JPA entities (e.g., TelegramUser)
├── enums/                  # Enumeration classes (Roles, Steps, etc.)
├── exceptions/             # Custom exceptions
├── handler/                # Exception handlers
├── payload/                # DTOs for API communication
├── repository/            # JPA repositories
├── service/                # Business logic services
│   ├── impl/              # Service implementations
│   └── template/          # Service interfaces
├── client/                 # Feign clients for external services
├── utils/                  # Utility classes
└── RentCarBotApplication.java  # Main application class
```

## External Services

The bot communicates with several microservices via Feign clients:

- **Auth Client**: User authentication
- **Car Client**: Car-related operations
- **User Client**: User management
- **Booking Client**: Booking operations
- **Payment Client**: Payment processing
- **Review Client**: Review management
- **And more...**

Ensure these services are running and accessible via the configured URLs.

## Testing

Run tests using:
```bash
mvn test
```

## Contributing

1. Fork the repository.
2. Create a feature branch (`git checkout -b feature/AmazingFeature`).
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`).
4. Push to the branch (`git push origin feature/AmazingFeature`).
5. Open a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Authors

- Asrorbek - Initial work

## Acknowledgments

- Spring Boot community
- Telegram Bots API
- Open-source contributors</content>
</xai:function_call">The README.md file has been successfully created for the Rent-Car-Bot project. It provides a comprehensive overview based on the explored project structure, including features, technologies, setup instructions, and more. If you need any modifications or additional sections, let me know!