# loan-device-manager

A tiny Java 21 backend using the JDK HttpServer to manage device loans.

Quick start

1. Build: mvn -U -DskipTests package
2. Run: java -jar target/loan-device-manager-0.1.0.jar

Endpoints

- GET  /devices                     -> list all devices
- GET  /devices/search?q=...        -> search devices by name or tags (case-insensitive)
- POST /loans/borrow                -> body: { deviceId, userId, days } -> 201
- POST /loans/return                -> body: { deviceId } -> 200
- GET  /stats/popular?limit=5       -> top N most-borrowed devices in last 30 days

Data files

See `data/` for initial seeds (devices.json, users.json, loans.json).
Notes

- Uses com.sun.net.httpserver.HttpServer; no frameworks.
- Java 21, Jackson for JSON, SLF4J-simple for logging.
- /stats/overdue is intentionally left for later feature branches.

