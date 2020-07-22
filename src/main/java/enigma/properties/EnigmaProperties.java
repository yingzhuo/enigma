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
    private Set<String> excludeAntPatterns;
    private String nonceParameterName = "_nonce";
    private String timestampParameterName = "_timestamp";
    private String signParameterName = "_sign";
    @DurationUnit(ChronoUnit.MINUTES)
    private Duration maxAllowedTimestampDiff = null;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<String> getExcludeAntPatterns() {
        return excludeAntPatterns;
    }

    public void setExcludeAntPatterns(Set<String> excludeAntPatterns) {
        this.excludeAntPatterns = excludeAntPatterns;
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

}
