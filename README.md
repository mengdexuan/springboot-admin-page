<h1 align="center"><a href="https://github.com/xkcoding" target="_blank">SpringBoot-Admin-Page</a></h1>
<p align="center">
  <a href="https://www.oracle.com/technetwork/java/javase/downloads/index.html"><img alt="JDK" src="https://img.shields.io/badge/JDK-1.8.0_162-orange.svg"/></a>
  <a href="https://docs.spring.io/spring-boot/docs/2.1.0.RELEASE/reference/html/"><img alt="Spring Boot" src="https://img.shields.io/badge/Spring Boot-2.1.0.RELEASE-brightgreen.svg"/></a>
</p>

## 项目简介

`SpringBoot-Admin-Page` 是一个基于 Bootstrap，JQuery，Layui，Ztree 等前端框架的SpringBoot管理后台项目脚手架。

该项目已成功集成 druid(`监控`)、logback(`日志`)、aopLog(`通过AOP记录web请求日志`)、统一异常处理(`json级别和页面级别`)、thymeleaf(`模板引擎`)、JPA(`强大的ORM框架`)、upload(`本地文件上传`)、quartz(`动态管理定时任务`)、swagger(`API接口管理测试`)

## 开发环境

- **JDK 1.8 +**
- **Maven 3.5 +**
- **IntelliJ IDEA ULTIMATE 2018.1 +** (_注意：使用 IDEA 开发，同时保证安装 `lombok` 插件_)
- **Mysql 5.7 +**

## 运行方式

1. `git clone https://github.com/xkcoding/spring-boot-demo.git`
2. 使用 IDEA 打开 clone 下来的项目
3. 在 IDEA 中 Maven Projects 的面板导入项目根目录下 的 `pom.xml` 文件
4. Maven Projects 找不到的童鞋，可以勾上 IDEA 顶部工具栏的 View -> Tool Buttons ，然后 Maven Projects 的面板就会出现在 IDEA 的右侧

## 功能模块
#### 首页
该项目已成功集成

[![image](https://gitee.com/mengwei_036/MW-JOB-UI/raw/master/doc/img/config.jpg)](https://gitee.com/mengwei_036/MW-JOB-UI/raw/master/doc/img/config.jpg)
#### WebShell
Web版的Xshell，在浏览器上简单实现Xshell功能，包括SFTP功能

#### 系统监控
- 1.系统日志：在页面上查看程序运行的info,error日志而不用登录linux服务器，websocket实时推送日志到页面
- 2.Swagger文档：嵌入Swagger的API接口文档页面
- 3.请求追踪：基于WebFilter的请求响应日志记录

#### 数据库系统
- 1.Web版Navicat：在页面上简单实现数据库连接工具Navicat的功能
- 2.应用数据库：本项目运行的数据库信息，可查看表信息，执行sql等功能
- 3.druid监控：嵌入Alibaba的druid监控页面


#### 文件系统
项目部署所在服务器的磁盘文件系统，以tree的方式展示

#### FTP管理
在Web页面中管理操作FTP服务器

#### 任务管理
基于Quartz的任务管理，可实现对任务的暂停、触发、删除、修改cron等操作，以及查看最近1次运行的错误日志

#### camel路由管理
可在页面中手动控制Apache Camel路由的状态

#### 系统管理
- 1.配置文件：项目运行所使用的配置文件，方便在页面上查看配置
- 2.接口限流：基于 google 的令牌桶算法实现的单体应用的限流控制
- 3.数据字典：系统数据配置维护

#### 权限中心
- 1.用户管理：用户模块CRUD
- 2.权限组管理：权限组模块CRUD
- 3.菜单管理：菜单模块CRUD


[注：系统登录控制功能没有实现]








### 开源推荐

- `hutool`：A set of tools that keep Java sweet，https://www.hutool.cn/




