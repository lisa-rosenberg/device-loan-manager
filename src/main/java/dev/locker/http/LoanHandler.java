package dev.locker.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dev.locker.service.ConflictException;
import dev.locker.service.LoanService;
import dev.locker.service.NotFoundException;
import dev.locker.service.ValidationException;

import java.io.IOException;

/**
 * Handles loan borrow and return endpoints.
 */
@SuppressWarnings("ClassCanBeRecord")
public class LoanHandler implements HttpHandler {
    private final LoanService loanService;

    /**
     * Constructor for LoanHandler.
     *
     * @param loanService the loan service
     */
    public LoanHandler(LoanService loanService) {
        this.loanService = loanService;
    }

    /**
     * Handles HTTP requests for loan-related endpoints.
     *
     * @param exchange the HTTP exchange
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        try {
            if ("/loans/borrow".equals(path)) {
                BorrowRequest req = HttpUtil.readJson(exchange, BorrowRequest.class);
                if (req == null) {
                    HttpUtil.sendError(exchange, 400, "Missing body");
                    return;
                }
                var loan = loanService.borrow(req.deviceId, req.userId, req.days);
                HttpUtil.sendJson(exchange, 201, loan);
                return;
            }
            if ("/loans/return".equals(path)) {
                ReturnRequest req = HttpUtil.readJson(exchange, ReturnRequest.class);
                if (req == null || req.deviceId == null) {
                    HttpUtil.sendError(exchange, 400, "Missing deviceId");
                    return;
                }
                var res = loanService.returnDevice(req.deviceId);
                HttpUtil.sendJson(exchange, 200, res);
                return;
            }
            HttpUtil.sendError(exchange, 404, "Not found");
        } catch (ValidationException e) {
            HttpUtil.sendError(exchange, 400, e.getMessage());
        } catch (NotFoundException e) {
            HttpUtil.sendError(exchange, 404, e.getMessage());
        } catch (ConflictException e) {
            HttpUtil.sendError(exchange, 409, e.getMessage());
        } catch (Exception e) {
            HttpUtil.sendError(exchange, 500, "Internal error");
        }
    }

    private static class BorrowRequest {
        public String deviceId;
        public String userId;
        public Integer days;
    }

    private static class ReturnRequest {
        public String deviceId;
    }
}

