package lab1;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Session manager for storing calculation results
 * Replaces PHP session functionality
 */
public class SessionManager {
    
    private static final Map<String, List<CalculationResult>> sessions = new ConcurrentHashMap<>();
    
    /**
     * Represents a single calculation result
     */
    public static class CalculationResult {
        private final double x;
        private final double y;
        private final double r;
        private final boolean isInArea;
        private final String currentTime;
        private final double executionTime;
        
        public CalculationResult(double x, double y, double r, boolean isInArea,
        String currentTime, double executionTime) {
            this.x = x;
            this.y = y;
            this.r = r;
            this.isInArea = isInArea;
            this.currentTime = currentTime;
            this.executionTime = executionTime;
        }
        
        // Getters
        public double getX() { return x; }
        public double getY() { return y; }
        public double getR() { return r; }
        public boolean isInArea() { return isInArea; }
        public String getCurrentTime() { return currentTime; }
        public double getExecutionTime() { return executionTime; }
    }
    
    /**
     * Gets or creates a session
     * @param sessionId Session identifier
     * @return List of calculation results for this session
     */
    public static List<CalculationResult> getSession(String sessionId) {
        return sessions.computeIfAbsent(sessionId, k -> new ArrayList<>());
    }
    
    /**
     * Adds a new calculation result to the session
     * @param sessionId Session identifier
     * @param result Calculation result to add
     */
    public static void addResult(String sessionId, CalculationResult result) {
        List<CalculationResult> session = getSession(sessionId);
        session.add(result);
    }
    
    /**
     * Gets all results for a session in reverse order (newest first)
     * @param sessionId Session identifier
     * @return List of results in reverse chronological order
     */
    public static List<CalculationResult> getResults(String sessionId) {
        List<CalculationResult> session = getSession(sessionId);
        List<CalculationResult> reversed = new ArrayList<>(session);
        Collections.reverse(reversed);
        return reversed;
    }
    
    /**
     * Clears a session
     * @param sessionId Session identifier
     */
    public static void clearSession(String sessionId) {
        sessions.remove(sessionId);
    }
}
