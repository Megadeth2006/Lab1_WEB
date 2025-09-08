package lab1;

/**
 * Java port of PHP CoordinatesValidator class
 * Validates input coordinates and radius values
 */
public class CoordinatesValidator {
    
    private final double x;
    private final double y;
    private final double r;
    
    // Valid X values
    private static final double[] VALID_X_VALUES = {-3, -2, -1, 0, 1, 2, 3, 4, 5};
    
    // Valid R values
    private static final double[] VALID_R_VALUES = {1, 1.5, 2, 2.5, 3};
    
    // Y range
    private static final double Y_MIN = -5.0;
    private static final double Y_MAX = 5.0;
    
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
        for (double validX : VALID_X_VALUES) {
            if (Math.abs(x - validX) < 1e-9) { // Handle floating point comparison
                return true;
            }
        }
        return false;
    }
    
    /**
     * Validates Y coordinate
     * @return true if Y is numeric and in valid range, false otherwise
     */
    private boolean checkY() {
        return !Double.isNaN(y) && !Double.isInfinite(y) && 
               (y > Y_MIN && y < Y_MAX);
    }
    
    /**
     * Validates R radius
     * @return true if R is in valid range, false otherwise
     */
    private boolean checkR() {
        for (double validR : VALID_R_VALUES) {
            if (Math.abs(r - validR) < 1e-9) { // Handle floating point comparison
                return true;
            }
        }
        return false;
    }
    
    // Getters for debugging
    public double getX() { return x; }
    public double getY() { return y; }
    public double getR() { return r; }
}
