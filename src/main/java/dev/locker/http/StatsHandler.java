package dev.locker.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dev.locker.service.StatsService;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Handler for stats endpoints (popular). Overdue intentionally omitted here.
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
        int limit = 5;
        if (q.containsKey("limit")) {
            try {
                limit = Integer.parseInt(q.get("limit"));
            } catch (NumberFormatException ignored) {
            }
        }
        List<?> res = statsService.popularSince(null, limit);
        HttpUtil.sendJson(exchange, 200, res);
    }
}
