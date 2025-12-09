# dhgx-portal

智慧园区统一门户项目（auth-server / portal-server / gateway-server / api-server）开发记录。

## 架构概览
- **auth-server**：Spring Boot + Sa-Token + OAuth2 + OIDC 统一认证中心。
- **portal-server**：统一门户后端，作为 OAuth2 客户端，处理门户业务、系统列表、菜单列表等。
- **gateway-server**：基于 Spring Cloud Gateway，提供统一网关、access_token 鉴权与一次性 ticket 单点登录。
- **api-server**：统一 API 平台，负责应用申请与审核，通过审核后生成 `appKey` / `appSecret`，并向网关提供内部查询接口。

## 技术基线
- JDK 1.8，Spring Boot 2.3.8.RELEASE，Spring Cloud Hoxton.SR12
- Sa-Token 1.44.0，MyBatis-Plus，MySQL，Redis，Nacos 2.0.3

## 数据与开发约定
当前数据库数据尚未整理。涉及数据库查询的接口需在代码中以 **TODO** 标识，并可使用 2-3 条模拟数据返回；DAO 与 MyBatis 代码仍需保留，后续再根据实际数据库调整。
