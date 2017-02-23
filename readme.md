#wxjf-dubbo-mock
---

##说明
dubbo服务测试挡板

##用法

### 1. 在dubbo服务端配置文件中增加filter="Mock"属性

	<dubbo:service interface="com.wxjfkg.dubbo.service.EchoService" ref="echoServiceImpl" protocol="dubbo" filter="Mock"/>

### 2. 在classpath目录下增加EchoService.<方法名>文本文件，内容为返回对象的JSON序列化字符串

**注意事项：**

1. 如果方法返回空，则不需要挡板文件
2. 如果方法返回字符串，则文件内容可为任意字符串（不需要是json格式）

