import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Server {

    // 메뉴 이름 → 득표수 (등록 순서 보존)
    private static final Map<String, Integer> votes = new LinkedHashMap<>();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // 정적 파일 서빙
        server.createContext("/", Server::handleStatic);
        // 메뉴 등록 + 목록 조회
        server.createContext("/api/menus", Server::handleMenus);
        // 투표
        server.createContext("/api/vote", Server::handleVote);
        // 메뉴 삭제
        server.createContext("/api/delete", Server::handleDelete);

        server.start();
        System.out.println("서버 시작: http://localhost:8080");
    }

    // --- 정적 파일 핸들러 ---
    private static void handleStatic(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        // 루트 요청은 index.html로
        if (path.equals("/")) {
            path = "/index.html";
        }

        Path file = Paths.get("web" + path);
        if (!Files.exists(file)) {
            send(exchange, 404, "text/plain", "Not Found");
            return;
        }

        byte[] body = Files.readAllBytes(file);
        String contentType = path.endsWith(".js") ? "application/javascript; charset=utf-8"
                : "text/html; charset=utf-8";

        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, body.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(body);
        }
    }

    // --- 메뉴 목록 조회(GET) / 메뉴 등록(POST) ---
    private static void handleMenus(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        if ("GET".equals(method)) {
            // 득표수 내림차순 정렬
            String json = votes.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .map(e -> String.format("{\"name\":\"%s\",\"votes\":%d}",
                            escapeJson(e.getKey()), e.getValue()))
                    .collect(Collectors.joining(",", "[", "]"));
            send(exchange, 200, "application/json; charset=utf-8", json);

        } else if ("POST".equals(method)) {
            String body = readBody(exchange);
            String name = parseNameField(body);
            if (name == null || name.isBlank()) {
                send(exchange, 400, "text/plain", "name 필드가 없습니다");
                return;
            }
            // 이미 등록된 메뉴는 무시
            votes.putIfAbsent(name, 0);
            send(exchange, 201, "application/json; charset=utf-8", "{\"ok\":true}");

        } else {
            send(exchange, 405, "text/plain", "Method Not Allowed");
        }
    }

    // --- 투표 핸들러: POST /api/vote/{name} ---
    private static void handleVote(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            send(exchange, 405, "text/plain", "Method Not Allowed");
            return;
        }

        // 경로에서 메뉴 이름 추출: /api/vote/{name}
        String path = exchange.getRequestURI().getPath(); // e.g. /api/vote/%EC%A7%9C%EC%9E%A5%EB%A9%B4
        String encoded = path.substring("/api/vote/".length());
        String name = URLDecoder.decode(encoded, StandardCharsets.UTF_8.name());

        if (!votes.containsKey(name)) {
            send(exchange, 404, "text/plain", "메뉴를 찾을 수 없습니다");
            return;
        }

        // 득표수 +1
        votes.merge(name, 1, Integer::sum);
        send(exchange, 200, "application/json; charset=utf-8", "{\"ok\":true}");
    }

    // --- 메뉴 삭제 핸들러: DELETE /api/delete/{name} ---
    private static void handleDelete(HttpExchange exchange) throws IOException {
        if (!"DELETE".equals(exchange.getRequestMethod())) {
            send(exchange, 405, "text/plain", "Method Not Allowed");
            return;
        }

        // 경로에서 메뉴 이름 추출
        String path = exchange.getRequestURI().getPath();
        String encoded = path.substring("/api/delete/".length());
        String name = URLDecoder.decode(encoded, StandardCharsets.UTF_8.name());

        if (votes.remove(name) == null) {
            send(exchange, 404, "text/plain", "메뉴를 찾을 수 없습니다");
            return;
        }

        send(exchange, 200, "application/json; charset=utf-8", "{\"ok\":true}");
    }

    // --- 유틸 ---

    private static void send(HttpExchange exchange, int status, String contentType, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    // 간단한 JSON name 필드 파싱 ("name":"값" 형태만 처리)
    private static String parseNameField(String json) {
        int idx = json.indexOf("\"name\"");
        if (idx < 0) return null;
        int colon = json.indexOf(':', idx);
        if (colon < 0) return null;
        int open = json.indexOf('"', colon + 1);
        int close = json.indexOf('"', open + 1);
        if (open < 0 || close < 0) return null;
        return json.substring(open + 1, close);
    }

    // JSON 문자열 이스케이프 (큰따옴표, 역슬래시만 처리)
    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
