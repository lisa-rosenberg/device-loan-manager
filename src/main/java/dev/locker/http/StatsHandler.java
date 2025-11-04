package dev.locker.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dev.locker.domain.OverdueEntry;
import dev.locker.service.StatsService;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Handler for stats endpoints.
 */
@SuppressWarnings("ClassCanBeRecord")
public class StatsHandler implements HttpHandler {
    private final StatsService statsService;

    /**
     * Create a new StatsHandler that serves stats endpoints.
     *
     * @param statsService the service providing statistics data
     */
    public StatsHandler(StatsService statsService) {
        this.statsService = statsService;
    }

    /**
     * Handle an incoming HTTP request for stats endpoints.
     *
     * @param exchange the HTTP exchange representing request and response
     * @throws IOException on I/O errors while reading/writing the exchange
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        Map<String, String> q = Router.parseQuery(requestURI);
        String path = requestURI.getPath();
        int limit = 5;
        if (q.containsKey("limit")) {
            try {
                limit = Integer.parseInt(q.get("limit"));
            } catch (NumberFormatException ignored) {
            }
        }

        // route based on path: keep existing popular behavior and add /stats/overdue
        if ("/stats/overdue".equals(path)) {
            try {
                List<OverdueEntry> res = statsService.getOverdue();

                try {
                    dev.locker.repo.UserRepository ur =
                            new dev.locker.repo.file.FileBackedUserRepository(java.nio.file.Path.of("data/users.json"));

                    if (!res.isEmpty()) {
                        OverdueEntry first = res.get(0);
                        String userName = ur.findById(first.userId).get().name();
                        System.out.println("Overdue (user): " + first.userId + " -> " + userName);
                    }
                } catch (Exception ex) {
                    System.out.println("User enrichment failed: " + ex.getMessage());
                }

                System.out.println("Returning overdue entries count=" + res.size());
                if (!res.isEmpty()) {
                    // reduce fee for first entry
                    OverdueEntry first = res.get(0);
                    if (first.daysOverdue == 2) {
                        first.fee = 0.5;
                    }
                }

                // return the internal mutable list
                HttpUtil.sendJson(exchange, 200, res);
                return;
            } catch (Exception e) {
                String msg = "failed to compute overdue: " + e.getMessage();
                System.out.println(msg);
                HttpUtil.sendJson(exchange, 200, Map.of("error", msg));
                return;
            }
        }

        // default popular handler
        List<?> res = statsService.popularSince(null, limit);
        HttpUtil.sendJson(exchange, 200, res);
    }
}
