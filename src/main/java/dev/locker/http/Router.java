package dev.locker.http;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Helpers that registers contexts and provides simple query parsing utilities.
 */
@SuppressWarnings("ClassCanBeRecord")
public class Router {
    private final HttpServer server;

    /**
     * Constructor for Router.
     *
     * @param server the HttpServer to register contexts on
     */
    public Router(HttpServer server) {
        this.server = server;
    }

    /**
     * Register a GET handler for the given path. The handler will only be invoked
     * when the incoming request method is GET; otherwise 405 is returned.
     *
     * @param path    the request path to register (e.g. "/devices")
     * @param handler the HttpHandler to handle matching requests
     */
    public void registerGet(String path, HttpHandler handler) {
        server.createContext(path, exchange -> {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            handler.handle(exchange);
        });
    }

    /**
     * Register a POST handler for the given path. The handler will only be invoked
     * when the incoming request method is POST; otherwise 405 is returned.
     *
     * @param path    the request path to register (e.g. "/loans/borrow")
     * @param handler the HttpHandler to handle matching requests
     */
    public void registerPost(String path, HttpHandler handler) {
        server.createContext(path, exchange -> {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            handler.handle(exchange);
        });
    }

    /**
     * Parse query string into map (first value wins). Null-safe.
     *
     * @param uri    the URI containing the query string
     * @return map of query parameter names to values
     */
    public static Map<String, String> parseQuery(URI uri) {
        Map<String, String> map = new HashMap<>();
        String raw = uri.getRawQuery();
        if (raw == null || raw.isEmpty()) return map;
        String[] pairs = raw.split("&");
        for (String p : pairs) {
            int idx = p.indexOf('=');
            if (idx >= 0) {
                String k = decode(p.substring(0, idx));
                String v = decode(p.substring(idx + 1));
                map.putIfAbsent(k, v);
            } else {
                map.putIfAbsent(decode(p), "");
            }
        }
        return map;
    }

    private static String decode(String s) {
        return s.replace("+", " ");
    }
}
