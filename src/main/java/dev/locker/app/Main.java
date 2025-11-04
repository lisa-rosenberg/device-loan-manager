package dev.locker.app;

import com.sun.net.httpserver.HttpServer;
import dev.locker.http.DeviceHandler;
import dev.locker.http.LoanHandler;
import dev.locker.http.Router;
import dev.locker.http.StatsHandler;
import dev.locker.repo.file.FileBackedDeviceRepository;
import dev.locker.repo.file.FileBackedLoanRepository;
import dev.locker.repo.file.FileBackedUserRepository;
import dev.locker.service.DeviceService;
import dev.locker.service.LoanService;
import dev.locker.service.StatsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;

/**
 * Application entry point. Starts a JDK HttpServer and wires repositories, services and handlers.
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        // Initialize repositories
        Path base = Path.of(".");
        FileBackedDeviceRepository deviceRepo = new FileBackedDeviceRepository(base.resolve("data").resolve("devices.json"));
        FileBackedUserRepository userRepo = new FileBackedUserRepository(base.resolve("data").resolve("users.json"));
        FileBackedLoanRepository loanRepo = new FileBackedLoanRepository(base.resolve("data").resolve("loans.json"));

        // Initialize services
        DeviceService deviceService = new DeviceService(deviceRepo);
        LoanService loanService = new LoanService(deviceRepo, userRepo, loanRepo);
        StatsService statsService = new StatsService(deviceRepo, loanRepo);

        // Start HTTP server
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        Router router = new Router(server);

        // Register routes
        router.registerGet("/devices", new DeviceHandler(deviceService));
        router.registerGet("/devices/search", new DeviceHandler(deviceService));
        router.registerPost("/loans/borrow", new LoanHandler(loanService));
        router.registerPost("/loans/return", new LoanHandler(loanService));
        router.registerGet("/stats/popular", new StatsHandler(statsService));
        router.registerStatsOverdue(new StatsHandler(statsService));

        // Start server
        server.start();
        logger.info("Started loan-device-manager on port {} with routes: /devices, /devices/search, /loans/borrow, /loans/return, /stats/popular", PORT);

        // Add shutdown hook to persist data
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down server and persisting data...");
            server.stop(1);
            try {
                deviceRepo.persist();
                userRepo.persist();
                loanRepo.persist();
            } catch (Exception e) {
                logger.error("Error persisting data on shutdown", e);
            }
        }));
    }
}
