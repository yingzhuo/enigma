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
import enigma.util.StringUtils;
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
import java.util.Set;
import java.util.TreeSet;

/**
 * @author 应卓
 * @since 0.0.1
 */
public class EnigmaInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger("enigma");

    private static final Class<EnigmaIgnored> IGNORED = EnigmaIgnored.class;
    private final PathMatcher pathMatcher = new AntPathMatcher();
    private EnigmaAlgorithm algorithm = EnigmaAlgorithm.getDefault();
    private Set<String> excludeAntPatterns;
    private String nonceParameterName;
    private String timestampParameterName;
    private String signParameterName;
    private String nonceHeaderName;
    private String timestampHeaderName;
    private String signHeaderName;
    private Duration maxAllowedTimestampDiff;
    private boolean debugMode = false;

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
                return true;
            }
        }

        if (this.skipByAnnotation(handler)) {
            return true;
        }

        final Enigma enigma = resolve(request);
        if (!enigma.isValid()) {

            if (debugMode) {
                log.warn("invalid enigma's instance");
                log.warn("enigma = {}", enigma);
                return true;
            } else {
                throw new InvalidRequestException("invalid request");
            }

        } else {
            EnigmaContext.set(enigma);
        }

        final String parametersAsString = flatAndSort(request.getParameterMap(), signParameterName);
        final String hashedParameters = algorithm.encode(parametersAsString);

        // 检查签名
        final String sign = enigma.getSign();

        if (!algorithm.matches(hashedParameters, sign)) {
            if (debugMode) {
                log.warn("invalid sign");
                log.warn("actual-sign = {}", sign);
                log.warn("expected-sign = {}", hashedParameters);
                return true;
            } else {
                throw new InvalidSignException("invalid sign");
            }
        }

        // 检查时间戳
        if (!isTimestampCheckingDisabled()) {
            long now = System.currentTimeMillis();
            long diff = Math.abs(now - enigma.getTimestamp());
            if (diff > maxAllowedTimestampDiff.toMillis()) {
                if (debugMode) {
                    log.warn("invalid timestamp");
                    log.warn("actual-timestamp = {}", enigma.getTimestamp());
                    log.warn("server-timestamp = {}", now);
                    return true;
                } else {
                    throw new InvalidTimestampException("invalid timestamp");
                }
            }
        }

        return true;
    }

    public Enigma resolve(HttpServletRequest request) {
        String nonce = resolveNonce(request);
        Long timestamp = resolveTimestamp(request);
        String sign = resolveSign(request);

        final Enigma enigma = new Enigma();
        enigma.setNonce(nonce);
        enigma.setTimestamp(timestamp);
        enigma.setSign(sign);
        return enigma;
    }

    private String resolveNonce(HttpServletRequest request) {
        String value = null;
        if (StringUtils.isNotBlank(nonceParameterName)) {
            value = StringUtils.blankToNull(request.getParameter(nonceParameterName));
        }
        if (value == null && StringUtils.isNotBlank(nonceHeaderName)) {
            value = StringUtils.blankToNull(request.getHeader(nonceHeaderName));
        }
        return StringUtils.blankToNull(value);
    }

    private Long resolveTimestamp(HttpServletRequest request) {
        Long value = null;
        if (StringUtils.isNotBlank(timestampParameterName)) {
            value = string2Long(request.getParameter(timestampParameterName));
        }
        if (value == null && StringUtils.isNotBlank(timestampHeaderName)) {
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
        if (StringUtils.isNotBlank(signParameterName)) {
            value = StringUtils.blankToNull(request.getParameter(signParameterName));
        }
        if (value == null && signHeaderName != null) {
            value = StringUtils.blankToNull(request.getHeader(signHeaderName));
        }
        return StringUtils.blankToNull(value);
    }

    private String flatAndSort(Map<String, String[]> source, String signParameterName) {
        final StringBuilder stringBuilder = new StringBuilder();

        for (String key : new TreeSet<>(source.keySet())) {

            if (signParameterName != null && signParameterName.equals(key)) {
                continue;
            }

            String[] values = source.get(key);
            String value = StringUtils.join(values);
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
            return Long.parseLong(s);
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

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

}
