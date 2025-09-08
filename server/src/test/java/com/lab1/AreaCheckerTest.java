package com.lab1;

import lab1.AreaChecker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Java port of PHP AreaTest class
 */
class AreaCheckerTest {

    @ParameterizedTest
    @MethodSource("areaCheckProvider")
    void testIsInArea(double x, double y, double r, boolean expected) {
        assertEquals(expected, AreaChecker.isInArea(x, y, r));
    }

    static Stream<Arguments> areaCheckProvider() {
        return Stream.of(
            // Points in the first quadrant (rectangle check)
            Arguments.of(0.5, 0.5, 2, true),   // First Quadrant Inside Rectangle
            Arguments.of(1.1, 0.5, 2, false),  // First Quadrant Outside Rectangle
            Arguments.of(0.5, 2.1, 2, false),  // First Quadrant Outside Rectangle Y
            
            // Points in the fourth quadrant (triangle check)
            Arguments.of(0.5, -0.2, 2, true),  // Fourth Quadrant Inside Triangle
            Arguments.of(2.1, -0.5, 2, false), // Fourth Quadrant Outside Triangle
            Arguments.of(0.5, -1.1, 2, false), // Fourth Quadrant Outside Triangle Y
            
            // Points in the third quadrant (circle check)
            Arguments.of(-0.5, -0.5, 2, true), // Third Quadrant Inside Circle
            Arguments.of(-1.5, -1.5, 2, false), // Third Quadrant Outside Circle
            
            // Points in the second quadrant (always outside)
            Arguments.of(-0.5, 0.5, 2, false), // Second Quadrant Always Outside
            
            // Edge cases
            Arguments.of(0, 0, 1, true),       // Origin point
            Arguments.of(1, 0, 2, true),       // On X axis
            Arguments.of(0, 1, 2, true),       // On Y axis
            Arguments.of(-1, 0, 2, false),     // Negative X axis
            Arguments.of(0, -1, 2, true),      // Negative Y axis (in triangle)
            
            // Boundary tests
            Arguments.of(1.0, 2.0, 2, true),   // Exactly on rectangle boundary
            Arguments.of(1.1, 2.0, 2, false),  // Just outside rectangle
            Arguments.of(2.0, -1.0, 2, true),  // Exactly on triangle boundary
            Arguments.of(2.1, -1.0, 2, false), // Just outside triangle
            Arguments.of(-1.414, -1.414, 2, true), // On circle boundary (approximately)
            Arguments.of(-1.5, -1.5, 2, false) // Just outside circle
        );
    }

    @Test
    void testFirstQuadrantRectangle() {
        // Test rectangle in first quadrant: 0 ≤ x ≤ R/2, 0 ≤ y ≤ R
        assertTrue(AreaChecker.isInArea(0, 0, 2));      // Corner
        assertTrue(AreaChecker.isInArea(1, 0, 2));      // On X axis
        assertTrue(AreaChecker.isInArea(0, 2, 2));      // On Y axis
        assertTrue(AreaChecker.isInArea(1, 2, 2));      // Corner
        assertTrue(AreaChecker.isInArea(0.5, 1, 2));    // Inside
        
        assertFalse(AreaChecker.isInArea(1.1, 1, 2));   // X too large
        assertFalse(AreaChecker.isInArea(0.5, 2.1, 2)); // Y too large
        assertFalse(AreaChecker.isInArea(-0.1, 1, 2));  // X negative
        assertFalse(AreaChecker.isInArea(0.5, -0.1, 2)); // Y negative
    }

    @Test
    void testFourthQuadrantTriangle() {
        // Test triangle in fourth quadrant
        assertTrue(AreaChecker.isInArea(0, 0, 2));      // Origin
        assertTrue(AreaChecker.isInArea(2, 0, 2));      // On X axis
        assertTrue(AreaChecker.isInArea(0, -1, 2));     // On Y axis
        assertTrue(AreaChecker.isInArea(1, -0.5, 2));   // Inside triangle
        
        assertFalse(AreaChecker.isInArea(2.1, 0, 2));   // X too large
        assertFalse(AreaChecker.isInArea(0, -1.1, 2));  // Y too small
        assertFalse(AreaChecker.isInArea(-0.1, 0, 2));  // X negative
        assertFalse(AreaChecker.isInArea(0, 0.1, 2));   // Y positive
    }

    @Test
    void testThirdQuadrantCircle() {
        // Test quarter circle in third quadrant
        assertTrue(AreaChecker.isInArea(0, 0, 2));      // Origin
        assertTrue(AreaChecker.isInArea(-1, -1, 2));    // Inside circle
        assertTrue(AreaChecker.isInArea(-1.4, -1.4, 2)); // Near boundary
        
        assertFalse(AreaChecker.isInArea(-1.5, -1.5, 2)); // Outside circle
        assertFalse(AreaChecker.isInArea(-2, 0, 2));     // X too large
        assertFalse(AreaChecker.isInArea(0, -2, 2));     // Y too large
        assertFalse(AreaChecker.isInArea(0.1, -1, 2));   // X positive
        assertFalse(AreaChecker.isInArea(-1, 0.1, 2));   // Y positive
    }

    @Test
    void testSecondQuadrant() {
        // Second quadrant should always return false
        assertFalse(AreaChecker.isInArea(-1, 1, 2));
        assertFalse(AreaChecker.isInArea(-0.5, 0.5, 2));
        assertFalse(AreaChecker.isInArea(-2, 2, 2));
    }
}
