/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *   ___ _ __ (_) __ _ _ __ ___   __ _
 *  / _ \ '_ \| |/ _` | '_ ` _ \ / _` |
 * |  __/ | | | | (_| | | | | | | (_| |
 *  \___|_| |_|_|\__, |_| |_| |_|\__,_|
 *              |___/      https://github.com/yingzhuo/enigma
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package enigma.configuration;

import enigma.EnigmaAlgorithm;
import enigma.core.EnigmaInterceptor;
import enigma.properties.EnigmaProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Optional;

/**
 * @author 应卓
 * @since 0.0.1
 */
@ConditionalOnWebApplication
@EnableConfigurationProperties(EnigmaProperties.class)
@ConditionalOnProperty(prefix = "enigma", name = "enabled", havingValue = "true", matchIfMissing = true)
public class EnigmaAutoConfiguration implements WebMvcConfigurer {

    @Autowired
    private EnigmaProperties properties;

    @Autowired(required = false)
    private EnigmaAlgorithm algorithm;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        final EnigmaInterceptor interceptor = new EnigmaInterceptor();
        Optional.ofNullable(algorithm).ifPresent(interceptor::setAlgorithm);
        Optional.ofNullable(properties.getInterceptor().getExcludeAntPatterns()).ifPresent(interceptor::setExcludeAntPatterns);
        Optional.ofNullable(properties.getSignParameterName()).ifPresent(interceptor::setSignParameterName);
        Optional.ofNullable(properties.getNonceParameterName()).ifPresent(interceptor::setNonceParameterName);
        Optional.ofNullable(properties.getTimestampParameterName()).ifPresent(interceptor::setTimestampParameterName);
        Optional.ofNullable(properties.getMaxAllowedTimestampDiff()).ifPresent(interceptor::setMaxAllowedTimestampDiff);
        registry.addInterceptor(interceptor).order(properties.getInterceptor().getOrder());
    }

}
