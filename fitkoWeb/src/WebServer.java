import com.sun.net.httpserver.*;
import data.*;
import domain.*;
import service.*;
import interfaces.*;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class WebServer {

    public static void main(String[] args) throws Exception {

        if (isDatabaseEmpty()) {
            DB.seed();
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);

        // API endpoints
        server.createContext("/api/lessons", new LessonsHandler());
        server.createContext("/api/reservations", new ReservationsHandler());
        server.createContext("/api/reservations/rate", new RatingHandler());
        server.createContext("/api/customer", new CustomerHandler());

        server.createContext("/", new StaticHandler());

        server.setExecutor(null);
        server.start();

        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║       FITKO Web Server - RUNNING                    ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("   Open in browser:");
        System.out.println("   http://localhost:8081/web/rezervace_lekce.html");
        System.out.println("   http://localhost:8081/web/hodnoceni_lekce.html");
        System.out.println();
        System.out.println("Press Ctrl+C to stop...");
    }


    //HANDLERS
    static class LessonsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            setCORS(ex);
            if ("OPTIONS".equals(ex.getRequestMethod())) {
                ex.sendResponseHeaders(200, -1);
                return;
            }

            try {
                LekceGateway gw = new LekceGateway();
                List<LekceDto> lessons = gw.findAll();

                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < lessons.size(); i++) {
                    if (i > 0) json.append(",");
                    LekceDto l = lessons.get(i);
                    json.append(String.format(
                            "{\"id\":%d,\"title\":\"%s\",\"trainer\":\"%s\",\"day\":\"%s\",\"time\":\"%s\",\"capacity\":%d,\"loggedIn\":%d,\"price\":%d}",
                            l.id, esc(l.title), esc(l.trainer), esc(l.day), esc(l.time), l.capacity, l.loggedIn, l.price
                    ));
                }
                json.append("]");

                sendJSON(ex, 200, json.toString());
            } catch (Exception e) {
                e.printStackTrace();
                sendJSON(ex, 500, "{\"error\":\"" + esc(e.getMessage()) + "\"}");
            }
        }
    }

    static class ReservationsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            setCORS(ex);
            if ("OPTIONS".equals(ex.getRequestMethod())) {
                ex.sendResponseHeaders(200, -1);
                return;
            }

            if ("POST".equals(ex.getRequestMethod())) {
                handleCreate(ex);
            } else if ("GET".equals(ex.getRequestMethod())) {
                handleGet(ex);
            } else {
                sendJSON(ex, 405, "{\"error\":\"Method not allowed\"}");
            }
        }

        private void handleCreate(HttpExchange ex) throws IOException {
            try {
                String body = readBody(ex);
                Map<String, String> params = parseJSON(body);

                int customerId = Integer.parseInt(params.get("customerId"));
                int lessonId = Integer.parseInt(params.get("lessonId"));
                String date = params.get("date");

                // Load customer
                ZakaznikGateway zakGw = new ZakaznikGateway();
                ZakaznikDto zakDto = zakGw.findById(customerId);

                if (zakDto == null) {
                    sendJSON(ex, 404, "{\"error\":\"Customer not found\"}");
                    return;
                }

                Zakaznik zakaznik = new osobniZakaznik(zakDto.id, zakDto.name, zakDto.email,
                        zakDto.credit, "", "", "");

                // Load lesson
                LekceGateway lekGw = new LekceGateway();
                List<LekceDto> allLessons = lekGw.findAll();
                LekceDto lekDto = null;
                for (LekceDto l : allLessons) {
                    if (l.id == lessonId) {
                        lekDto = l;
                        break;
                    }
                }

                if (lekDto == null) {
                    sendJSON(ex, 404, "{\"error\":\"Lesson not found\"}");
                    return;
                }

                Lekce lekce = new Lekce(lekDto.id, lekDto.title, lekDto.trainer,
                        lekDto.day, lekDto.time, lekDto.capacity, lekDto.price);
                lekce.loggedIn = lekDto.loggedIn;

                // Create reservation
                ReservationGateway resGw = new ReservationGateway();
                ReservationUnitOfWork uow = new ReservationUnitOfWork(resGw);
                ReservationService service = new ReservationService(uow,
                        new NotificationServiceStub(), new PaymentGatewaySim());

                service.createReservation(zakaznik, lekce, date);

                // Update customer credits in DB
                zakDto.credit = zakaznik.credit;
                zakGw.update(zakDto);

                // Update lesson capacity in DB
                lekDto.loggedIn = lekce.loggedIn;
                lekGw.update(lekDto);

                String response = String.format(
                        "{\"success\":true,\"message\":\"Reservation created\",\"newCredits\":%d}",
                        zakaznik.credit
                );
                sendJSON(ex, 200, response);

            } catch (Exception e) {
                e.printStackTrace();
                sendJSON(ex, 500, "{\"error\":\"" + esc(e.getMessage()) + "\"}");
            }
        }

        private void handleGet(HttpExchange ex) throws IOException {
            String query = ex.getRequestURI().getQuery();
            if (query == null || !query.startsWith("customerId=")) {
                sendJSON(ex, 400, "{\"error\":\"Missing customerId\"}");
                return;
            }

            try {
                int customerId = Integer.parseInt(query.split("=")[1]);
                ReservationGateway gw = new ReservationGateway();
                List<ReservationDto> reservations = gw.findByZakaznikId(customerId);

                LekceGateway lekGw = new LekceGateway();
                List<LekceDto> lessons = lekGw.findAll();

                StringBuilder json = new StringBuilder("[");
                int count = 0;
                for (ReservationDto r : reservations) {
                    if ("DOKONCENA".equals(r.stav) || "POTVRZENA".equals(r.stav)) {
                        if (count > 0) json.append(",");

                        // Find lesson details
                        String lessonTitle = "";
                        String trainer = "";
                        for (LekceDto l : lessons) {
                            if (l.id == r.lekceId) {
                                lessonTitle = l.title;
                                trainer = l.trainer;
                                break;
                            }
                        }

                        json.append(String.format(
                                "{\"id\":%d,\"lessonTitle\":\"%s\",\"trainer\":\"%s\",\"datum\":\"%s\",\"rating\":%s,\"review\":\"%s\"}",
                                r.id, esc(lessonTitle), esc(trainer), esc(r.datum),
                                r.rating == null ? "null" : r.rating,
                                esc(r.review != null ? r.review : "")
                        ));
                        count++;
                    }
                }
                json.append("]");

                sendJSON(ex, 200, json.toString());
            } catch (Exception e) {
                e.printStackTrace();
                sendJSON(ex, 500, "{\"error\":\"" + esc(e.getMessage()) + "\"}");
            }
        }
    }

    static class RatingHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            setCORS(ex);
            if ("OPTIONS".equals(ex.getRequestMethod())) {
                ex.sendResponseHeaders(200, -1);
                return;
            }

            if (!"POST".equals(ex.getRequestMethod())) {
                sendJSON(ex, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }

            try {
                String body = readBody(ex);
                Map<String, String> params = parseJSON(body);

                int reservationId = Integer.parseInt(params.get("reservationId"));
                int rating = Integer.parseInt(params.get("rating"));
                String review = params.getOrDefault("review", "");

                ReservationGateway gw = new ReservationGateway();
                ReservationUnitOfWork uow = new ReservationUnitOfWork(gw);
                ReservationService service = new ReservationService(uow);

                service.addRating(reservationId, rating, review);

                sendJSON(ex, 200, "{\"success\":true,\"message\":\"Rating saved\"}");
            } catch (Exception e) {
                e.printStackTrace();
                sendJSON(ex, 500, "{\"error\":\"" + esc(e.getMessage()) + "\"}");
            }
        }
    }

    static class CustomerHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            setCORS(ex);
            if ("OPTIONS".equals(ex.getRequestMethod())) {
                ex.sendResponseHeaders(200, -1);
                return;
            }

            String query = ex.getRequestURI().getQuery();
            if (query == null || !query.startsWith("id=")) {
                sendJSON(ex, 400, "{\"error\":\"Missing id parameter\"}");
                return;
            }

            try {
                int id = Integer.parseInt(query.split("=")[1]);
                ZakaznikGateway gw = new ZakaznikGateway();
                ZakaznikDto z = gw.findById(id);

                if (z == null) {
                    sendJSON(ex, 404, "{\"error\":\"Customer not found\"}");
                    return;
                }

                String json = String.format(
                        "{\"id\":%d,\"name\":\"%s\",\"email\":\"%s\",\"credit\":%d}",
                        z.id, esc(z.name), esc(z.email), z.credit
                );
                sendJSON(ex, 200, json);
            } catch (Exception e) {
                e.printStackTrace();
                sendJSON(ex, 500, "{\"error\":\"" + esc(e.getMessage()) + "\"}");
            }
        }
    }

    static class StaticHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            String path = ex.getRequestURI().getPath();
            if ("/".equals(path)) path = "/web/rezervace_lekce.html";

            File file = new File("." + path);
            if (!file.exists() || file.isDirectory()) {
                String html = "<h1>404 Not Found</h1><p>" + path + "</p>";
                send(ex, 404, html, "text/html");
                return;
            }

            String type = "text/html";
            if (path.endsWith(".css")) type = "text/css";
            if (path.endsWith(".js")) type = "application/javascript";

            byte[] bytes = Files.readAllBytes(file.toPath());
            ex.getResponseHeaders().set("Content-Type", type);
            ex.sendResponseHeaders(200, bytes.length);
            ex.getResponseBody().write(bytes);
            ex.getResponseBody().close();
        }
    }

    // Helpers
    static void setCORS(HttpExchange ex) {
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().set("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
        ex.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }

    static void sendJSON(HttpExchange ex, int code, String json) throws IOException {
        send(ex, code, json, "application/json");
    }

    static void send(HttpExchange ex, int code, String text, String type) throws IOException {
        ex.getResponseHeaders().set("Content-Type", type);
        byte[] bytes = text.getBytes();
        ex.sendResponseHeaders(code, bytes.length);
        ex.getResponseBody().write(bytes);
        ex.getResponseBody().close();
    }

    static String readBody(HttpExchange ex) throws IOException {
        return new BufferedReader(new InputStreamReader(ex.getRequestBody()))
                .lines()
                .reduce("", (a, b) -> a + b);
    }

    static Map<String, String> parseJSON(String json) {
        Map<String, String> map = new HashMap<>();
        json = json.trim().replace("{", "").replace("}", "");
        for (String pair : json.split(",")) {
            String[] kv = pair.split(":");
            if (kv.length == 2) {
                String key = kv[0].trim().replace("\"", "");
                String val = kv[1].trim().replace("\"", "");
                map.put(key, val);
            }
        }
        return map;
    }

    static String esc(String s) {
        if (s == null) return "";
        return s.replace("\"", "\\\"").replace("\n", "\\n");
    }

    private static boolean isDatabaseEmpty() throws SQLException {
        try (Connection c = DB.get();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM Lekce")) {
            return rs.next() && rs.getInt(1) == 0;
        }
    }
}