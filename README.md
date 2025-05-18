[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/iKoHruX_)

# Car Rental App
All functionalities for reservations and user management are implemented in their respective services.
The frontend allows managing the car catalog (models), fleet (vehicles), and reservations as a manager/staff, while customers can make, view, and cancel reservations.
For this lab, two customers are available with user IDs 1 and 2.

## Environment Setup

### Start the Application

To build and run all services:

```bash
docker-compose --env-file .env up --build
```


#### Services & Ports
| Service                 | Port  | Description                                     |
|-------------------------|-------|-------------------------------------------------|
| Postgres (Multi-DB)     | 5432  | Hosts `reservation_db`, `user_db`, `payment_db` |
| Reservation Service     | 8081  | Handles reservation operations                  |
| User Management Service | 8082  | Manages user data                               |
| Payment Service         | 8085  | Integrates with PayPal                          |
| Frontend                | 8080  | Customer & staff web interface                  |
| Adminer                 | 8090  | Database UI                                     |
| Kafka Broker            | 29092 | Kafka messaging system                          |
| Kafka Connect           | 8083  | Kafka CDC connector interface                   |
| Kafka UI                | 9094  | Kafka topic viewer                              |


## Testing & Quality Assurance

### Running Tests

To execute all tests (Unit, API, and Integration) with coverage reports for both services:

```bash
./run-all-tests.sh
```
- Before running, ensure the project is running via Docker and the database is up and operational.

This script performs the following:
- Executes test suites for both UserManagementService and ReservationService
- Generates detailed coverage reports using Jacoco
- Automatically opens the coverage reports in your default browser

### Coverage Reports
Coverage reports can be found at the following locations after running the tests:
- `ReservationService/build/jacocoHtml/index.html`
- `UserManagementService/build/jacocoHtml/index.html`

The coverage reports include:
- Detailed test coverage metrics per class, method, and line
- Visual representation of covered and uncovered code
- Direct links to source code lines lacking coverage
