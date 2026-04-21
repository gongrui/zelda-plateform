
# Zelda-FW 通用动态查询框架

## 一、项目简介
Zelda-FW 是基于 SpringBoot + MyBatis-Flex 自研的**配置化无代码动态查询框架**。
业务无需重复写 Controller、Service、SQL，只需维护数据库配置，即可实现：
- 单表分页查询
- 多表 Left / Inner / Right 关联查询
- 动态条件检索
- 全局默认过滤条件
- 字段白名单防注入
- 配置缓存预热 + 动态刷新
- 自定义 VO 返回映射

主项目引入依赖即可，**零注解、零扫描、零侵入**。

---

## 二、整体架构
```
zelda-fw
├── config      自动装配、SPI 加载
├── core        统一门面 ZeldaDb
├── controller  通用HTTP查询接口、缓存刷新接口
├── cache       配置内存缓存、启动预热
├── service     动态 QueryWrapper 构建、联表拼接
├── domain      配置实体 / DTO / 响应体
├── mapper      四张配置表 MyBatis-Flex Mapper
└── util        反射、上下文、工具类
```

### 执行流程
1. 项目启动自动加载配置到内存缓存
2. 请求传入 bizType + 查询条件
3. 匹配业务配置、联表配置、字段白名单、默认条件
4. 自动组装单表/联表 SQL
5. 自动调用 Mapper + 映射 VO 返回分页数据

---

## 三、数据库初始化（必须执行）

### 1. 业务主配置表
```sql
CREATE TABLE query_biz_config (
    id BIGSERIAL PRIMARY KEY,
    biz_code VARCHAR(128) NOT NULL UNIQUE,
    biz_name VARCHAR(255),
    mapper_class VARCHAR(512) NOT NULL,
    vo_class VARCHAR(512) NOT NULL,
    table_name VARCHAR(255) NOT NULL
);
```

### 2. 多表关联配置表
```sql
CREATE TABLE query_join_config (
    id BIGSERIAL PRIMARY KEY,
    biz_id BIGINT NOT NULL,
    join_type VARCHAR(32),
    join_table VARCHAR(255),
    main_alias VARCHAR(32),
    join_alias VARCHAR(32),
    on_condition VARCHAR(512)
);
```

### 3. 查询字段白名单
```sql
CREATE TABLE query_item_config (
    id BIGSERIAL PRIMARY KEY,
    biz_id BIGINT NOT NULL,
    field_name VARCHAR(255),
    default_query_type VARCHAR(32)
);
```

### 4. 默认过滤条件
```sql
CREATE TABLE query_default_condition (
    id BIGSERIAL PRIMARY KEY,
    biz_id BIGINT NOT NULL,
    field_name VARCHAR(255),
    query_type VARCHAR(32),
    value VARCHAR(255)
);
```

### 5. 自测内置配置
```sql
INSERT INTO query_biz_config(biz_code,biz_name,mapper_class,vo_class,table_name)
VALUES(
'query_biz_config',
'配置表自身查询',
'com.blue.zelda.fw.db.mapper.QueryBizConfigMapper',
'com.blue.zelda.fw.db.domain.entity.QueryBizConfig',
'query_biz_config'
);
```

---

## 四、快速接入

### 1. Maven 依赖
```xml
<dependency>
    <groupId>com.blue.zelda</groupId>
    <artifactId>zelda-fw</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 主项目启动类
```java
@SpringBootApplication
public class MainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}
```

### 3. 启动成功标识
控制台输出：
```
=== 通用查询配置加载完成：X 条 ===
```

---

## 五、全场景使用示例

### 场景一：单表通用查询

#### 1）配置
```sql
INSERT INTO query_biz_config(biz_code,biz_name,mapper_class,vo_class,table_name)
VALUES(
'sys_user_list',
'系统用户单表查询',
'com.blue.zelda.system.mapper.SysUserMapper',
'com.blue.zelda.system.vo.SysUserVO',
'sys_user'
);
```

#### 2）HTTP 请求
POST `/api/zelda/db/sys_user_list`
```json
{
    "pageNum": 1,
    "pageSize": 10,
    "sortField": "create_time",
    "sortOrder": "desc",
    "items": [
        {"fieldName":"username","fieldValue":"admin","queryType":"like"},
        {"fieldName":"status","fieldValue":"1","queryType":"eq"}
    ]
}
```

---

### 场景二：多表关联查询（Left Join）

#### 1）主配置
```sql
INSERT INTO query_biz_config(biz_code,biz_name,mapper_class,vo_class,table_name)
VALUES(
'user_order_join',
'用户关联订单查询',
'com.blue.zelda.system.mapper.SysUserMapper',
'com.blue.zelda.system.vo.UserOrderVO',
'sys_user u'
);
```

#### 2）关联配置
```sql
INSERT INTO query_join_config(biz_id,join_type,join_table,main_alias,join_alias,on_condition)
VALUES(
2,
'left',
't_order o',
'u',
'o',
'u.id = o.user_id'
);
```

#### 3）字段白名单
```sql
INSERT INTO query_item_config(biz_id,field_name)
VALUES
(2,'u.username'),
(2,'u.phone'),
(2,'o.order_no'),
(2,'o.pay_status');
```

#### 4）请求示例
```json
{
    "pageNum":1,
    "pageSize":10,
    "items":[
        {"fieldName":"o.pay_status","fieldValue":"1","queryType":"eq"},
        {"fieldName":"u.username","fieldValue":"张","queryType":"like"}
    ]
}
```

#### 5）自动生成 SQL
```sql
SELECT u.*,o.*
FROM sys_user u
LEFT JOIN t_order o ON u.id = o.user_id
WHERE o.pay_status = 1 AND u.username LIKE '%张%'
LIMIT 0,10
```

---

### 场景三：全局默认过滤条件
```sql
INSERT INTO query_default_condition(biz_id,field_name,query_type,value)
VALUES(2,'u.is_deleted','eq','0');
```
自动追加：
```sql
AND u.is_deleted = 0
```

---

### 场景四：Java 代码内部调用
```java
@Autowired
private ZeldaDb zeldaDb;

CommonQueryReq req = new CommonQueryReq();
req.setPageNum(1);
req.setPageSize(20);

CommonQueryResp<UserOrderVO> resp
        = zeldaDb.execute("user_order_join", req, UserOrderVO.class);
```

---

### 场景五：刷新配置缓存（无需重启）
POST `/api/zelda/db/cache/refresh`
```json
{}
```

---

## 六、支持的查询类型
| queryType | 说明 |
|-----------|------|
| eq        | 等于 |
| ne        | 不等于 |
| like      | 全模糊 |
| like_left | 左模糊 |
| like_right| 右模糊 |
| gt        | 大于 |
| ge        | 大于等于 |
| lt        | 小于 |
| le        | 小于等于 |
| in        | 包含 |
| not_in    | 不包含 |
| is_null   | 为空 |
| is_not_null | 非空 |

---

## 七、接口汇总
- 通用查询：`POST /api/zelda/db/{bizType}`
- 缓存刷新：`POST /api/zelda/db/cache/refresh`

---

## 八、常见问题排查
1. config 为 null
   > 缓存未加载 / bizCode 不一致 / 未刷新缓存

2. 接口404
   > 自动装配 SPI 未生效，检查 resources META-INF 配置

3. 联表不生效
   > biz_id 不匹配、别名错误、ON 条件写错

4. 字段查不到
   > 未配置 query_item_config 白名单

---

## 九、版本说明
- 框架名称：Zelda-FW 通用动态查询框架
- 核心依赖：SpringBoot + MyBatis-Flex
- 当前版本：V1.0.0
- 能力：单表查询 + 多表联查 + 配置化 + 缓存自动预热
```

你把上面全部内容复制，新建文件命名 `README.md` 粘贴即可。