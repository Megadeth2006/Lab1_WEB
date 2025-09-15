package lab1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple HTTP server for area checking application
 */
public class FastCGIServer {
    
    private static final Logger logger = LoggerFactory.getLogger(FastCGIServer.class);
    private static final int PORT = 8080;
    
    public static void main(String[] args) {
        try {
            FastCGIServer server = new FastCGIServer();
            server.start();
        } catch (Exception e) {
            logger.error("Failed to start HTTP server", e);
            System.exit(1);
        }
    }
    
    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.info("Starting HTTP server on port {}...", PORT);
            
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    new Thread(() -> handleRequest(clientSocket)).start();
                } catch (IOException e) {
                    logger.error("Error accepting connection", e);
                }
            }
        }
    }
    
    private void handleRequest(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
            
            String requestLine = in.readLine();
            if (requestLine == null) return;
            
            logger.info("Received request: {}", requestLine);
            
            // Read all headers to avoid blocking
            String headerLine;
            int contentLength = 0;
            while ((headerLine = in.readLine()) != null && !headerLine.isEmpty()) {
                logger.debug("Header: {}", headerLine);
                if (headerLine.toLowerCase().startsWith("content-length:")) {
                    contentLength = Integer.parseInt(headerLine.substring(15).trim());
                }
            }
            
            // Parse request
            String[] parts = requestLine.split(" ");
            if (parts.length < 2) {
                sendErrorResponse(out, 400, "Bad Request");
                return;
            }
            
            String method = parts[0];
            String path = parts[1];
            
            // Handle CORS preflight requests
            if ("OPTIONS".equals(method)) {
                sendCorsResponse(out);
                return;
            }
            
            if (!"GET".equals(method) && !"POST".equals(method)) {
                sendErrorResponse(out, 405, "Method Not Allowed");
                return;
            }
            
            if (!path.startsWith("/script")) {
                sendErrorResponse(out, 404, "Not Found");
                return;
            }
            
            // Parse parameters based on method
            Map<String, String> params;
            if ("POST".equals(method)) {
                params = parsePostParams(in, contentLength);
            } else {
                params = parseQueryParams(path);
            }
            
            // Handle GET request for loading saved results
            if ("GET".equals(method)) {
                String sessionId = params.get("sessionId");
                if (sessionId != null && !sessionId.trim().isEmpty()) {
                    var allResults = SessionManager.getResults(sessionId.trim());
                    String htmlResponse = ResponseBuilder.buildResultsTable(allResults);
                    sendSuccessResponse(out, htmlResponse);
                    return;
                } else {
                    sendErrorResponse(out, 400, "Missing sessionId parameter");
                    return;
                }
            }
            
            String xStr = params.get("xVal");
            String yStr = params.get("yVal");
            String rStr = params.get("rVal");
            
            if (xStr == null || yStr == null || rStr == null) {
                sendErrorResponse(out, 400, "Missing required parameters");
                return;
            }
            
            long startTime = System.nanoTime();
            
            // Parse and validate coordinates
            double x, y, r;
            try {
                x = Double.parseDouble(xStr);
                y = Double.parseDouble(yStr);
                r = Double.parseDouble(rStr);
            } catch (NumberFormatException e) {
                sendErrorResponse(out, 422, "Invalid number format");
                return;
            }
            
            // Validate coordinates
            CoordinatesValidator validator = new CoordinatesValidator(x, y, r);
            if (!validator.checkData()) {
                sendErrorResponse(out, 422, "Invalid data, try again :)");
                return;
            }
            
            // Check if point is in area
            boolean isInArea = AreaChecker.isInArea(x, y, r);
            
            // Calculate execution time
            double executionTime = (System.nanoTime() - startTime) / 1_000_000.0;
            
            // Get current time
            String currentTime = ResponseBuilder.getCurrentTime();
            
            // Create result
            SessionManager.CalculationResult result = new SessionManager.CalculationResult(
                x, y, r, isInArea, currentTime, executionTime
            );
            
            // Generate or extract session ID from request
            String sessionId = extractSessionId(requestLine, params);
            SessionManager.addResult(sessionId, result);
            
            // Get all results for this session
            var allResults = SessionManager.getResults(sessionId);
            
            // Build and send response
            String htmlResponse = ResponseBuilder.buildResultsTable(allResults);
            sendSuccessResponse(out, htmlResponse);
            
            logger.info("Processed request: x={}, y={}, r={}, result={}, time={}ms", 
                       x, y, r, isInArea, executionTime);
            
        } catch (Exception e) {
            logger.error("Error processing request", e);
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                logger.error("Error closing socket", e);
            }
        }
    }
    
    private Map<String, String> parseQueryParams(String path) {
        Map<String, String> params = new HashMap<>();
        
        int queryIndex = path.indexOf('?');
        if (queryIndex == -1) return params;
        
        String query = path.substring(queryIndex + 1);
        String[] pairs = query.split("&");
        
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                try {
                    String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                    String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                    params.put(key, value);
                } catch (Exception e) {
                    logger.warn("Error parsing parameter: {}", pair);
                }
            }
        }
        
        return params;
    }
    
    private Map<String, String> parsePostParams(BufferedReader in, int contentLength) {
        Map<String, String> params = new HashMap<>();
        
        try {
            
            // Read POST body
            if (contentLength > 0) {
                char[] body = new char[contentLength];
                int totalRead = 0;
                while (totalRead < contentLength) {
                    int read = in.read(body, totalRead, contentLength - totalRead);
                    if (read == -1) break;
                    totalRead += read;
                }
                
                String bodyStr = new String(body, 0, totalRead);
                logger.debug("POST body: {}", bodyStr);
                
                // Parse form data
                String[] pairs = bodyStr.split("&");
                for (String pair : pairs) {
                    String[] keyValue = pair.split("=", 2);
                    if (keyValue.length == 2) {
                        try {
                            String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                            String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                            params.put(key, value);
                        } catch (Exception e) {
                            logger.warn("Error parsing POST parameter: {}", pair);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error parsing POST parameters", e);
        }
        
        return params;
    }
    
    private void sendSuccessResponse(PrintWriter out, String body) {
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html; charset=UTF-8");
        out.println("Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length);
        out.println("Access-Control-Allow-Origin: *");
        out.println("Access-Control-Allow-Methods: GET, POST, OPTIONS");
        out.println("Access-Control-Allow-Headers: Content-Type");
        out.println();
        out.println(body);
    }
    
    private void sendCorsResponse(PrintWriter out) {
        out.println("HTTP/1.1 200 OK");
        out.println("Access-Control-Allow-Origin: *");
        out.println("Access-Control-Allow-Methods: GET, POST, OPTIONS");
        out.println("Access-Control-Allow-Headers: Content-Type");
        out.println("Content-Length: 0");
        out.println();
    }
    
    private void sendErrorResponse(PrintWriter out, int statusCode, String message) {
        String body = "<html><body><h1>Error " + statusCode + "</h1><p>" + message + "</p></body></html>";
        out.println("HTTP/1.1 " + statusCode + " " + getStatusText(statusCode));
        out.println("Content-Type: text/html; charset=UTF-8");
        out.println("Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length);
        out.println();
        out.println(body);
    }
    
    private String getStatusText(int statusCode) {
        switch (statusCode) {
            case 400: return "Bad Request";
            case 404: return "Not Found";
            case 405: return "Method Not Allowed";
            case 422: return "Unprocessable Entity";
            case 500: return "Internal Server Error";
            default: return "Unknown";
        }
    }
    
    /**
     * Extracts or generates session ID from request
     * @param requestLine HTTP request line
     * @param params Request parameters
     * @return Session ID
     */
    private String extractSessionId(String requestLine, Map<String, String> params) {
        // Try to get session ID from parameters first
        String sessionId = params.get("sessionId");
        if (sessionId != null && !sessionId.trim().isEmpty()) {
            return sessionId.trim();
        }
        
        // Try to get session ID from User-Agent header (if available)
        // For now, generate a unique session ID based on timestamp and random
        return "session_" + System.currentTimeMillis() + "_" + 
               Integer.toHexString((int)(Math.random() * 10000));
    }
}
