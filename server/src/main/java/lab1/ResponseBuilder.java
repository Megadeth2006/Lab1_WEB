package lab1;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Builds HTML responses for the area checker application
 */
public class ResponseBuilder {
    
    private static final DateTimeFormatter TIME_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Builds HTML table rows for calculation results
     * @param results List of calculation results
     * @return HTML string with table rows
     */
    public static String buildResultsTable(List<SessionManager.CalculationResult> results) {
        StringBuilder html = new StringBuilder();
        
        for (SessionManager.CalculationResult result : results) {
            html.append("<tr>");
            html.append("<td>").append(result.getX()).append("</td>");
            html.append("<td>").append(result.getY()).append("</td>");
            html.append("<td>").append(result.getR()).append("</td>");
            
            // Add result with appropriate CSS class
            if (result.isInArea()) {
                html.append("<td><span class='result-cell-in'>Hit</span></td>");
            } else {
                html.append("<td><span class='result-cell-out'>Didn't hit</span></td>");
            }
            
            html.append("<td>").append(result.getCurrentTime()).append("</td>");
            html.append("<td>").append(String.format("%.6f", result.getExecutionTime())).append("</td>");
            html.append("</tr>");
        }
        
        return html.toString();
    }
    
    /**
     * Builds error response
     * @param message Error message
     * @return HTML error response
     */
    public static String buildErrorResponse(String message) {
        return "<tr><td colspan='6' style='color: red; text-align: center;'>" + 
               message + "</td></tr>";
    }
    
    /**
     * Gets current timestamp
     * @return Formatted current time string
     */
    public static String getCurrentTime() {
        return LocalDateTime.now().format(TIME_FORMATTER);
    }
}
