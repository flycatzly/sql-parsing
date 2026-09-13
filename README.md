# sql-parsing

一个轻量级 Java 库，使用 MyBatis 兼容的动态 SQL 语法解析和生成 SQL 语句。无需依赖 MyBatis 框架，即可在任意 Java 项目中使用动态 SQL 能力。

## 特性

- 完全兼容 MyBatis 动态 SQL 标签语法
- 支持 `<if>`、`<choose>`/`<when>`/`<otherwise>`、`<where>`、`<set>`、`<trim>`、`<foreach>`、`<bind>` 标签
- OGNL 表达式条件判断
- `#{}` 参数占位符 → JDBC `?` 预编译参数
- `${}` 常量替换（直接内嵌值）
- `<![CDATA[ ]]>` 支持
- 转义字符 `\#{` `\}` 支持
- SQL 节点解析结果缓存（ConcurrentHashMap）
- 可提取 SQL 中的所有参数名
- 基于 Druid 的 SQL 格式化输出，支持 MySQL、Oracle、PostgreSQL 等数据库方言

## 环境要求

- Java 1.8+

## Maven 依赖

```xml
<dependency>
    <groupId>io.github.flycatzly</groupId>
    <artifactId>sql-parsing</artifactId>
    <version>1.0.2</version>
</dependency>
```

## 快速开始

### 基本用法

```java
import com.github.flycatzly.sqlparsing.engine.DynamicSqlEngine;
import com.github.flycatzly.sqlparsing.engine.SqlMeta;

String sql = "SELECT * FROM users WHERE 1=1" +
    "<if test=\"name != null and name != ''\">" +
    "  AND name = #{name}" +
    "</if>" +
    "<if test=\"age != null\">" +
    "  AND age = #{age}" +
    "</if>";

Map<String, Object> params = new HashMap<>();
params.put("name", "张三");
params.put("age", 25);

DynamicSqlEngine engine = new DynamicSqlEngine();
SqlMeta result = engine.parse(sql, params);

System.out.println(result.getSql());
// 输出: SELECT * FROM users WHERE 1=1  AND name = ? AND age = ?

System.out.println(result.getJdbcParamValues());
// 输出: [张三, 25]
```

### 使用全局工具类

```java
import com.github.flycatzly.sqlparsing.engine.SqlEngineUtil;
import com.alibaba.druid.DbType;

// 解析并格式化 SQL（指定数据库类型）
SqlMeta result = SqlEngineUtil.getEngine().parse(sql, params, DbType.mysql);
System.out.println(result.getSql());
```

## 支持的动态标签

### `<if>` — 条件判断

根据 OGNL 表达式决定是否拼接 SQL 片段。

```xml
<if test="name != null and name != ''">
    AND name = #{name}
</if>
```

### `<choose>` / `<when>` / `<otherwise>` — 多条件互斥选择

类似 Java 的 `switch-case`，依次判断 `<when>`，匹配第一个即停止；都不匹配时使用 `<otherwise>`。

```xml
<choose>
    <when test="orderBy == 'name'">
        ORDER BY name ASC
    </when>
    <when test="orderBy == 'date'">
        ORDER BY create_time DESC
    </when>
    <otherwise>
        ORDER BY id DESC
    </otherwise>
</choose>
```

### `<where>` — 智能 WHERE 子句

自动添加 `WHERE` 关键字，并去除子内容开头多余的 `AND` / `OR`。

```xml
<select>
    SELECT * FROM users
    <where>
        <if test="name != null">
            AND name = #{name}
        </if>
        <if test="age != null">
            AND age = #{age}
        </if>
    </where>
</select>
```

### `<set>` — 智能 SET 子句（UPDATE 语句）

自动添加 `SET` 关键字，并去除末尾多余的逗号。

```xml
UPDATE users
<set>
    <if test="name != null">name = #{name},</if>
    <if test="age != null">age = #{age},</if>
</set>
WHERE id = #{id}
```

### `<trim>` — 自定义前后缀裁剪

`<where>` 和 `<set>` 的底层实现。可自定义前缀、后缀、需去除的前缀列表和后缀列表。

```xml
<trim prefix="(" suffix=")" prefixesToOverride="AND|OR" suffixesToOverride=",">
    <if test="name != null">AND name = #{name},</if>
    <if test="age != null">AND age = #{age},</if>
</trim>
```

属性说明：
| 属性 | 说明 |
|---|---|
| `prefix` | 内容非空时添加的前缀 |
| `suffix` | 内容非空时添加的后缀 |
| `prefixesToOverride` | 去除内容开头匹配的前缀（多个用 `\|` 分隔） |
| `suffixesToOverride` | 去除内容末尾匹配的后缀（多个用 `\|` 分隔） |

### `<foreach>` — 集合遍历

遍历集合或数组，生成 `IN` 子句或其他批量 SQL。

```xml
<foreach collection="ids" open="(" separator="," close=")" item="item" index="idx">
    #{item}
</foreach>
```

属性说明：
| 属性 | 说明 |
|---|---|
| `collection` | 参数中的集合/数组变量名（必填） |
| `item` | 迭代变量名（默认 `item`） |
| `index` | 索引变量名（默认 `index`） |
| `open` | 循环开始前拼接的字符串 |
| `close` | 循环结束后拼接的字符串 |
| `separator` | 每次迭代之间的分隔符 |

支持的集合类型：`Collection`、数组、`Map`（遍历 entrySet）、逗号分隔的字符串。

### `<bind>` — 变量绑定

声明一个 OGNL 表达式变量（当前为占位实现）。

```xml
<bind name="pattern" value="'%' + name + '%'" />
```

## 完整示例

```java
String sql = "SELECT c.agent_no, a.order_times, b.operator_no\n" +
    "FROM dwd_agent_customer_relation c\n" +
    "INNER JOIN ads_user_behavior a ON a.operator_no = c.operator_no\n" +
    "<choose>\n" +
    "    <when test=\"order_flag\">\n" +
    "        AND a.order_times > 0\n" +
    "    </when>\n" +
    "    <otherwise>\n" +
    "        <![CDATA[ AND a.order_times <= 0 ]]>\n" +
    "    </otherwise>\n" +
    "</choose>\n" +
    "WHERE c.agent_no = #{agent_no}\n" +
    "<if test=\"user_name != null and user_name != ''\">\n" +
    "    AND b.user_name = #{user_name}\n" +
    "</if>\n" +
    "ORDER BY c.create_time DESC";

Map<String, Object> params = new HashMap<>();
params.put("agent_no", "TEST001");
params.put("order_flag", false);
params.put("user_name", "");

DynamicSqlEngine engine = new DynamicSqlEngine();
SqlMeta result = engine.parse(sql, params);
System.out.println(result.getSql());
System.out.println(result.getJdbcParamValues());
```

## API 说明

### `DynamicSqlEngine`

| 方法 | 说明 |
|---|---|
| `parse(String text, Map<String, Object> params)` | 解析动态 SQL，返回 `SqlMeta` |
| `parse(String text, Map<String, Object> params, String dbType)` | 解析并按指定数据库方言格式化 SQL |
| `parseParameter(String text)` | 提取 SQL 模板中所有参数名 |
| `put(String text)` | 预加载 SQL 模板到缓存 |
| `destroy()` | 清空所有缓存 |
| `destroy(String text)` | 清除指定 SQL 模板的缓存 |

### `SqlMeta`

| 方法 | 说明 |
|---|---|
| `getSql()` | 获取解析后的 SQL（`#{}` 已替换为 `?`） |
| `getOldSql()` | 获取原始 SQL 模板 |
| `getJdbcParamValues()` | 获取 JDBC 参数值列表（按 `?` 出现顺序） |
| `getParamValueObj()` | 获取参数值数组 |
| `getParamValues()` | 获取原始参数 Map |

### `SqlEngineUtil`

全局静态工具类，持有一个共享的 `DynamicSqlEngine` 实例。

```java
// 获取引擎
DynamicSqlEngine engine = SqlEngineUtil.getEngine();

// 预加载模板
SqlEngineUtil.put(sqlTemplate);

// 清除缓存
SqlEngineUtil.destroy();
```

## 项目结构

```
sql-parsing/
├── src/main/java/com/github/flycatzly/sqlparsing/
│   ├── engine/                # 引擎层
│   │   ├── DynamicSqlEngine   # 核心解析引擎
│   │   ├── SqlMeta            # 解析结果元数据
│   │   ├── SqlEngineUtil      # 全局工具类
│   │   └── Cache              # SQL 节点缓存
│   ├── context/
│   │   └── Context            # 解析上下文（SQL 拼接、参数收集、OGNL 求值）
│   ├── handler/               # XML 标签处理器
│   │   ├── XmlParser          # XML 解析入口（dom4j）
│   │   ├── NodeHandler        # 处理器接口
│   │   ├── IfHandler          # <if>
│   │   ├── ChooseHandler      # <choose>
│   │   ├── WhenHandler        # <when>
│   │   ├── OtherwiseHandler   # <otherwise>
│   │   ├── WhereHandler       # <where>
│   │   ├── SetHandler         # <set>
│   │   ├── TrimHandler        # <trim>
│   │   ├── ForeachHandler     # <foreach>
│   │   └── BindHandler        # <bind>
│   ├── node/                  # SQL 节点（树形结构）
│   │   ├── SqlNode            # 节点接口
│   │   ├── TextSqlNode        # 纯文本节点
│   │   ├── MixedSqlNode       # 混合节点（子节点列表）
│   │   ├── IfSqlNode          # 条件节点
│   │   ├── ChooseNode         # 互斥选择节点
│   │   ├── WhereSqlNode       # WHERE 裁剪节点
│   │   ├── SetSqlNode         # SET 裁剪节点
│   │   ├── TrimSqlNode        # 裁剪节点基类
│   │   ├── ForeachSqlNode     # 遍历节点
│   │   └── VarDeclSqlNode     # 变量声明节点
│   ├── token/                 # Token 解析
│   │   ├── TokenParser        # #{}/${} 占位符解析器
│   │   └── TokenHandler       # Token 处理回调
│   └── utils/
│       ├── OgnlUtil           # OGNL 表达式工具
│       └── GitUtils           # JGit 操作工具（克隆、提交、推送）
└── src/test/java/             # 测试用例
```

## 依赖说明

| 依赖 | 用途 |
|---|---|
| dom4j | XML 解析 |
| ognl | OGNL 表达式引擎 |
| druid | SQL 格式化 |
| commons-lang3 | 字符串工具 |
| commons-collections | 集合工具 |
| JGit / JSch | Git 操作工具（GitUtils） |
| Flink flink-core | Tuple2 类型（GitUtils） |
| Lombok | 日志注解 |

## 许可证

[LGPL-3.0](http://www.gnu.org/licenses/lgpl.txt)

## 参与贡献

1. Fork 本仓库
2. 新建 `Feat_xxx` 分支
3. 提交代码
4. 新建 Pull Request
