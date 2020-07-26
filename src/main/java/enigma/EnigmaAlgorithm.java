/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *   ___ _ __ (_) __ _ _ __ ___   __ _
 *  / _ \ '_ \| |/ _` | '_ ` _ \ / _` |
 * |  __/ | | | | (_| | | | | | | (_| |
 *  \___|_| |_|_|\__, |_| |_| |_|\__,_|
 *              |___/      https://github.com/yingzhuo/enigma
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package enigma;

import enigma.util.DigestUtils;

/**
 * @author 应卓
 * @since 0.0.1
 */
public interface EnigmaAlgorithm {

    public static EnigmaAlgorithm getDefault() {
        return new EnigmaAlgorithm() {
        };
    }

    public default String encode(String parametersAsString) {
        return DigestUtils.sha256(DigestUtils.md5(parametersAsString));
    }

    public default boolean matches(String hashedParameters, String sign) {
        if (hashedParameters == null || sign == null) return false;
        return hashedParameters.equalsIgnoreCase(sign);
    }

}
