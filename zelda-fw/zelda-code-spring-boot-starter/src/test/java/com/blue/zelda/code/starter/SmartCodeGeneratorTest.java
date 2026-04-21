package com.blue.zelda.code.starter;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class SmartCodeGeneratorTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private CodeGeneratorProperties properties;

    @InjectMocks
    private SmartCodeGenerator smartCodeGenerator;

    @BeforeEach
    void setUp() {
        log.info("初始化测试环境");
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        log.info("Mock 设置完成");
    }

    @Test
    void testGenerate_withValidPrefix() {
        log.info("开始测试: testGenerate_withValidPrefix");
        log.info("测试参数: prefix = AA");
        when(properties.getMaxNode()).thenReturn(10);
        when(properties.getSeqLength()).thenReturn(4);
        when(properties.getThreshold()).thenReturn(9000);
        when(valueOperations.get(anyString())).thenReturn(null);
        when(valueOperations.increment(anyString(), anyLong())).thenReturn(1L);

        String result = smartCodeGenerator.generate("AA");

        log.info("生成结果: {}", result);
        assertNotNull(result);
        assertEquals(17, result.length());
        assertTrue(result.startsWith("AA"));
        log.info("测试通过: testGenerate_withValidPrefix");
    }

    @Test
    void testGenerate_withInvalidPrefix_null() {
        log.info("开始测试: testGenerate_withInvalidPrefix_null");
        log.info("测试参数: prefix = null");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> smartCodeGenerator.generate(null)
        );

        assertEquals("前缀必须是 2 位字符", exception.getMessage());
        log.info("抛出异常: {}", exception.getMessage());
        log.info("测试通过: testGenerate_withInvalidPrefix_null");
    }

    @Test
    void testGenerate_withInvalidPrefix_wrongLength() {
        log.info("开始测试: testGenerate_withInvalidPrefix_wrongLength");
        log.info("测试参数: prefix = A");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> smartCodeGenerator.generate("A")
        );

        assertEquals("前缀必须是 2 位字符", exception.getMessage());
        log.info("抛出异常: {}", exception.getMessage());
        log.info("测试通过: testGenerate_withInvalidPrefix_wrongLength");
    }

    @Test
    void testGenerate_withInvalidPrefix_tooLong() {
        log.info("开始测试: testGenerate_withInvalidPrefix_tooLong");
        log.info("测试参数: prefix = AAA");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> smartCodeGenerator.generate("AAA")
        );

        assertEquals("前缀必须是 2 位字符", exception.getMessage());
        log.info("抛出异常: {}", exception.getMessage());
        log.info("测试通过: testGenerate_withInvalidPrefix_tooLong");
    }

    @Test
    void testGenerate_incrementsSequence() {
        log.info("开始测试: testGenerate_incrementsSequence");
        log.info("测试参数: prefix = AB");
        when(properties.getMaxNode()).thenReturn(10);
        when(properties.getSeqLength()).thenReturn(4);
        when(properties.getThreshold()).thenReturn(9000);
        when(valueOperations.get(anyString())).thenReturn("0005");
        when(valueOperations.increment(anyString(), anyLong())).thenReturn(6L);

        String result = smartCodeGenerator.generate("AB");

        log.info("生成结果: {}", result);
        assertNotNull(result);
        assertEquals(17, result.length());
        assertTrue(result.startsWith("AB"));

        verify(redisTemplate, times(1)).expire(anyString(), anyLong(), any());
        log.info("验证成功: expire 方法被调用");
        log.info("测试通过: testGenerate_incrementsSequence");
    }

    @Test
    void testGenerate_selectsAvailableNode() {
        log.info("开始测试: testGenerate_selectsAvailableNode");
        log.info("测试参数: prefix = AB, maxNode = 3");
        when(properties.getMaxNode()).thenReturn(3);
        when(properties.getSeqLength()).thenReturn(4);
        when(properties.getThreshold()).thenReturn(9000);
        when(valueOperations.get(anyString())).thenReturn("9500");
        when(valueOperations.increment(anyString(), anyLong())).thenReturn(1L);

        String result = smartCodeGenerator.generate("AB");

        log.info("生成结果: {}", result);
        assertNotNull(result);
        log.info("测试通过: testGenerate_selectsAvailableNode");
    }

    @Test
    void testVerify_withValidCode() {
        log.info("开始测试: testVerify_withValidCode");
        String body = "20250101010001";
        int checkDigit = CheckCodeUtil.calcCheck(body);
        String code = "AA" + body + checkDigit;

        log.info("测试参数: code = {}", code);
        log.info("校验位: {}", checkDigit);

        boolean result = smartCodeGenerator.verify(code);

        log.info("验证结果: {}", result);
        assertTrue(result);
        log.info("测试通过: testVerify_withValidCode");
    }

    @Test
    void verify_withInvalidCode_null() {
        log.info("开始测试: verify_withInvalidCode_null");
        log.info("测试参数: code = null");
        boolean result = smartCodeGenerator.verify(null);
        log.info("验证结果: {}", result);
        assertFalse(result);
        log.info("测试通过: verify_withInvalidCode_null");
    }

    @Test
    void verify_withInvalidCode_wrongLength() {
        log.info("开始测试: verify_withInvalidCode_wrongLength");
        log.info("测试参数: code = ABC");
        boolean result = smartCodeGenerator.verify("ABC");
        log.info("验证结果: {}", result);
        assertFalse(result);
        log.info("测试通过: verify_withInvalidCode_wrongLength");
    }

    @Test
    void verify_withInvalidCode_wrongCheckDigit() {
        log.info("开始测试: verify_withInvalidCode_wrongCheckDigit");
        String code = "AA20250101010001"; // 长度16，缺少校验位
        log.info("测试参数: code = {}, 长度: {}", code, code.length());
        boolean result = smartCodeGenerator.verify(code);
        log.info("验证结果: {}", result);
        assertFalse(result);
        log.info("测试通过: verify_withInvalidCode_wrongCheckDigit");
    }

    @Test
    void verify_withInvalidCheckDigit() {
        log.info("开始测试: verify_withInvalidCheckDigit");
        String code = "AA202501010100019"; // 错误的校验位
        log.info("测试参数: code = {}", code);
        boolean result = smartCodeGenerator.verify(code);
        log.info("验证结果: {}", result);
        assertFalse(result);
        log.info("测试通过: verify_withInvalidCheckDigit");
    }

    @Test
    void testGenerate_withMultipleNodes() {
        log.info("开始测试: testGenerate_withMultipleNodes");
        log.info("测试参数: prefix = ZZ, maxNode = 5");
        when(properties.getMaxNode()).thenReturn(5);
        when(properties.getSeqLength()).thenReturn(4);
        when(properties.getThreshold()).thenReturn(9000);

        // 所有节点都已达到阈值
        when(valueOperations.get(anyString())).thenReturn("9500");
        when(valueOperations.increment(anyString(), anyLong())).thenReturn(9501L);

        String result = smartCodeGenerator.generate("ZZ");

        log.info("生成结果: {}", result);
        assertNotNull(result);
        assertEquals(17, result.length());
        log.info("测试通过: testGenerate_withMultipleNodes");
    }

    @Test
    void testGenerate_withDifferentSeqLength() {
        log.info("开始测试: testGenerate_withDifferentSeqLength");
        log.info("测试参数: prefix = CD, seqLength = 6");
        when(properties.getMaxNode()).thenReturn(10);
        when(properties.getSeqLength()).thenReturn(6);
        when(properties.getThreshold()).thenReturn(9000);
        when(valueOperations.get(anyString())).thenReturn(null);
        when(valueOperations.increment(anyString(), anyLong())).thenReturn(1L);

        String result = smartCodeGenerator.generate("CD");

        log.info("生成结果: {}", result);
        log.info("结果长度: {}", result.length());
        assertNotNull(result);
        assertEquals(19, result.length()); // 2 + 8 + 2 + 6 + 1 = 19
        log.info("测试通过: testGenerate_withDifferentSeqLength");
    }
}
