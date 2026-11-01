package com.ticketing;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.ticketing.model.Booking;
import com.ticketing.model.Event;
import com.ticketing.service.BookingService;

import java.awt.Desktop;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Built-in HTTP Web Server that hosts the human-crafted ticketing website
 * and provides REST API endpoints for the BookingService.
 */
public class WebServer {

    private final BookingService bookingService;
    private final int port;
    private HttpServer server;

    public WebServer(BookingService bookingService, int port) {
        this.bookingService = bookingService;
        this.port = port;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        // REST API routes
        server.createContext("/api/events", new EventsHandler());
        server.createContext("/api/book", new BookHandler());
        server.createContext("/api/bookings", new BookingsHandler());
        server.createContext("/api/cancel", new CancelHandler());

        // Static files router
        server.createContext("/", new StaticFileHandler());

        server.setExecutor(null); // default executor
        server.start();
        System.out.println("=================================================");
        System.out.println("  PASSAGE Event Ticketing Web Server Started!    ");
        System.out.println("  URL: http://localhost:" + port + "/");
        System.out.println("=================================================");
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    // --- Handlers ---

    private class EventsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            List<Event> events = bookingService.getAllEvents();
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < events.size(); i++) {
                Event e = events.get(i);
                json.append("{")
                        .append("\"id\":\"").append(escapeJson(e.getId())).append("\",")
                        .append("\"title\":\"").append(escapeJson(e.getTitle())).append("\",")
                        .append("\"category\":\"").append(escapeJson(e.getCategory())).append("\",")
                        .append("\"date\":\"").append(escapeJson(e.getDate())).append("\",")
                        .append("\"venue\":\"").append(escapeJson(e.getVenue())).append("\",")
                        .append("\"city\":\"").append(escapeJson(e.getCity())).append("\",")
                        .append("\"description\":\"").append(escapeJson(e.getDescription())).append("\",")
                        .append("\"imageUrl\":\"").append(escapeJson(e.getImageUrl())).append("\",")
                        .append("\"badge\":\"").append(escapeJson(e.getBadge())).append("\",")
                        .append("\"price\":").append(e.getPrice()).append(",")
                        .append("\"totalTickets\":").append(e.getTotalTickets()).append(",")
                        .append("\"availableTickets\":").append(e.getAvailableTickets())
                        .append("}");
                if (i < events.size() - 1) json.append(",");
            }
            json.append("]");

            byte[] bytes = json.toString().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    private class BookHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendError(exchange, 405, "Method Not Allowed");
                return;
            }

            String body = readBody(exchange);
            Map<String, String> data = parseJsonSimple(body);

            String eventId = data.get("eventId");
            String name = data.get("customerName");
            String contact = data.get("contact");
            String tier = data.get("tier");
            String qtyStr = data.get("quantity");

            int qty = 1;
            try {
                if (qtyStr != null) qty = Integer.parseInt(qtyStr);
            } catch (NumberFormatException ignored) {}

            try {
                Booking booking = bookingService.bookTickets(eventId, name, contact, tier, qty);
                String resp = "{"
                        + "\"success\":true,"
                        + "\"bookingId\":\"" + escapeJson(booking.getBookingId()) + "\","
                        .concat("\"eventId\":\"").concat(escapeJson(booking.getEventId())).concat("\",")
                        + "\"eventTitle\":\"" + escapeJson(booking.getEventTitle()) + "\","
                        + "\"customerName\":\"" + escapeJson(booking.getCustomerName()) + "\","
                        + "\"customerContact\":\"" + escapeJson(booking.getCustomerContact()) + "\","
                        + "\"ticketTier\":\"" + escapeJson(booking.getTicketTier()) + "\","
                        + "\"quantity\":" + booking.getQuantity() + ","
                        + "\"totalCost\":" + booking.getTotalCost() + ","
                        + "\"bookingTime\":\"" + escapeJson(booking.getFormattedBookingTime()) + "\","
                        + "\"receipt\":\"" + escapeJson(booking.generateReceipt()) + "\""
                        + "}";
                byte[] bytes = resp.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            } catch (Exception ex) {
                sendError(exchange, 400, ex.getMessage());
            }
        }
    }

    private class BookingsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            List<Booking> bookings = bookingService.getAllBookings();
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < bookings.size(); i++) {
                Booking b = bookings.get(i);
                json.append("{")
                        .append("\"bookingId\":\"").append(escapeJson(b.getBookingId())).append("\",")
                        .append("\"eventId\":\"").append(escapeJson(b.getEventId())).append("\",")
                        .append("\"eventTitle\":\"").append(escapeJson(b.getEventTitle())).append("\",")
                        .append("\"customerName\":\"").append(escapeJson(b.getCustomerName())).append("\",")
                        .append("\"customerContact\":\"").append(escapeJson(b.getCustomerContact())).append("\",")
                        .append("\"ticketTier\":\"").append(escapeJson(b.getTicketTier())).append("\",")
                        .append("\"quantity\":").append(b.getQuantity()).append(",")
                        .append("\"totalCost\":").append(b.getTotalCost()).append(",")
                        .append("\"bookingTime\":\"").append(escapeJson(b.getFormattedBookingTime())).append("\"")
                        .append("}");
                if (i < bookings.size() - 1) json.append(",");
            }
            json.append("]");

            byte[] bytes = json.toString().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    private class CancelHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            String body = readBody(exchange);
            Map<String, String> data = parseJsonSimple(body);
            String bookingId = data.get("bookingId");

            if (bookingId == null || bookingId.trim().isEmpty()) {
                sendError(exchange, 400, "Booking ID is required.");
                return;
            }

            boolean ok = bookingService.cancelBooking(bookingId.trim());
            if (ok) {
                String resp = "{\"success\":true,\"message\":\"Booking cancelled and tickets refunded to pool.\"}";
                byte[] bytes = resp.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            } else {
                sendError(exchange, 404, "Booking not found or already cancelled.");
            }
        }
    }

    private static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path == null || path.equals("/") || path.isEmpty()) {
                path = "/index.html";
            }

            // Look in web directory on disk first, fallback to resources
            Path localFile = Paths.get("web", path.startsWith("/") ? path.substring(1) : path);
            byte[] fileBytes = null;
            String contentType = getMimeType(path);

            if (Files.exists(localFile) && !Files.isDirectory(localFile)) {
                fileBytes = Files.readAllBytes(localFile);
            } else {
                // Try classpath
                try (InputStream is = getClass().getResourceAsStream("/web" + path)) {
                    if (is != null) {
                        fileBytes = is.readAllBytes();
                    }
                }
            }

            if (fileBytes != null) {
                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.sendResponseHeaders(200, fileBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(fileBytes);
                }
            } else {
                String notFound = "<h1>404 File Not Found</h1><p>" + path + "</p>";
                exchange.sendResponseHeaders(404, notFound.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(notFound.getBytes(StandardCharsets.UTF_8));
                }
            }
        }
    }

    // --- Helpers ---

    private static void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }

    private static void sendError(HttpExchange exchange, int code, String msg) throws IOException {
        String json = "{\"success\":false,\"error\":\"" + escapeJson(msg) + "\"}";
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[2048];
            int read;
            while ((read = is.read(buf)) != -1) {
                baos.write(buf, 0, read);
            }
            return baos.toString(StandardCharsets.UTF_8);
        }
    }

    private static Map<String, String> parseJsonSimple(String body) {
        Map<String, String> map = new HashMap<>();
        if (body == null || body.trim().isEmpty()) return map;

        String trimmed = body.trim();
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            trimmed = trimmed.substring(1, trimmed.length() - 1);
            String[] pairs = trimmed.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            for (String pair : pairs) {
                String[] kv = pair.split(":(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (kv.length == 2) {
                    String k = cleanQuotes(kv[0].trim());
                    String v = cleanQuotes(kv[1].trim());
                    map.put(k, v);
                }
            }
        }
        return map;
    }

    private static String cleanQuotes(String s) {
        if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static String getMimeType(String path) {
        if (path.endsWith(".html")) return "text/html; charset=UTF-8";
        if (path.endsWith(".css")) return "text/css; charset=UTF-8";
        if (path.endsWith(".js")) return "application/javascript; charset=UTF-8";
        if (path.endsWith(".svg")) return "image/svg+xml";
        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        if (path.endsWith(".json")) return "application/json; charset=UTF-8";
        return "text/plain; charset=UTF-8";
    }

    public static void main(String[] args) {
        BookingService service = new BookingService();
        WebServer webServer = new WebServer(service, 8080);
        try {
            webServer.start();
            // Automatically launch user's default browser
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                try {
                    Desktop.getDesktop().browse(new java.net.URI("http://localhost:8080/"));
                } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            System.err.println("Could not start web server: " + e.getMessage());
        }
    }
}
