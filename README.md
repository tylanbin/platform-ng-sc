NG开发平台（个人使用）
======
NG开发平台的springcloud版本（重写中）

## Module介绍
* platform-ng-common：基础的代码，包括一些通用接口、实现类、工具类等
* platform-ng-sys：基础模块（机构-员工）
* platform-ng-auth：权限模块（权限-角色-用户）
* platform-ng-bpm：流程引擎模块（Activti）
* platform-ng-zuul：Zuul路由网关，主要目的是对前端开放调用
* platform-ng-web-easyui：前台页面，这里直接使用普通版本的jQuery EasyUI界面

## 注意
* auth与bpm包含了分布式事务（使用了[tx-lcn](https://github.com/codingapi/tx-lcn/)）
* 项目启动需要Redis、TX-LCN服务器环境（tx-lcn项目的tm模块）
* 项目启动顺序（每个英文括号中的不区分顺序）：(auth/sys/bpm) -> (zuul) -> (web-easyui)
