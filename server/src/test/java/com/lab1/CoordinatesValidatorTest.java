package com.lab1;

import lab1.CoordinatesValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CoordinatesValidator class
 */
class CoordinatesValidatorTest {

    @Test
    void testValidCoordinates() {
        CoordinatesValidator validator = new CoordinatesValidator(0, 0, 2);
        assertTrue(validator.checkData());
    }

    @ParameterizedTest
    @ValueSource(doubles = {-3, -2, -1, 0, 1, 2, 3, 4, 5})
    void testValidXValues(double x) {
        CoordinatesValidator validator = new CoordinatesValidator(x, 0, 2);
        assertTrue(validator.checkData());
    }

    @Test
    void testInvalidXValues() {
        // Test invalid X values
        assertFalse(new CoordinatesValidator(-4, 0, 2).checkData());
        assertFalse(new CoordinatesValidator(6, 0, 2).checkData());
        assertFalse(new CoordinatesValidator(0.5, 0, 2).checkData());
        assertFalse(new CoordinatesValidator(-1.5, 0, 2).checkData());
    }

    @Test
    void testValidYRange() {
        // Test valid Y values
        assertTrue(new CoordinatesValidator(0, -4.9, 2).checkData());
        assertTrue(new CoordinatesValidator(0, 4.9, 2).checkData());
        assertTrue(new CoordinatesValidator(0, 0, 2).checkData());
        assertTrue(new CoordinatesValidator(0, -1.5, 2).checkData());
        assertTrue(new CoordinatesValidator(0, 3.7, 2).checkData());
    }

    @Test
    void testInvalidYRange() {
        // Test invalid Y values
        assertFalse(new CoordinatesValidator(0, -5, 2).checkData());  // Y = -5 (not included)
        assertFalse(new CoordinatesValidator(0, 5, 2).checkData());   // Y = 5 (not included)
        assertFalse(new CoordinatesValidator(0, -5.1, 2).checkData()); // Y < -5
        assertFalse(new CoordinatesValidator(0, 5.1, 2).checkData());  // Y > 5
    }

    @Test
    void testInvalidYValues() {
        // Test special values
        assertFalse(new CoordinatesValidator(0, Double.NaN, 2).checkData());
        assertFalse(new CoordinatesValidator(0, Double.POSITIVE_INFINITY, 2).checkData());
        assertFalse(new CoordinatesValidator(0, Double.NEGATIVE_INFINITY, 2).checkData());
    }

    @ParameterizedTest
    @ValueSource(doubles = {1, 1.5, 2, 2.5, 3})
    void testValidRValues(double r) {
        CoordinatesValidator validator = new CoordinatesValidator(0, 0, r);
        assertTrue(validator.checkData());
    }

    @Test
    void testInvalidRValues() {
        // Test invalid R values
        assertFalse(new CoordinatesValidator(0, 0, 0.5).checkData());
        assertFalse(new CoordinatesValidator(0, 0, 3.5).checkData());
        assertFalse(new CoordinatesValidator(0, 0, 1.2).checkData());
        assertFalse(new CoordinatesValidator(0, 0, 2.7).checkData());
    }

    @Test
    void testGetters() {
        CoordinatesValidator validator = new CoordinatesValidator(1, 2, 3);
        assertEquals(1, validator.getX());
        assertEquals(2, validator.getY());
        assertEquals(3, validator.getR());
    }

    @Test
    void testFloatingPointPrecision() {
        // Test that floating point precision is handled correctly
        assertTrue(new CoordinatesValidator(1.0, 0, 2.0).checkData());
        assertTrue(new CoordinatesValidator(1.0000000001, 0, 2.0).checkData()); // Should be treated as 1
        assertTrue(new CoordinatesValidator(1.0, 0, 2.0000000001).checkData()); // Should be treated as 2
    }
}
