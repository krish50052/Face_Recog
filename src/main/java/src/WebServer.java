package src;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;

/**
 * Minimal HTTP adapter for the existing face-recognition processing code.
 * The Swing desktop application remains available through FrontEnd.
 */
public final class WebServer {
    private static final int DEFAULT_PORT = 8080;
    private static final int MAX_UPLOAD_BYTES = 10 * 1024 * 1024;

    private WebServer() {
    }

    public static void main(String[] args) throws IOException {
        int port = readPort();
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
        server.createContext("/", WebServer::serveIndex);
        server.createContext("/health", WebServer::serveHealth);
        server.createContext("/api/recognize", WebServer::recognize);
        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();
        System.out.println("Face recognition web service listening on port " + port);
    }

    private static int readPort() {
        String configuredPort = System.getenv("PORT");
        if (configuredPort == null || configuredPort.isBlank()) {
            return DEFAULT_PORT;
        }
        try {
            return Integer.parseInt(configuredPort);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("PORT must be a valid number", exception);
        }
    }

    private static void serveIndex(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod()) || !"/".equals(exchange.getRequestURI().getPath())) {
            sendText(exchange, 404, "Not found");
            return;
        }
        try (InputStream page = WebServer.class.getResourceAsStream("/index.html")) {
            if (page == null) {
                sendText(exchange, 500, "Web interface is unavailable");
                return;
            }
            byte[] content = page.readAllBytes();
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
            exchange.sendResponseHeaders(200, content.length);
            try (OutputStream output = exchange.getResponseBody()) {
                output.write(content);
            }
        }
    }

    private static void serveHealth(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendText(exchange, 405, "Method not allowed");
            return;
        }
        sendJson(exchange, 200, "{\"status\":\"ok\",\"service\":\"face-recognition\"}");
    }

    private static void recognize(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendText(exchange, 405, "Method not allowed");
            return;
        }

        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        if (contentType == null || !contentType.toLowerCase().startsWith("image/")) {
            sendJson(exchange, 415, "{\"error\":\"Send an image with an image/* Content-Type\"}");
            return;
        }

        long contentLength = readContentLength(exchange);
        if (contentLength > MAX_UPLOAD_BYTES) {
            sendJson(exchange, 413, "{\"error\":\"Image is larger than 10 MB\"}");
            return;
        }

        Path upload = Files.createTempFile("face-recognition-", ".image");
        try {
            try (InputStream input = exchange.getRequestBody()) {
                copyWithLimit(input, upload, MAX_UPLOAD_BYTES);
            }
            Face face = new Face(upload.toFile());
            face.load(true);
            int width = face.getPicture().getImage().getWidth();
            int height = face.getPicture().getImage().getHeight();
            sendJson(exchange, 200, String.format(
                "{\"status\":\"processed\",\"width\":%d,\"height\":%d,\"classification\":null,\"message\":\"Image processed; upload training data to enable classification.\"}",
                width, height
            ));
        } catch (IllegalArgumentException exception) {
            sendJson(exchange, 400, "{\"error\":\"Invalid image upload\"}");
        } catch (Exception exception) {
            sendJson(exchange, 422, "{\"error\":\"Image could not be processed\"}");
        } finally {
            Files.deleteIfExists(upload);
        }
    }

    private static void copyWithLimit(InputStream input, Path destination, int limit) throws IOException {
        byte[] buffer = new byte[8192];
        int total = 0;
        int read;
        try (OutputStream output = Files.newOutputStream(destination)) {
            while ((read = input.read(buffer)) != -1) {
                total += read;
                if (total > limit) {
                    throw new IllegalArgumentException("Image is too large");
                }
                output.write(buffer, 0, read);
            }
        }
        if (total == 0) {
            throw new IllegalArgumentException("Image is empty");
        }
    }

    private static long readContentLength(HttpExchange exchange) {
        String value = exchange.getRequestHeaders().getFirst("Content-Length");
        if (value == null) {
            return -1;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    private static void sendJson(HttpExchange exchange, int status, String body) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        sendText(exchange, status, body);
    }

    private static void sendText(HttpExchange exchange, int status, String body) throws IOException {
        byte[] content = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, content.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(content);
        }
    }
}
