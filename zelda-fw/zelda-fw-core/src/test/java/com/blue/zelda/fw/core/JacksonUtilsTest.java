package com.blue.zelda.fw.core;

import com.blue.zelda.fw.core.util.JacksonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JacksonUtils 单元测试
 */
@DisplayName("JacksonUtils 工具类测试")
class JacksonUtilsTest {

    // ==================== 基础对象定义 ====================

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    static class User {
        private Long id;
        private String name;
        private Integer age;
        private Boolean active;
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    static class UserWithDate {
        private Long id;
        private String name;
        private LocalDateTime createTime;
        private LocalDate birthDate;
        private LocalTime updateTime;
        private Instant timestamp;
    }

    @Nested
    @DisplayName("toJson / writeValueAsString 测试")
    class ToJsonTest {

        @Test
        @DisplayName("对象序列化为JSON字符串")
        void testToJsonObject() {
            User user = new User(1L, "张三", 25, true);
            String json = JacksonUtils.toJson(user);

            assertNotNull(json);
            // Long类型被序列化为字符串格式（ToStringSerializer配置）
            assertTrue(json.contains("\"id\":\"1\""));
            assertTrue(json.contains("\"name\":\"张三\""));
            assertTrue(json.contains("\"age\":25"));
            assertTrue(json.contains("\"active\":true"));
        }

        @Test
        @DisplayName("Map序列化为JSON字符串")
        void testToJsonMap() {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("key1", "value1");
            map.put("key2", 123);
            map.put("key3", true);

            String json = JacksonUtils.toJson(map);
            assertNotNull(json);
            assertTrue(json.contains("\"key1\":\"value1\""));
            assertTrue(json.contains("\"key2\":123"));
            assertTrue(json.contains("\"key3\":true"));
        }

        @Test
        @DisplayName("List序列化为JSON字符串")
        void testToJsonList() {
            List<String> list = Arrays.asList("a", "b", "c");
            String json = JacksonUtils.toJson(list);

            assertEquals("[\"a\",\"b\",\"c\"]", json);
        }

        @Test
        @DisplayName("null对象序列化为JSON")
        void testToJsonNull() {
            String json = JacksonUtils.toJson(null);
            assertEquals("null", json);
        }

        @Test
        @DisplayName("空对象序列化为JSON")
        void testToJsonEmptyObject() {
            User user = new User();
            String json = JacksonUtils.toJson(user);
            assertNotNull(json);
        }

        @Test
        @DisplayName("toJsonStr 与 toJson 等价")
        void testToJsonStr() {
            User user = new User(1L, "测试", 30, false);
            assertEquals(JacksonUtils.toJson(user), JacksonUtils.toJsonStr(user));
        }
    }

    @Nested
    @DisplayName("toNonNullJson / writeValueAsNonNullString 测试")
    class ToNonNullJsonTest {

        @Test
        @DisplayName("忽略null属性")
        void testToNonNullJson() {
            User user = new User();
            user.setId(1L);
            user.setName("测试");
            // age 和 active 为 null

            String json = JacksonUtils.toNonNullJson(user);

            // Long类型被序列化为字符串格式
            assertTrue(json.contains("\"id\":\"1\""));
            assertTrue(json.contains("\"name\":\"测试\""));
            assertFalse(json.contains("\"age\""));
            assertFalse(json.contains("\"active\""));
        }

        @Test
        @DisplayName("与非空JSON对比")
        void testToNonNullJsonVsNormal() {
            User user = new User(1L, "测试", null, null);
            String normalJson = JacksonUtils.toJson(user);
            String nonNullJson = JacksonUtils.toNonNullJson(user);

            assertTrue(normalJson.contains("\"age\":null"));
            assertFalse(nonNullJson.contains("\"age\":null"));
        }
    }

    @Nested
    @DisplayName("toJsonBytes 测试")
    class ToJsonBytesTest {

        @Test
        @DisplayName("对象转换为字节数组")
        void testToJsonBytes() {
            User user = new User(1L, "字节测试", 20, true);
            byte[] bytes = JacksonUtils.toJsonBytes(user);

            assertNotNull(bytes);
            String json = new String(bytes, StandardCharsets.UTF_8);
            // Long类型被序列化为字符串格式
            assertTrue(json.contains("\"id\":\"1\""));
        }
    }

    @Nested
    @DisplayName("readValue (String) 测试")
    class ReadValueStringTest {

        @Test
        @DisplayName("JSON字符串反序列化为对象")
        void testReadValueToObject() {
            String json = "{\"id\":100,\"name\":\"李四\",\"age\":30,\"active\":false}";
            User user = JacksonUtils.readValue(json, User.class);

            assertNotNull(user);
            assertEquals(100L, user.getId());
            assertEquals("李四", user.getName());
            assertEquals(30, user.getAge());
            assertFalse(user.getActive());
        }

        @Test
        @DisplayName("JSON字符串反序列化为Map")
        void testReadValueToMap() {
            String json = "{\"key\":\"value\",\"num\":42}";
            Map<String, Object> map = JacksonUtils.readValue(json, new TypeReference<Map<String, Object>>() {});

            assertNotNull(map);
            assertEquals("value", map.get("key"));
            assertEquals(42, map.get("num"));
        }

        @Test
        @DisplayName("JSON字符串反序列化为List")
        void testReadValueToList() {
            String json = "[1, 2, 3, 4, 5]";
            List<Integer> list = JacksonUtils.readValue(json, new TypeReference<List<Integer>>() {});

            assertNotNull(list);
            assertEquals(5, list.size());
            assertEquals(Arrays.asList(1, 2, 3, 4, 5), list);
        }

        @Test
        @DisplayName("JSON字符串反序列化为List<Map>")
        void testReadValueToListOfMap() {
            String json = "[{\"id\":1,\"name\":\"a\"},{\"id\":2,\"name\":\"b\"}]";
            List<Map<String, Object>> list = JacksonUtils.readValue(json, new TypeReference<List<Map<String, Object>>>() {});

            assertNotNull(list);
            assertEquals(2, list.size());
            assertEquals("a", list.get(0).get("name"));
        }

        @Test
        @DisplayName("使用toBean方法")
        void testToBean() {
            String json = "{\"id\":5,\"name\":\"王五\",\"age\":40,\"active\":true}";
            User user = JacksonUtils.toBean(json, User.class);

            assertNotNull(user);
            assertEquals(5L, user.getId());
            assertEquals("王五", user.getName());
        }

        @Test
        @DisplayName("使用Type反序列化")
        void testReadValueWithType() {
            String json = "{\"id\":1,\"name\":\"测试\"}";
            Type type = User.class;
            User user = JacksonUtils.readValue(json, type);

            assertNotNull(user);
            assertEquals(1L, user.getId());
        }
    }

    @Nested
    @DisplayName("readValue (byte[]) 测试")
    class ReadValueBytesTest {

        @Test
        @DisplayName("字节数组反序列化为对象")
        void testReadValueBytesToObject() {
            String jsonStr = "{\"id\":200,\"name\":\"字节用户\",\"age\":35,\"active\":true}";
            byte[] jsonBytes = jsonStr.getBytes(StandardCharsets.UTF_8);

            User user = JacksonUtils.readValue(jsonBytes, User.class);

            assertNotNull(user);
            assertEquals(200L, user.getId());
            assertEquals("字节用户", user.getName());
        }

        @Test
        @DisplayName("字节数组使用TypeReference反序列化")
        void testReadValueBytesWithTypeReference() {
            String jsonStr = "{\"key\":\"value\"}";
            byte[] jsonBytes = jsonStr.getBytes(StandardCharsets.UTF_8);

            Map<String, String> map = JacksonUtils.readValue(jsonBytes, new TypeReference<Map<String, String>>() {});

            assertNotNull(map);
            assertEquals("value", map.get("key"));
        }

        @Test
        @DisplayName("使用toBean处理字节数组")
        void testToBeanBytes() {
            String jsonStr = "{\"id\":99,\"name\":\"Bean测试\",\"age\":50,\"active\":false}";
            byte[] jsonBytes = jsonStr.getBytes(StandardCharsets.UTF_8);

            User user = JacksonUtils.toBean(jsonBytes, User.class);

            assertNotNull(user);
            assertEquals(99L, user.getId());
        }
    }

    @Nested
    @DisplayName("readValue (InputStream) 测试")
    class ReadValueInputStreamTest {

        @Test
        @DisplayName("InputStream反序列化为对象")
        void testReadValueInputStream() {
            String jsonStr = "{\"id\":300,\"name\":\"流用户\",\"age\":28,\"active\":true}";
            InputStream inputStream = new ByteArrayInputStream(jsonStr.getBytes(StandardCharsets.UTF_8));

            User user = JacksonUtils.readValue(inputStream, User.class);

            assertNotNull(user);
            assertEquals(300L, user.getId());
            assertEquals("流用户", user.getName());
        }

        @Test
        @DisplayName("InputStream使用TypeReference反序列化")
        void testReadValueInputStreamWithTypeReference() {
            String jsonStr = "[{\"id\":1},{\"id\":2}]";
            InputStream inputStream = new ByteArrayInputStream(jsonStr.getBytes(StandardCharsets.UTF_8));

            List<Map<String, Integer>> list = JacksonUtils.readValue(inputStream, new TypeReference<List<Map<String, Integer>>>() {});

            assertNotNull(list);
            assertEquals(2, list.size());
        }
    }

    @Nested
    @DisplayName("Java 8 时间类型测试")
    class JavaTimeTest {

        @Test
        @DisplayName("LocalDateTime 序列化与反序列化")
        void testLocalDateTime() {
            LocalDateTime now = LocalDateTime.of(2024, 6, 15, 14, 30, 45);
            String json = JacksonUtils.toJson(now);

            LocalDateTime parsed = JacksonUtils.readValue(json, LocalDateTime.class);
            assertEquals(now, parsed);
        }

        @Test
        @DisplayName("LocalDate 序列化与反序列化")
        void testLocalDate() {
            LocalDate date = LocalDate.of(2024, 12, 25);
            String json = JacksonUtils.toJson(date);

            LocalDate parsed = JacksonUtils.readValue(json, LocalDate.class);
            assertEquals(date, parsed);
        }

        @Test
        @DisplayName("LocalTime 序列化与反序列化")
        void testLocalTime() {
            LocalTime time = LocalTime.of(10, 30, 0);
            String json = JacksonUtils.toJson(time);

            LocalTime parsed = JacksonUtils.readValue(json, LocalTime.class);
            assertEquals(time, parsed);
        }

        @Test
        @DisplayName("Instant 序列化与反序列化")
        void testInstant() {
            Instant instant = Instant.parse("2024-06-15T14:30:45.000Z");
            String json = JacksonUtils.toJson(instant);

            Instant parsed = JacksonUtils.readValue(json, Instant.class);
            assertNotNull(parsed);
        }

        @Test
        @DisplayName("包含时间类型的对象序列化")
        void testObjectWithTimeFields() {
            UserWithDate user = new UserWithDate(
                    1L,
                    "时间用户",
                    LocalDateTime.of(2024, 6, 15, 14, 30),
                    LocalDate.of(2000, 1, 1),
                    LocalTime.of(12, 0, 0),
                    Instant.parse("2024-06-15T14:30:00Z")
            );

            String json = JacksonUtils.toJson(user);
            UserWithDate parsed = JacksonUtils.readValue(json, UserWithDate.class);

            assertEquals(user.getId(), parsed.getId());
            assertEquals(user.getName(), parsed.getName());
            assertEquals(user.getCreateTime(), parsed.getCreateTime());
            assertEquals(user.getBirthDate(), parsed.getBirthDate());
            assertEquals(user.getUpdateTime(), parsed.getUpdateTime());
        }

        @Test
        @DisplayName("时间戳格式字符串反序列化为Instant")
        void testInstantFromTimestamp() {
            String json = "\"2024-06-15 14:30:45\"";
            Instant instant = JacksonUtils.readValue(json, Instant.class);

            assertNotNull(instant);
        }
    }

    @Nested
    @DisplayName("Long类型序列化测试")
    class LongSerializeTest {

        @Test
        @DisplayName("Long类型序列化为字符串避免精度丢失")
        void testLongToString() {
            Map<String, Object> map = new HashMap<>();
            map.put("id", 1234567890123456789L);

            String json = JacksonUtils.toJson(map);
            assertTrue(json.contains("\"1234567890123456789\""));
        }

        @Test
        @DisplayName("反序列化Long字符串")
        void testDeserializeLong() {
            String json = "{\"id\":\"1234567890123456789\"}";
            Map<String, Object> map = JacksonUtils.readValue(json, new TypeReference<Map<String, Object>>() {});

            assertNotNull(map.get("id"));
        }
    }

    @Nested
    @DisplayName("JsonNode / toObj 测试")
    class JsonNodeTest {

        @Test
        @DisplayName("JSON字符串转换为JsonNode")
        void testToObj() {
            String json = "{\"name\":\"测试\",\"age\":30}";
            JsonNode node = JacksonUtils.toObj(json);

            assertNotNull(node);
            assertTrue(node.isObject());
            assertEquals("测试", node.get("name").asText());
            assertEquals(30, node.get("age").asInt());
        }

        @Test
        @DisplayName("JSON数组转换为JsonNode")
        void testToObjArray() {
            String json = "[1, 2, 3]";
            JsonNode node = JacksonUtils.toObj(json);

            assertNotNull(node);
            assertTrue(node.isArray());
            assertEquals(3, node.size());
        }

        @Test
        @DisplayName("使用toJsonNode方法")
        void testToJsonNode() {
            String json = "{\"key\":\"value\"}";
            JsonNode node = JacksonUtils.toJsonNode(json);

            assertEquals("value", node.get("key").asText());
        }

        @Test
        @DisplayName("字节数组转JsonNode")
        void testToJsonNodeBytes() {
            String jsonStr = "{\"num\":100}";
            byte[] bytes = jsonStr.getBytes(StandardCharsets.UTF_8);
            JsonNode node = JacksonUtils.toJsonNode(bytes);

            assertEquals(100, node.get("num").asInt());
        }

        @Test
        @DisplayName("InputStream转JsonNode")
        void testToJsonNodeInputStream() {
            String jsonStr = "{\"flag\":true}";
            InputStream inputStream = new ByteArrayInputStream(jsonStr.getBytes(StandardCharsets.UTF_8));
            JsonNode node = JacksonUtils.toJsonNode(inputStream);

            assertTrue(node.get("flag").asBoolean());
        }

        @Test
        @DisplayName("JsonNode数组访问")
        void testJsonNodeArrayAccess() {
            String json = "[{\"id\":1},{\"id\":2},{\"id\":3}]";
            JsonNode node = JacksonUtils.toObj(json);

            assertEquals(3, node.size());
            assertEquals(1, node.get(0).get("id").asInt());
            assertEquals(2, node.get(1).get("id").asInt());
            assertEquals(3, node.get(2).get("id").asInt());
        }
    }

    @Nested
    @DisplayName("transferToJsonNode 测试")
    class TransferToJsonNodeTest {

        @Test
        @DisplayName("对象转换为JsonNode")
        void testTransferObjectToJsonNode() {
            User user = new User(1L, "转换测试", 25, true);
            JsonNode node = JacksonUtils.transferToJsonNode(user);

            assertNotNull(node);
            assertEquals(1L, node.get("id").asLong());
            assertEquals("转换测试", node.get("name").asText());
        }

        @Test
        @DisplayName("Map转换为JsonNode")
        void testTransferMapToJsonNode() {
            Map<String, Object> map = new HashMap<>();
            map.put("key", "value");
            map.put("num", 42);

            JsonNode node = JacksonUtils.transferToJsonNode(map);

            assertEquals("value", node.get("key").asText());
            assertEquals(42, node.get("num").asInt());
        }
    }

    @Nested
    @DisplayName("treeToValue 测试")
    class TreeToValueTest {

        @Test
        @DisplayName("JsonNode转换为对象")
        void testTreeToValue() {
            String json = "{\"id\":50,\"name\":\"树转换\",\"age\":35,\"active\":true}";
            JsonNode node = JacksonUtils.toObj(json);

            User user = JacksonUtils.treeToValue(node, User.class);

            assertNotNull(user);
            assertEquals(50L, user.getId());
            assertEquals("树转换", user.getName());
        }

        @Test
        @DisplayName("JsonNode使用TypeReference转换")
        void testTreeToValueWithTypeReference() {
            String json = "{\"items\":[\"a\",\"b\",\"c\"]}";
            JsonNode node = JacksonUtils.toObj(json);

            Map<String, List<String>> result = JacksonUtils.treeToValue(node, new TypeReference<Map<String, List<String>>>() {});

            assertNotNull(result);
            assertEquals(Arrays.asList("a", "b", "c"), result.get("items"));
        }
    }

    @Nested
    @DisplayName("createEmptyJsonNode / createEmptyArrayNode 测试")
    class CreateNodeTest {

        @Test
        @DisplayName("创建空ObjectNode")
        void testCreateEmptyJsonNode() {
            ObjectNode node = JacksonUtils.createEmptyJsonNode();

            assertNotNull(node);
            assertTrue(node.isObject());
            assertTrue(node.isEmpty());
        }

        @Test
        @DisplayName("创建空ArrayNode")
        void testCreateEmptyArrayNode() {
            ArrayNode node = JacksonUtils.createEmptyArrayNode();

            assertNotNull(node);
            assertTrue(node.isArray());
            assertTrue(node.isEmpty());
        }

        @Test
        @DisplayName("向ObjectNode添加内容")
        void testObjectNodeAddContent() {
            ObjectNode node = JacksonUtils.createEmptyJsonNode();
            node.put("key", "value");
            node.put("num", 123);

            assertEquals("value", node.get("key").asText());
            assertEquals(123, node.get("num").asInt());
        }

        @Test
        @DisplayName("向ArrayNode添加内容")
        void testArrayNodeAddContent() {
            ArrayNode node = JacksonUtils.createEmptyArrayNode();
            node.add("item1");
            node.add(100);
            node.addObject().put("nested", true);

            assertEquals(3, node.size());
            assertEquals("item1", node.get(0).asText());
        }
    }

    @Nested
    @DisplayName("constructJavaType 测试")
    class ConstructJavaTypeTest {

        @Test
        @DisplayName("构造JavaType")
        void testConstructJavaType() {
            Type type = List.class;
            var javaType = JacksonUtils.constructJavaType(type);

            assertNotNull(javaType);
        }
    }

    @Nested
    @DisplayName("registerSubtype 测试")
    class RegisterSubtypeTest {

        @Test
        @DisplayName("注册子类类型")
        void testRegisterSubtype() {
            assertDoesNotThrow(() -> JacksonUtils.registerSubtype(ObjectNode.class, "customType"));
        }
    }

    @Nested
    @DisplayName("readTree 测试")
    class ReadTreeTest {

        @Test
        @DisplayName("字符串读取为JsonNode")
        void testReadTree() {
            String json = "{\"test\":\"readTree\"}";
            JsonNode node = JacksonUtils.readTree(json);

            assertNotNull(node);
            assertEquals("readTree", node.get("test").asText());
        }
    }

    @Nested
    @DisplayName("getObjectMapper 测试")
    class GetObjectMapperTest {

        @Test
        @DisplayName("获取ObjectMapper实例")
        void testGetObjectMapper() {
            var mapper = JacksonUtils.getObjectMapper();
            assertNotNull(mapper);

            // 验证可以正常使用
            User user = new User(1L, "Mapper测试", 20, true);
            String json = JacksonUtils.toJson(user);
            // Long类型被序列化为字符串格式
            assertTrue(json.contains("\"id\":\"1\""));
        }
    }

    @Nested
    @DisplayName("异常场景测试")
    class ExceptionTest {

        @Test
        @DisplayName("无效JSON抛出RuntimeException")
        void testInvalidJsonThrows() {
            String invalidJson = "{invalid json}";

            assertThrows(RuntimeException.class, () -> JacksonUtils.readValue(invalidJson, User.class));
            assertThrows(RuntimeException.class, () -> JacksonUtils.toObj(invalidJson));
        }

        @Test
        @DisplayName("无效类型转换抛出RuntimeException")
        void testInvalidTypeConversion() {
            String json = "{\"id\":\"not_a_number\"}";

            // 尝试将字符串反序列化为Integer字段应该失败
            assertThrows(RuntimeException.class, () -> JacksonUtils.readValue(json, User.class));
        }

        @Test
        @DisplayName("构造函数私有化不可实例化")
        void testPrivateConstructor() {
            Exception exception = assertThrows(Exception.class, () -> {
                // 通过反射尝试调用私有构造函数
                var constructor = JacksonUtils.class.getDeclaredConstructor();
                constructor.setAccessible(true);
                constructor.newInstance();
            });
            // 反射调用时，实际异常被包装在 InvocationTargetException 中
            assertTrue(exception.getCause() instanceof UnsupportedOperationException
                    || exception instanceof UnsupportedOperationException);
        }
    }

    @Nested
    @DisplayName("忽略未知属性测试")
    class IgnoreUnknownTest {

        @Test
        @DisplayName("JSON中多余的字段不会导致反序列化失败")
        void testIgnoreUnknownProperties() {
            String json = "{\"id\":1,\"name\":\"测试\",\"unknownField\":\"ignored\",\"anotherUnknown\":123}";
            User user = JacksonUtils.readValue(json, User.class);

            assertNotNull(user);
            assertEquals(1L, user.getId());
            assertEquals("测试", user.getName());
        }
    }

    @Nested
    @DisplayName("复杂嵌套结构测试")
    class ComplexStructureTest {

        @Test
        @DisplayName("复杂嵌套Map结构")
        void testComplexNestedMap() {
            Map<String, Object> inner = new HashMap<>();
            inner.put("innerKey", "innerValue");

            Map<String, Object> outer = new HashMap<>();
            outer.put("level1", Map.of(
                    "level2", Map.of(
                            "level3", "deepValue"
                    )
            ));
            outer.put("list", Arrays.asList(1, 2, 3));
            outer.put("map", inner);

            String json = JacksonUtils.toJson(outer);
            Map<String, Object> parsed = JacksonUtils.readValue(json, new TypeReference<Map<String, Object>>() {});

            assertNotNull(parsed);
            assertNotNull(parsed.get("list"));
        }

        @Test
        @DisplayName("包含特殊字符的字符串")
        void testSpecialCharacters() {
            Map<String, String> map = new HashMap<>();
            map.put("emoji", "😀🎉");
            map.put("chinese", "中文测试");
            map.put("quotes", "\"quotes\" and 'single'");
            map.put("newline", "line1\nline2");

            String json = JacksonUtils.toJson(map);
            Map<String, String> parsed = JacksonUtils.readValue(json, new TypeReference<Map<String, String>>() {});

            assertEquals("😀🎉", parsed.get("emoji"));
            assertEquals("中文测试", parsed.get("chinese"));
            assertEquals("\"quotes\" and 'single'", parsed.get("quotes"));
            assertEquals("line1\nline2", parsed.get("newline"));
        }
    }
}
