/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *   ___ _ __ (_) __ _ _ __ ___   __ _
 *  / _ \ '_ \| |/ _` | '_ ` _ \ / _` |
 * |  __/ | | | | (_| | | | | | | (_| |
 *  \___|_| |_|_|\__, |_| |_| |_|\__,_|
 *              |___/      https://github.com/yingzhuo/enigma
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package enigma.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.core.Ordered;

import java.io.Serializable;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Set;

/**
 * @author 应卓
 * @since 0.0.1
 */
@ConfigurationProperties(prefix = "enigma")
public class EnigmaProperties implements Serializable {

    @DurationUnit(ChronoUnit.MINUTES)
    private Duration maxAllowedTimestampDiff = null;
    private EnigmaInterceptorProperties interceptor = new EnigmaInterceptorProperties();
    private EnigmaResolverProperties resolver = new EnigmaResolverProperties();
    private boolean debugMode = false;

    public Duration getMaxAllowedTimestampDiff() {
        return maxAllowedTimestampDiff;
    }

    public void setMaxAllowedTimestampDiff(Duration maxAllowedTimestampDiff) {
        this.maxAllowedTimestampDiff = maxAllowedTimestampDiff;
    }

    public EnigmaInterceptorProperties getInterceptor() {
        return interceptor;
    }

    public void setInterceptor(EnigmaInterceptorProperties enigmaInterceptorProperties) {
        this.interceptor = enigmaInterceptorProperties;
    }

    public EnigmaResolverProperties getResolver() {
        return resolver;
    }

    public void setResolver(EnigmaResolverProperties enigmaResolverProperties) {
        this.resolver = enigmaResolverProperties;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    // ----------------------------------------------------------------------------------------------------------------

    public static class EnigmaInterceptorProperties implements Serializable {

        private int order = Ordered.LOWEST_PRECEDENCE;
        private Set<String> excludeAntPatterns;

        public EnigmaInterceptorProperties() {
        }

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public Set<String> getExcludeAntPatterns() {
            return excludeAntPatterns;
        }

        public void setExcludeAntPatterns(Set<String> excludeAntPatterns) {
            this.excludeAntPatterns = excludeAntPatterns;
        }
    }

    // ----------------------------------------------------------------------------------------------------------------

    public static class EnigmaResolverProperties implements Serializable {

        private String nonceParameterName = "_nonce";
        private String timestampParameterName = "_timestamp";
        private String signParameterName = "_sign";
        private String nonceHeaderName = null;
        private String timestampHeaderName = null;
        private String signHeaderName = null;

        public EnigmaResolverProperties() {
        }

        public String getNonceParameterName() {
            return nonceParameterName;
        }

        public void setNonceParameterName(String nonceParameterName) {
            this.nonceParameterName = nonceParameterName;
        }

        public String getTimestampParameterName() {
            return timestampParameterName;
        }

        public void setTimestampParameterName(String timestampParameterName) {
            this.timestampParameterName = timestampParameterName;
        }

        public String getSignParameterName() {
            return signParameterName;
        }

        public void setSignParameterName(String signParameterName) {
            this.signParameterName = signParameterName;
        }

        public String getNonceHeaderName() {
            return nonceHeaderName;
        }

        public void setNonceHeaderName(String nonceHeaderName) {
            this.nonceHeaderName = nonceHeaderName;
        }

        public String getTimestampHeaderName() {
            return timestampHeaderName;
        }

        public void setTimestampHeaderName(String timestampHeaderName) {
            this.timestampHeaderName = timestampHeaderName;
        }

        public String getSignHeaderName() {
            return signHeaderName;
        }

        public void setSignHeaderName(String signHeaderName) {
            this.signHeaderName = signHeaderName;
        }
    }

}
