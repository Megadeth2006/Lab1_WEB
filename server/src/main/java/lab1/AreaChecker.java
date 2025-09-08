package lab1;

/**
 * Java port of PHP AreaChecker class
 * Checks if a point (x, y) falls within the specified area for radius r
 */
public class AreaChecker {
    
    /**
     * Checks if the point (x, y) is within the area defined by radius r
     * 
     * Area consists of:
     * 1. Rectangle in first quadrant: (0 ≤ x ≤ R/2, 0 ≤ y ≤ R)
     * 2. Triangle in fourth quadrant: vertices at (0,0), (R,0), (0,-R/2)
     * 3. Quarter circle in third quadrant: center at (0,0), radius R
     * 4. Second quadrant: always false
     * 
     * @param x X coordinate
     * @param y Y coordinate  
     * @param r Radius
     * @return true if point is in area, false otherwise
     */
    public static boolean isInArea(double x, double y, double r) {
        // Rectangle in first quadrant (x=0 to x=R/2, y=0 to y=R)
        if (x >= 0 && y >= 0) {
            return (x <= r / 2) && (y <= r);
        }
        
        // Triangle in fourth quadrant (vertices at (0,0), (R,0), (0,-R/2))
        if (x >= 0 && y < 0) {
            return (x <= r) && (y >= -r / 2) && (x - 2 * y <= r);
        }
        
        // Quarter circle in third quadrant (center at (0,0), radius R)
        if (x < 0 && y < 0) {
            return (x * x + y * y) <= (r * r);
        }
        
        return false; // Second quadrant is always false
    }
}
