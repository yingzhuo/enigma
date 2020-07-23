/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *   ___ _ __ (_) __ _ _ __ ___   __ _
 *  / _ \ '_ \| |/ _` | '_ ` _ \ / _` |
 * |  __/ | | | | (_| | | | | | | (_| |
 *  \___|_| |_|_|\__, |_| |_| |_|\__,_|
 *              |___/      https://github.com/yingzhuo/enigma
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package enigma.impl;

import enigma.EnigmaAlgorithm;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author 应卓
 * @since 0.0.1
 */
public class DefaultEnigmaAlgorithm implements EnigmaAlgorithm {

    @Override
    public String encode(String parametersAsString) {
        return DigestUtils.sha256Hex(DigestUtils.md5Hex(parametersAsString));
    }

}
