# myproxy
Netty实现的内网穿透代理工具。参考**[natx](https://github.com/wucao/natx)** 实现，支持动态配置端口映射，实时生效（启动时只需配置程序所需的环境信息，端口映射在redis中配置）；支持多个映射多个主机；支持映射多个端口。

启动配置：

```yaml
# common
app:
  #  role: proxy
  role: provider
logging:
  config: classpath:logback-spring.xml

# server
proxy:
  id: proxy-001
spring:
  redis:
    host: localhost
    password: password
    port: 6379


# provider
provider:
  user-id: "user001"
  proxyHost: localhost
```

端口映射配置：

key为proxy-001:configMap（proxy-001为配置文件配置的proxyId）的hash中配置，key为配置文件中配置的userId

```json
[{"targetHost":"localhost","serverId":"自定义服务","targetPort":80,"exposePort":8080}]
```

