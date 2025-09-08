package lab1;

/**
 * Java port of PHP CoordinatesValidator class
 * Validates input coordinates and radius values
 */
public class CoordinatesValidator {
    
    private final double x;
    private final double y;
    private final double r;
    
    // X range
    private static final double X_MIN = -5.0;
    private static final double X_MAX = 5.0;
    
    // Valid Y values
    private static final double[] VALID_Y_VALUES = {-4, -3, -2, -1, 0, 1, 2, 3, 4};
    
    // R range
    private static final double R_MIN = 2.0;
    private static final double R_MAX = 5.0;
    
    public CoordinatesValidator(double x, double y, double r) {
        this.x = x;
        this.y = y;
        this.r = r;
    }
    
    /**
     * Validates all coordinates and radius
     * @return true if all values are valid, false otherwise
     */
    public boolean checkData() {
        return checkX() && checkY() && checkR();
    }
    
    /**
     * Validates X coordinate
     * @return true if X is in valid range, false otherwise
     */
    private boolean checkX() {
        return !Double.isNaN(x) && !Double.isInfinite(x) && 
               (x > X_MIN && x < X_MAX);
    }
    
    /**
     * Validates Y coordinate
     * @return true if Y is in valid list, false otherwise
     */
    private boolean checkY() {
        for (double validY : VALID_Y_VALUES) {
            if (Math.abs(y - validY) < 1e-9) { // Handle floating point comparison
                return true;
            }
        }
        return false;
    }
    
    /**
     * Validates R radius
     * @return true if R is in valid range, false otherwise
     */
    private boolean checkR() {
        return !Double.isNaN(r) && !Double.isInfinite(r) && 
               (r > R_MIN && r < R_MAX);
    }
    
    // Getters for debugging
    public double getX() { return x; }
    public double getY() { return y; }
    public double getR() { return r; }
}
