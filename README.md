# enigma

处于安全方面的考虑，客户端访问服务器的API时，考虑对参数进行验证。本项目提供此功能。

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

请求发送发时的时间戳，long型数。值应当为`1970-01-01 00:00:00 UTC`到当前时间所经历的毫秒数。

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
