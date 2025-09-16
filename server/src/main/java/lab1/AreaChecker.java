package lab1;


public class AreaChecker {
    
   
    public static boolean isInArea(double x, double y, double r) {
        // Прямоугольник в первой четверти
        if (x >= 0 && y >= 0) {
            return (x <= r / 2) && (y <= r);
        }
        
        // Треугольник в четвертой четверти
        if (x >= 0 && y < 0) {
            return (x <= r) && (y >= -r / 2) && (x - 2 * y <= r);
        }
        
        // четверть круга
        if (x < 0 && y < 0) {
            return (x * x + y * y) <= (r * r);
        }
        
        return false; 
    }
}
