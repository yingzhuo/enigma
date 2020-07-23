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

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Set;

/**
 * @author 应卓
 * @since 0.0.1
 */
@ConfigurationProperties(prefix = "enigma")
public class EnigmaProperties {

    private boolean enabled = true;

    private String nonceParameterName = "_nonce";

    private String timestampParameterName = "_timestamp";

    private String signParameterName = "_sign";

    @DurationUnit(ChronoUnit.MINUTES)
    private Duration maxAllowedTimestampDiff = null;

    private Interceptor interceptor = new Interceptor();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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

    public Duration getMaxAllowedTimestampDiff() {
        return maxAllowedTimestampDiff;
    }

    public void setMaxAllowedTimestampDiff(Duration maxAllowedTimestampDiff) {
        this.maxAllowedTimestampDiff = maxAllowedTimestampDiff;
    }

    public Interceptor getInterceptor() {
        return interceptor;
    }

    public void setInterceptor(Interceptor interceptor) {
        this.interceptor = interceptor;
    }

    // ----------------------------------------------------------------------------------------------------------------

    /**
     * @since 0.0.2
     */
    public static class Interceptor {
        private int order = Ordered.LOWEST_PRECEDENCE;

        private Set<String> excludeAntPatterns;

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

}
