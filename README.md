# faculty-flow

Back-end for **Faculty Flow**, an app for managing faculty room reservations.

## Modules

| Module                     | Purpose                                                     |
|----------------------------|-------------------------------------------------------------|
| `faculty-flow-core`        | Shared library (security, DTOs, events, exception handling) |
| `faculty-flow-gateway`     | API gateway, the single entry point for the front-end       |
| `faculty-flow-user`        | Users, authentication, account verification                 |
| `faculty-flow-room`        | Rooms                                                       |
| `faculty-flow-reservation` | Room reservations                                           |
| `faculty-flow-mail`        | Sends e-mails based on Kafka events                         |
| `faculty-flow-postgres`    | PostgreSQL image with the database init script              |

## Prerequisites

- [SDKMAN!](https://sdkman.io/) with JDK 25 (or JDK 25 set as the default)
- Maven
- Docker with Docker Compose

## Running

### Development mode

Select the JDK and start the project (`dev` is optional; it is the default profile).
The front-end container must be started manually (see `faculty-flow-client` project for more information).

```bash
sdk use java 25.0.2-tem # Or use any other 25 JDK
bash run.sh dev         # Start the project with the 'dev' profile
```

### Production mode

Requires the production environment file `docker/prod.env` (see the [example](#environment-example) below).
The front-end container is started automatically.

```bash
bash run.sh prod
```

### Purge

Stop everything and delete containers and volumes, including the database data:

```bash
bash run.sh purge
```

## Environment example

### Production

The production environment file `docker/prod.env` should have the following format:

```properties
ENV=prod

JWT_SECRET=<your-secret>

APP_PUBLIC_BASE_URL=http://localhost:9000
FRONTEND_URL=http://localhost:4200

SMTP_HOST=smtp.example.com
SMTP_PORT=587
SMTP_USERNAME=<smtp-username>
SMTP_PASSWORD=<smtp-password>

POSTGRES_ADMIN_USER=admin
POSTGRES_ADMIN_PASSWORD=<admin-password>

USER_DB_URL=jdbc:postgresql://postgres:5432/ff_user
USER_DB_USERNAME=ff_user
USER_DB_PASSWORD=ff_user

ROOM_DB_URL=jdbc:postgresql://postgres:5432/ff_room
ROOM_DB_USERNAME=ff_room
ROOM_DB_PASSWORD=ff_room

RESERVATION_DB_URL=jdbc:postgresql://postgres:5432/ff_reservation
RESERVATION_DB_USERNAME=ff_reservation
RESERVATION_DB_PASSWORD=ff_reservation
```

## License

See [LICENSE](LICENSE).
