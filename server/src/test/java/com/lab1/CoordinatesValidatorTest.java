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
        CoordinatesValidator validator = new CoordinatesValidator(0, 0, 3);
        assertTrue(validator.checkData());
    }

    @ParameterizedTest
    @ValueSource(doubles = {-4.9, -2.5, 0, 2.3, 4.9})
    void testValidXValues(double x) {
        CoordinatesValidator validator = new CoordinatesValidator(x, 0, 3);
        assertTrue(validator.checkData());
    }

    @Test
    void testInvalidXValues() {
        // Test invalid X values
        assertFalse(new CoordinatesValidator(-5, 0, 3).checkData());  // X = -5 (not included)
        assertFalse(new CoordinatesValidator(5, 0, 3).checkData());   // X = 5 (not included)
        assertFalse(new CoordinatesValidator(-5.1, 0, 3).checkData()); // X < -5
        assertFalse(new CoordinatesValidator(5.1, 0, 3).checkData());  // X > 5
    }

    @ParameterizedTest
    @ValueSource(doubles = {-4, -3, -2, -1, 0, 1, 2, 3, 4})
    void testValidYValues(double y) {
        CoordinatesValidator validator = new CoordinatesValidator(0, y, 3);
        assertTrue(validator.checkData());
    }

    @Test
    void testInvalidYValues() {
        // Test invalid Y values
        assertFalse(new CoordinatesValidator(0, -5, 3).checkData());
        assertFalse(new CoordinatesValidator(0, 5, 3).checkData());
        assertFalse(new CoordinatesValidator(0, 0.5, 3).checkData());
        assertFalse(new CoordinatesValidator(0, -1.5, 3).checkData());
        assertFalse(new CoordinatesValidator(0, Double.NaN, 3).checkData());
        assertFalse(new CoordinatesValidator(0, Double.POSITIVE_INFINITY, 3).checkData());
        assertFalse(new CoordinatesValidator(0, Double.NEGATIVE_INFINITY, 3).checkData());
    }

    @ParameterizedTest
    @ValueSource(doubles = {2.1, 2.5, 3.0, 3.5, 4.9})
    void testValidRValues(double r) {
        CoordinatesValidator validator = new CoordinatesValidator(0, 0, r);
        assertTrue(validator.checkData());
    }

    @Test
    void testInvalidRValues() {
        // Test invalid R values
        assertFalse(new CoordinatesValidator(0, 0, 2).checkData());   // R = 2 (not included)
        assertFalse(new CoordinatesValidator(0, 0, 5).checkData());   // R = 5 (not included)
        assertFalse(new CoordinatesValidator(0, 0, 1.5).checkData()); // R < 2
        assertFalse(new CoordinatesValidator(0, 0, 5.1).checkData()); // R > 5
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
        assertTrue(new CoordinatesValidator(1.0, 0, 3.0).checkData());
        assertTrue(new CoordinatesValidator(1.0000000001, 0, 3.0).checkData()); // Should be treated as 1
        assertTrue(new CoordinatesValidator(1.0, 0, 3.0000000001).checkData()); // Should be treated as 3
    }
}
