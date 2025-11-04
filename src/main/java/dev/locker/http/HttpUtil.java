package dev.locker.http;

import com.sun.net.httpserver.HttpExchange;
import dev.locker.util.JsonUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Helpers for reading request bodies and writing JSON responses.
 */
@SuppressWarnings("unused")
public final class HttpUtil {
    private HttpUtil() {
    }

    /**
     * Read the full request body from the exchange and return it as bytes.
     *
     * @param exchange the HttpExchange to read from
     * @return request body bytes (can be empty)
     * @throws IOException if an I/O error occurs reading the request
     */
    public static byte[] readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream in = exchange.getRequestBody(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buf = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buf)) != -1) {
                out.write(buf, 0, bytesRead);
            }
            return out.toByteArray();
        }
    }

    /**
     * Read and deserialize the JSON request body into an object of the given class.
     *
     * @param exchange the HttpExchange to read from
     * @param clazz    the class of the object to deserialize into
     * @param <T>      the type of the object to deserialize into
     * @return the deserialized object, or null if the request body is empty
     * @throws IOException if an I/O error occurs reading the request or deserializing the JSON
     */
    public static <T> T readJson(HttpExchange exchange, Class<T> clazz) throws IOException {
        byte[] bytes = readRequestBody(exchange);
        if (bytes.length == 0) return null;
        return JsonUtil.mapper().readValue(bytes, clazz);
    }

    /**
     * Serialize the given object as JSON and send it in the response with the given status.
     *
     * @param exchange the HttpExchange to send the response to
     * @param status   the HTTP status code to send
     * @param obj      the object to serialize as JSON
     * @throws IOException if an I/O error occurs sending the response
     */
    public static void sendJson(HttpExchange exchange, int status, Object obj) throws IOException {
        byte[] bytes = JsonUtil.mapper().writeValueAsBytes(obj);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.getResponseBody().close();
    }

    /**
     * Send an empty response with the given status.
     *
     * @param exchange the HttpExchange to send the response to
     * @param status   the HTTP status code to send
     * @throws IOException if an I/O error occurs sending the response
     */
    public static void sendEmpty(HttpExchange exchange, int status) throws IOException {
        exchange.sendResponseHeaders(status, -1);
        exchange.getResponseBody().close();
    }

    /**
     * Send an error response with the given status and message.
     *
     * @param exchange the HttpExchange to send the response to
     * @param status   the HTTP status code to send
     * @param message  the error message to send
     * @throws IOException if an I/O error occurs sending the response
     */
    public static void sendError(HttpExchange exchange, int status, String message) throws IOException {
        ErrorPayload payload = new ErrorPayload(message);
        sendJson(exchange, status, payload);
    }

    private record ErrorPayload(String error) {
    }
}
