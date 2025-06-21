# eBay Clone SOAP Service

SOAP web service clone of eBay REST API.

## Overview

This project implements a SOAP web service that provides the same functionality as the eBay REST API. It includes operations for user management, listings, bidding, and order processing.

## Features

- User registration and authentication
- Listing creation and management
- Bidding functionality
- Buy Now option
- Order processing
- Category management

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Running the Service

```bash
# Clone the repository
git clone https://github.com/yourusername/ebay-clone-soap.git
cd ebay-clone-soap

# Run the service
./scripts/run.sh
```

The SOAP service will be available at: http://localhost:8080/ws/ebay
WSDL will be available at: http://localhost:8080/ws/ebay.wsdl

### Client Examples

The project includes a sample SOAP client script that demonstrates how to call the service operations:

```bash
# Run client examples
./client/soap-client.sh
```

### Testing

The project includes automated tests that compare the SOAP responses with the equivalent REST API responses:

```bash
# Run automated tests
./tests/test.sh
```

## API Documentation

### User Operations
- registerUser: Register a new user
- login: Authenticate a user and get a token
- getUser: Get user details by ID
- searchUsers: Search for users by username or email

### Listing Operations
- createListing: Create a new listing
- getListing: Get listing details by ID
- searchListings: Search for listings by various criteria

### Bid Operations
- placeBid: Place a bid on a listing
- getBidHistory: Get bid history for a listing

### Order Operations
- buyNow: Purchase a listing immediately
- getUserOrders: Get orders for a user

## Project Structure

```
/ebay-clone-soap
 ├── src/main/java/com/ebay/soap/
 │   ├── config/              # Spring configuration
 │   ├── endpoint/            # SOAP endpoints
 │   ├── entity/              # JPA entities
 │   ├── repository/          # Data repositories
 │   └── types/               # JAXB-generated classes
 ├── src/main/resources/
 │   └── xsd/                 # XML Schema Definitions
 ├── scripts/                 # Deployment scripts
 ├── client/                  # Client examples
 └── tests/                   # Automated tests
```

## Technologies Used

- Spring Boot
- Spring Web Services
- Spring Data JPA
- JAXB
- H2 Database
- Maven

## Docker Support

The project can also be run using Docker:

```bash
# Build and run with Docker
docker build -t ebay-soap .
docker run -p 8080:8080 ebay-soap
```

Or using Docker Compose:

```bash
# Run with Docker Compose
docker-compose up
```
