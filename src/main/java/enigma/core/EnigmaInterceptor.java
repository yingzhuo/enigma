/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *   ___ _ __ (_) __ _ _ __ ___   __ _
 *  / _ \ '_ \| |/ _` | '_ ` _ \ / _` |
 * |  __/ | | | | (_| | | | | | | (_| |
 *  \___|_| |_|_|\__, |_| |_| |_|\__,_|
 *              |___/      https://github.com/yingzhuo/enigma
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package enigma.core;

import enigma.Enigma;
import enigma.EnigmaAlgorithm;
import enigma.EnigmaIgnored;
import enigma.exception.EnigmaException;
import enigma.exception.InvalidRequestException;
import enigma.exception.InvalidSignException;
import enigma.exception.InvalidTimestampException;
import enigma.impl.DefaultEnigmaAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author 应卓
 * @since 0.0.1
 */
public class EnigmaInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(EnigmaInterceptor.class);
    private static final Class<EnigmaIgnored> IGNORED = EnigmaIgnored.class;

    private final PathMatcher pathMatcher = new AntPathMatcher();
    private Set<String> excludeAntPatterns;
    private EnigmaAlgorithm algorithm = new DefaultEnigmaAlgorithm();
    private String nonceParameterName = "_nonce";
    private String timestampParameterName = "_timestamp";
    private String signParameterName = "_sign";
    private String nonceHeaderName = "X-Enigma-Nonce";
    private String timestampHeaderName = "X-Enigma-Timestamp";
    private String signHeaderName = "X-Enigma-Sign";
    private Duration maxAllowedTimestampDiff = null;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        EnigmaContext.remove();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws EnigmaException {
        EnigmaContext.remove();

        final String path = request.getRequestURI();

        if (excludeAntPatterns != null && !excludeAntPatterns.isEmpty()) {
            boolean skip = excludeAntPatterns.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
            if (skip) {
                log.debug("{} skipped", path);
                return true;
            }
        }

        if (this.skipByAnnotation(handler)) {
            log.debug("{} skipped by annotation @EnigmaIgnored", path);
            return true;
        }

        final Enigma enigma = resolve(request).orElse(null);
        if (enigma == null) {
            throw new InvalidRequestException("invalid request");
        } else {
            log.debug("enigma context setting");
            EnigmaContext.set(enigma);
        }

        final String parametersAsString = flatAndSort(request.getParameterMap(), signParameterName);
        log.debug("parameters = {}", parametersAsString);

        final String hashed = algorithm.encode(parametersAsString);
        log.debug("hashed-parameters = {}", hashed);

        // 检查签名
        final String sign = enigma.getSign();
        log.debug("sign = {}", sign);

        if (!StringUtils.equals(hashed, sign)) {
            throw new InvalidSignException("invalid sign");
        }

        // 检查时间戳
        if (maxAllowedTimestampDiff != null) {
            long diff = Math.abs(System.currentTimeMillis() - enigma.getTimestamp());
            if (diff > maxAllowedTimestampDiff.toMillis()) {
                throw new InvalidTimestampException("invalid timestamp");
            }
        } else {
            log.debug("timestamp-checking is ignored");
        }

        return true;
    }

    public Optional<Enigma> resolve(HttpServletRequest request) {

        String nonce = resolveNonce(request);
        Long timestamp = resolveTimestamp(request);
        String sign = resolveSign(request);

        if (nonce == null || sign == null || timestamp == null) {
            return Optional.empty();
        }

        final Enigma enigma = new Enigma();
        enigma.setNonce(nonce);
        enigma.setTimestamp(timestamp);
        enigma.setSign(sign);
        return Optional.of(enigma);
    }

    private String resolveNonce(HttpServletRequest request) {
        String value = null;
        if (nonceParameterName != null) {
            value = request.getParameter(nonceParameterName);
        }
        if (value == null && nonceHeaderName != null) {
            value = request.getHeader(nonceHeaderName);
        }
        return value;
    }

    private Long resolveTimestamp(HttpServletRequest request) {
        Long value = null;
        if (timestampParameterName != null) {
            value = string2Long(request.getParameter(timestampParameterName));
        }
        if (value == null && timestampHeaderName != null) {
            value = string2Long(request.getHeader(timestampHeaderName));
        }
        // since 0.0.2
        if (isTimestampCheckingDisabled() && value == null) {
            value = -1L;
        }
        return value;
    }

    private String resolveSign(HttpServletRequest request) {
        String value = null;
        if (signParameterName != null) {
            value = request.getParameter(signParameterName);
        }
        if (value == null && signHeaderName != null) {
            value = request.getHeader(signHeaderName);
        }
        return value;
    }

    private String flatAndSort(Map<String, String[]> source, String signParameterName) {
        final StringBuilder stringBuilder = new StringBuilder();

        for (String key : new TreeSet<>(source.keySet())) {

            if (signParameterName.equals(key)) {
                continue;
            }

            String[] values = source.get(key);
            String value = StringUtils.join(values, ",");
            stringBuilder.append(
                    String.format("%s=%s,", key, value)
            );
        }

        String string = stringBuilder.toString();
        if (string.endsWith(",")) {
            string = string.substring(0, string.length() - 1);
        }
        return string;
    }

    private Long string2Long(String s) {
        try {
            return Math.abs(Long.parseLong(s));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean skipByAnnotation(Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            return handlerMethod.getMethodAnnotation(IGNORED) != null ||
                    handlerMethod.getBeanType().getAnnotation(IGNORED) != null;
        }
        return false;
    }

    private boolean isTimestampCheckingDisabled() {
        return this.maxAllowedTimestampDiff == null;
    }

    public void setExcludeAntPatterns(Set<String> excludeAntPatterns) {
        this.excludeAntPatterns = excludeAntPatterns;
    }

    public void setNonceParameterName(String nonceParameterName) {
        this.nonceParameterName = nonceParameterName;
    }

    public void setTimestampParameterName(String timestampParameterName) {
        this.timestampParameterName = timestampParameterName;
    }

    public void setSignParameterName(String signParameterName) {
        this.signParameterName = signParameterName;
    }

    public void setAlgorithm(EnigmaAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public void setMaxAllowedTimestampDiff(Duration maxAllowedTimestampDiff) {
        this.maxAllowedTimestampDiff = maxAllowedTimestampDiff;
    }

    public void setNonceHeaderName(String nonceHeaderName) {
        this.nonceHeaderName = nonceHeaderName;
    }

    public void setTimestampHeaderName(String timestampHeaderName) {
        this.timestampHeaderName = timestampHeaderName;
    }

    public void setSignHeaderName(String signHeaderName) {
        this.signHeaderName = signHeaderName;
    }

}
