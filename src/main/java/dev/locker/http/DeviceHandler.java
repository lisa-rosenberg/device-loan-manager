package dev.locker.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dev.locker.domain.Device;
import dev.locker.service.DeviceService;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * HTTP handler for device-related endpoints.
 */
@SuppressWarnings("ClassCanBeRecord")
public class DeviceHandler implements HttpHandler {
    private final DeviceService deviceService;

    /**
     * Constructor for DeviceHandler.
     *
     * @param deviceService the device service
     */
    public DeviceHandler(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    /**
     * Handles HTTP requests for device-related endpoints.
     *
     * @param exchange the HTTP exchange
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        Map<String, String> parseQuery = Router.parseQuery(requestURI);
        if ("/devices".equals(requestURI.getPath())) {
            List<Device> devices = deviceService.listAll();
            HttpUtil.sendJson(exchange, 200, devices);
            return;
        }
        if ("/devices/search".equals(requestURI.getPath())) {
            String query = parseQuery.getOrDefault("q", "");
            List<Device> result = deviceService.search(query);
            HttpUtil.sendJson(exchange, 200, result);
            return;
        }
        HttpUtil.sendError(exchange, 404, "Not found");
    }
}
