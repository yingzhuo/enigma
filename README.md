[![JDK](http://img.shields.io/badge/JDK-v8.0-yellow.svg)](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
[![Build](http://img.shields.io/badge/Build-Maven_2-green.svg)](https://maven.apache.org/)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.yingzhuo/enigma.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.yingzhuo%22%20AND%20a:%22enigma%22)

# enigma

处于安全方面的考虑，客户端访问服务器的API时，考虑对参数进行验证。本项目提供此功能。

### 下载

```xml
<dependency>
    <groupId>com.github.yingzhuo</groupId>
    <artifactId>enigma</artifactId>
    <version>${enigma.version}</version>
</dependency>
```

### 与spring-boot集成

```yaml
enigma:
  enabled: true
  sign-parameter-name: "_sign"
  nonce-parameter-name: "_nonce"
  timestamp-parameter-name: "_timestamp"
  max-allowed-timestamp-diff: 1h
```

* (1) nonce-parameter-name

为了增加签名的复杂性，客户端向服务器发送一个随机数，建议使用6位字母和数字。

* (2) timestamp-parameter-name

请求发送发时的时间戳，long型数。值应当为`1970-01-01 00:00:00 UTC`到当前时间所经历的毫秒数。如果配置了`max-allowed-timestamp-diff`
服务器会检查服务器的当前时间与请求发出时相差是不是超过了`max-allowed-timestamp-diff`指定的跨度，如果超过了，请求会被拒绝。

* (3) sign-parameter-name

参数签名。

默认情况下，请将除了签名本身所有参数按升序排列，用逗号分隔，生成一个字符串。如下:

```text
_nonce=123456,_timestamp=0,bar=bar,foo=foo
```

不妨称这个字符串为s。最终的签名是`SHA256(MD5(s))`。

如果你的项目需要自行实现签名算法。可以在Spring上下文中加入如下的Bean:

```java
@Primary
@Component
public class MyEnigmaAlgorithm implements EnigmaAlgorithm {

    @Override
    public String encode(String parametersAsString) {
        return ...;
    }

}
```

### 作者

* 应卓 - [github](https://github.com/yingzhuo)

### License

Apache 2.0 license. See [LICENSE](./LICENSE)
