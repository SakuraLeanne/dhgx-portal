# dhgx-portal

智慧园区统一门户项目（auth-server / portal-server / gateway-server / api-server）开发记录。

## 架构概览
- **common**：通用模块，存放标准响应体、异常定义与校验工具等公共类。
- **auth-server**：Spring Boot + Sa-Token + OAuth2 + OIDC 统一认证中心。
- **portal-server**：统一门户后端，作为 OAuth2 客户端，处理门户业务、系统列表、菜单列表等。
- **gateway-server**：基于 Spring Cloud Gateway，提供统一网关、access_token 鉴权与一次性 ticket 单点登录。
- **api-server**：统一 API 平台，负责应用申请与审核，通过审核后生成 `appKey` / `appSecret`，并向网关提供内部查询接口。
- **message-stream**：提供基于 Redis Stream 的消息发布 / 订阅能力，便于各微服务之间同步用户、权限等事件。

## 技术基线
- JDK 1.8，Spring Boot 2.3.8.RELEASE，Spring Cloud Hoxton.SR12
- Sa-Token 1.44.0，MyBatis-Plus，MySQL，Redis，Nacos 2.0.3

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
