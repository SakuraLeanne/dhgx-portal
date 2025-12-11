# dhgx-portal

智慧园区统一门户项目（auth-server / portal-server / gateway-server / common / message-stream）开发记录。

## 架构概览
- **common**：通用模块，提供统一响应体、异常与校验工具等公共类。
- **auth-server**：Spring Boot + Sa-Token + OAuth2 + OIDC 统一认证中心，默认端口 `8080`。
- **portal-server**：统一门户后端，作为 OAuth2 客户端，处理门户业务（系统列表、菜单列表、跳转票据等），默认端口 `8082`。
- **gateway-server**：基于 Spring Cloud Gateway，提供统一网关、access_token 鉴权与一次性 ticket 单点登录（支持白名单、自定义路由等）。
- **message-stream**：基于 Redis Stream 的消息发布 / 订阅模块，便于各微服务同步用户、权限等事件。

## 技术基线
- JDK 1.8，Spring Boot 2.3.8.RELEASE，Spring Cloud Hoxton.SR12
- Sa-Token 1.44.0，MyBatis-Plus，MySQL，Redis，Nacos 2.0.3

## 本地启动与调试
1. 先在根目录完成依赖下载与聚合构建：

   ```bash
   mvn clean install -DskipTests
   ```

2. 运行单个微服务（示例：认证中心）：

   ```bash
   mvn -pl auth-server spring-boot:run
   ```

   可替换为 `portal-server` / `gateway-server` 等模块名称启动其他服务。若需要同时启动多个服务，可在不同终端执行对应命令，或使用 IDE 的多模块运行配置。

3. 基础配置：
   - `auth-server`：`application.yml` 默认监听 `8080` 端口，可根据环境调整 token 相关配置。
   - `portal-server`：`application.yml` 默认监听 `8082` 端口，包含示例元数据字段 `app.description` / `app.owner`。
   - `gateway-server`：`application.yml` 已内置白名单、SSO ticket 配置与示例路由，可按需修改 Redis 连接、网关域名等信息。

## 数据与开发约定
当前数据库数据尚未整理。涉及数据库查询的接口需在代码中以 **TODO** 标识，并可使用 2-3 条模拟数据返回；DAO 与 MyBatis 代码仍需保留，后续再根据实际数据库调整。

## message-stream 模块使用说明

1. **添加依赖**：在业务微服务的 `pom.xml` 中加入

   ```xml
   <dependency>
       <groupId>com.dhgx.portal</groupId>
       <artifactId>message-stream</artifactId>
       <version>0.0.1-SNAPSHOT</version>
   </dependency>
   ```

2. **配置 Redis 连接与 Stream 选项**（可选，默认使用 `dhgx:stream:events` 流、`dhgx-portal-group` 消费组）

   ```yaml
   spring:
     redis:
       host: 127.0.0.1
       port: 6379
   portal:
     stream:
       stream-key: dhgx:stream:events
       consumer-group: auth-server-group
       consumer-name: auth-server-instance-1
   ```

3. **发布消息**：直接注入 `MessagePublisher` 使用，例如用户新增事件

   ```java
   @RestController
   @RequiredArgsConstructor
   public class UserController {
       private final MessagePublisher publisher;

       @PostMapping("/users")
       public void createUser(@RequestBody UserDTO user) {
           // ...业务处理逻辑
           publisher.publish("event", "user.created");
       }
   }
   ```

4. **订阅消息**：在需要同步信息的微服务中注册监听器

   ```java
   @Component
   @RequiredArgsConstructor
   public class UserEventListener {
       private final MessageSubscriber subscriber;

       @PostConstruct
       public void listen() {
           subscriber.subscribe((messageId, body) -> {
               // 根据业务字段完成处理
               log.info("收到消息 {} 内容 {}", messageId, body);
           });
       }
   }
   ```

`message-stream` 模块默认会在消费前检查并创建消费组，使用时只需确保 Redis 连接可用即可。
