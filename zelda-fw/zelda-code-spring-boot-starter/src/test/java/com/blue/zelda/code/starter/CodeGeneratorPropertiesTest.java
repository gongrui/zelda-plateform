package com.blue.zelda.code.starter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CodeGeneratorPropertiesTest {

    private CodeGeneratorProperties properties;

    @BeforeEach
    void setUp() {
        properties = new CodeGeneratorProperties();
    }

    @Test
    void testDefaultValues() {
        assertTrue(properties.isEnabled());
        assertEquals(4, properties.getSeqLength());
        assertEquals(9000, properties.getThreshold());
        assertEquals(10, properties.getMaxNode());
    }

    @Test
    void testSetEnabled() {
        properties.setEnabled(false);
        assertFalse(properties.isEnabled());

        properties.setEnabled(true);
        assertTrue(properties.isEnabled());
    }

    @Test
    void testSetSeqLength() {
        properties.setSeqLength(6);
        assertEquals(6, properties.getSeqLength());

        properties.setSeqLength(10);
        assertEquals(10, properties.getSeqLength());
    }

    @Test
    void testSetThreshold() {
        properties.setThreshold(5000);
        assertEquals(5000, properties.getThreshold());

        properties.setThreshold(10000);
        assertEquals(10000, properties.getThreshold());
    }

    @Test
    void testSetMaxNode() {
        properties.setMaxNode(5);
        assertEquals(5, properties.getMaxNode());

        properties.setMaxNode(20);
        assertEquals(20, properties.getMaxNode());
    }

    @Test
    void testAllSettersAndGetters() {
        properties.setEnabled(false);
        properties.setSeqLength(8);
        properties.setThreshold(8000);
        properties.setMaxNode(15);

        assertFalse(properties.isEnabled());
        assertEquals(8, properties.getSeqLength());
        assertEquals(8000, properties.getThreshold());
        assertEquals(15, properties.getMaxNode());
    }

    @Test
    void testZeroValues() {
        properties.setEnabled(false);
        properties.setSeqLength(0);
        properties.setThreshold(0);
        properties.setMaxNode(0);

        assertFalse(properties.isEnabled());
        assertEquals(0, properties.getSeqLength());
        assertEquals(0, properties.getThreshold());
        assertEquals(0, properties.getMaxNode());
    }

    @Test
    void testNegativeValues() {
        properties.setEnabled(false);
        properties.setSeqLength(-1);
        properties.setThreshold(-100);
        properties.setMaxNode(-5);

        assertFalse(properties.isEnabled());
        assertEquals(-1, properties.getSeqLength());
        assertEquals(-100, properties.getThreshold());
        assertEquals(-5, properties.getMaxNode());
    }
}
