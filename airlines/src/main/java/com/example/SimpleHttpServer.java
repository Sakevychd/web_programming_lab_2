package com.example;

import com.example.dao.PlaneDao;
import com.example.model.Plane;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SimpleHttpServer {

    private static final PlaneDao planeDao = new PlaneDao();

  public static void main(String[] args) throws IOException {
    HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

    server.createContext("/planes", new PlaneListHandler());
    server.createContext("/planes/view", new PlaneViewHandler());
    server.createContext("/planes/create", new PlaneCreateHandler());
    server.createContext("/planes/edit", new PlaneEditHandler());
    server.createContext("/planes/delete", new PlaneDeleteHandler());

    // 👉 Додати 5 літаків, якщо база порожня
    if (planeDao.findAll().isEmpty()) {
        planeDao.insert(new Plane(1, "Boeing 737", 160, 1, 1));
        planeDao.insert(new Plane(2, "Airbus A320", 150, 2, 1));
        planeDao.insert(new Plane(3, "Embraer E190", 100, 3, 2));
        planeDao.insert(new Plane(4, "Sukhoi Superjet 100", 98, 4, 2));
        planeDao.insert(new Plane(5, "Bombardier CRJ200", 50, 5, 3));
        System.out.println("🛫 Inserted 5 sample planes");
    }

    server.setExecutor(null);
    System.out.println("Server started at http://localhost:8000/planes");
    server.start();
}


    // Показує список літаків з кнопками для редагування і видалення + кнопка створення нового
    static class PlaneListHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }

            List<Plane> planes = planeDao.findAll();

            StringBuilder html = new StringBuilder();
            html.append("<html><head><title>Planes</title>")
                .append("<style>")
                .append("table { border-collapse: collapse; width: 70%; }")
                .append("th, td { border: 1px solid #ccc; padding: 8px; }")
                .append("th { background-color: #eee; }")
                .append("</style>")
                .append("</head><body>");
                

            html.append("<h1>Planes List</h1>");
            html.append("<a href='/planes/create'>Create New Plane</a><br/><br/>");

            html.append("<table>");
            html.append("<tr><th>ID</th><th>Model</th><th>Capacity</th><th>Manufacturer ID</th><th>Airline ID</th><th>Actions</th></tr>");
            for (Plane p : planes) {
                html.append("<tr>")
                .append("<td>")
.append("<a href='/planes/view?id=").append(p.getId()).append("'>View</a> ")
.append("</td>")


                    .append("<td>").append(p.getId()).append("</td>")
                    .append("<td>").append(p.getModel()).append("</td>")
                    .append("<td>").append(p.getCapacity()).append("</td>")
                    .append("<td>").append(p.getManufacturerId()).append("</td>")
                    .append("<td>").append(p.getAirlineId()).append("</td>")
                    .append("<td>")
                    .append("<a href='/planes/edit?id=").append(p.getId()).append("'>Edit</a> | ")
                    .append("<a href='/planes/delete?id=").append(p.getId()).append("' onclick='return confirm(\"Delete this plane?\")'>Delete</a>")
                    .append("</td>")
                    .append("</tr>");
            }
            html.append("</table>");

            html.append("</body></html>");
            sendResponse(exchange, 200, html.toString());
        }
    }


    static class PlaneViewHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = queryToMap(query);
        if (!params.containsKey("id")) {
            sendResponse(exchange, 400, "Missing id");
            return;
        }

        int id = Integer.parseInt(params.get("id"));
        Plane plane = planeDao.findById(id);
        if (plane == null) {
            sendResponse(exchange, 404, "Plane not found");
            return;
        }

        // 🔧 Тут можна розширити: замінити ID виробника на його назву
        String manufacturerName = switch (plane.getManufacturerId()) {
            case 1 -> "Boeing";
            case 2 -> "Airbus";
            case 3 -> "Embraer";
            case 4 -> "Sukhoi";
            case 5 -> "Bombardier";
            default -> "Unknown";
        };

        String airlineName = switch (plane.getAirlineId()) {
            case 1 -> "SkyUp";
            case 2 -> "Ukraine International Airlines";
            case 3 -> "WizzAir";
            default -> "Unknown";
        };

        String html = """
            <html><head><title>Plane Details</title></head><body>
            <h1>Plane Details</h1>
            <p><strong>ID:</strong> %d</p>
            <p><strong>Model:</strong> %s</p>
            <p><strong>Capacity:</strong> %d</p>
            <p><strong>Manufacturer:</strong> %s</p>
            <p><strong>Airline:</strong> %s</p>
            <br/><a href="/planes">Back to list</a>
            </body></html>
        """.formatted(
            plane.getId(),
            escapeHtml(plane.getModel()),
            plane.getCapacity(),
            escapeHtml(manufacturerName),
            escapeHtml(airlineName)
        );

        sendResponse(exchange, 200, html);
    }
}


    // Форма створення і обробка POST для нового літака
    static class PlaneCreateHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                // Показати форму
                String html = """
                <html><head><title>Create Plane</title></head><body>
                <h1>Create New Plane</h1>
                <form method="POST" action="/planes/create">
                    Model: <input name="model" required><br/>
                    Capacity: <input type="number" name="capacity" required><br/>
                    Manufacturer ID: <input type="number" name="manufacturer_id" required><br/>
                    Airline ID: <input type="number" name="airline_id" required><br/>
                    <input type="submit" value="Create">
                </form>
                <br/><a href="/planes">Back to list</a>
                </body></html>
                """;
                sendResponse(exchange, 200, html);
            } else if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                // Обробка форми
                Map<String, String> params = parseFormData(exchange);
                try {
                    String model = params.get("model");
                    int capacity = Integer.parseInt(params.get("capacity"));
                    int manufacturerId = Integer.parseInt(params.get("manufacturer_id"));
                    int airlineId = Integer.parseInt(params.get("airline_id"));

                    Plane plane = new Plane(0, model, capacity, manufacturerId, airlineId);
                    planeDao.insert(plane);

                    // Редірект на список
                    redirect(exchange, "/planes");
                } catch (Exception e) {
                    sendResponse(exchange, 400, "Invalid input data");
                }
            } else {
                sendResponse(exchange, 405, "Method Not Allowed");
            }
        }
    }


    // Показ деталей літака
static class PlaneDetailHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = queryToMap(query);
        if (!params.containsKey("id")) {
            sendResponse(exchange, 400, "Missing id");
            return;
        }

        int id = Integer.parseInt(params.get("id"));
        Plane plane = planeDao.findById(id);
        if (plane == null) {
            sendResponse(exchange, 404, "Plane not found");
            return;
        }

        String html = """
        <html>
        <head><title>Plane Details</title></head>
        <body>
            <h1>Plane Details</h1>
            <p><strong>ID:</strong> %d</p>
            <p><strong>Model:</strong> %s</p>
            <p><strong>Capacity:</strong> %d</p>
            <p><strong>Manufacturer ID:</strong> %d</p>
            <p><strong>Airline ID:</strong> %d</p>
            <br/><a href="/planes">Back to list</a>
        </body>
        </html>
        """.formatted(
            plane.getId(),
            escapeHtml(plane.getModel()),
            plane.getCapacity(),
            plane.getManufacturerId(),
            plane.getAirlineId()
        );

        sendResponse(exchange, 200, html);
    }
}

    // Форма редагування і обробка POST для оновлення
    static class PlaneEditHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                String query = exchange.getRequestURI().getQuery();
                Map<String, String> params = queryToMap(query);
                String idStr = params.get("id");
                if (idStr == null) {
                    sendResponse(exchange, 400, "Missing id");
                    return;
                }
                int id = Integer.parseInt(idStr);
                Plane plane = planeDao.findById(id);
                if (plane == null) {
                    sendResponse(exchange, 404, "Plane not found");
                    return;
                }

                String html = String.format("""
                <html><head><title>Edit Plane</title></head><body>
                <h1>Edit Plane</h1>
                <form method="POST" action="/planes/edit?id=%d">
                    Model: <input name="model" value="%s" required><br/>
                    Capacity: <input type="number" name="capacity" value="%d" required><br/>
                    Manufacturer ID: <input type="number" name="manufacturer_id" value="%d" required><br/>
                    Airline ID: <input type="number" name="airline_id" value="%d" required><br/>
                    <input type="submit" value="Update">
                </form>
                <br/><a href="/planes">Back to list</a>
                </body></html>
                """, plane.getId(), escapeHtml(plane.getModel()), plane.getCapacity(), plane.getManufacturerId(), plane.getAirlineId());

                sendResponse(exchange, 200, html);
            } else if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                String query = exchange.getRequestURI().getQuery();
                Map<String, String> paramsQuery = queryToMap(query);
                if (!paramsQuery.containsKey("id")) {
                    sendResponse(exchange, 400, "Missing id");
                    return;
                }
                int id = Integer.parseInt(paramsQuery.get("id"));

                Map<String, String> paramsForm = parseFormData(exchange);

                try {
                    String model = paramsForm.get("model");
                    int capacity = Integer.parseInt(paramsForm.get("capacity"));
                    int manufacturerId = Integer.parseInt(paramsForm.get("manufacturer_id"));
                    int airlineId = Integer.parseInt(paramsForm.get("airline_id"));

                    Plane plane = new Plane(id, model, capacity, manufacturerId, airlineId);
                    planeDao.update(plane);

                    redirect(exchange, "/planes");
                } catch (Exception e) {
                    sendResponse(exchange, 400, "Invalid input data");
                }
            } else {
                sendResponse(exchange, 405, "Method Not Allowed");
            }
        }
    }

    // Видалення літака за id (GET з параметром)
    static class PlaneDeleteHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }

            String query = exchange.getRequestURI().getQuery();
            Map<String, String> params = queryToMap(query);
            if (!params.containsKey("id")) {
                sendResponse(exchange, 400, "Missing id");
                return;
            }

            int id = Integer.parseInt(params.get("id"));
            planeDao.delete(id);

            redirect(exchange, "/planes");
        }
    }

    // --- Допоміжні методи ---

    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void redirect(HttpExchange exchange, String location) throws IOException {
        exchange.getResponseHeaders().set("Location", location);
        exchange.sendResponseHeaders(302, -1);
        exchange.close();
    }

    private static Map<String, String> parseFormData(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder buf = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            buf.append(line);
        }
        String[] pairs = buf.toString().split("&");
        Map<String, String> params = new HashMap<>();
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length == 2) {
                params.put(URLDecoder.decode(kv[0], "UTF-8"), URLDecoder.decode(kv[1], "UTF-8"));
            }
        }
        return params;
    }

    private static Map<String, String> queryToMap(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isEmpty()) return map;
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length == 2) {
                map.put(kv[0], kv[1]);
            }
        }
        return map;
    }

    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
