# 旅游管理系统

这是一个基于 Spring Boot 的旅游管理系统，提供旅游线路管理、用户管理、订单管理等功能。

## 技术栈

- Spring Boot 3.2.3
- Spring Security
- MyBatis-Plus
- MySQL
- Swagger/OpenAPI

## 功能特性

- 用户管理
  - 用户注册
  - 用户登录
  - 个人信息管理
- 旅游线路管理
  - 线路查询
  - 线路详情
  - 线路创建（管理员）
  - 线路更新（管理员）
- 订单管理
  - 创建订单
  - 订单状态更新
  - 订单查询
  - 订单详情

## 快速开始

1. 克隆项目
```bash
git clone https://github.com/yourusername/tourism-management.git
```

2. 创建数据库
```bash
mysql -u root -p < src/main/resources/db/init.sql
```

3. 修改配置
编辑 `src/main/resources/application.yml` 文件，更新数据库连接信息。

4. 运行项目
```bash
./mvnw spring-boot:run
```

5. 访问接口文档
打开浏览器访问：http://localhost:8080/swagger-ui.html

## API 文档

系统使用 Swagger/OpenAPI 生成接口文档，包含以下模块：

- 用户管理 API
- 旅游线路管理 API
- 订单管理 API

## 默认账户

管理员账户：
- 用户名：admin
- 密码：123456

## 开发环境

- JDK 17+
- Maven 3.6+
- MySQL 8.0+

## 部署说明

1. 打包
```bash
./mvnw clean package
```

2. 运行
```bash
java -jar target/tourism-management-0.0.1-SNAPSHOT.jar
```

## 许可证

MIT License 