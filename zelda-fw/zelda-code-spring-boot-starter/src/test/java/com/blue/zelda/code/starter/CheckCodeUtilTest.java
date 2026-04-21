package com.blue.zelda.code.starter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class CheckCodeUtilTest {

    @Test
    void testCalcCheck_withNormalInput() {
        String numBody = "12345678901234";
        int result = CheckCodeUtil.calcCheck(numBody);
        assertNotNull(result);
        assertTrue(result >= 0 && result <= 8);
    }

    @Test
    void testCalcCheck_withShorterInput() {
        String numBody = "123";
        int result = CheckCodeUtil.calcCheck(numBody);
        assertNotNull(result);
        assertTrue(result >= 0 && result <= 8);
    }

    @Test
    void testCalcCheck_withEmptyInput() {
        String numBody = "";
        int result = CheckCodeUtil.calcCheck(numBody);
        assertEquals(0, result);
    }

    @Test
    void testCalcCheck_withAllZeros() {
        String numBody = "00000000000000";
        int result = CheckCodeUtil.calcCheck(numBody);
        assertEquals(0, result);
    }

    @Test
    void testCalcCheck_withAllNines() {
        String numBody = "99999999999999";
        int result = CheckCodeUtil.calcCheck(numBody);
        assertTrue(result >= 0 && result <= 8);
    }

    @ParameterizedTest
    @CsvSource({
            "12345678901234",
            "11111111111111",
            "22222222222222",
            "98765432109876"
    })
    void testCalcCheck_withVariousInputs(String numBody) {
        int result = CheckCodeUtil.calcCheck(numBody);
        assertNotNull(result);
        assertTrue(result >= 0 && result <= 8, "Check digit should be between 0 and 8");
    }

    @Test
    void testCalcCheck_withInputLongerThanWeightArray() {
        String numBody = "12345678901234567890";
        int result = CheckCodeUtil.calcCheck(numBody);
        assertNotNull(result);
        assertTrue(result >= 0 && result <= 8);
    }

    @Test
    void testCalcCheck_consistency() {
        String numBody = "12345678901234";
        int result1 = CheckCodeUtil.calcCheck(numBody);
        int result2 = CheckCodeUtil.calcCheck(numBody);
        assertEquals(result1, result2, "Same input should produce same check digit");
    }
}
