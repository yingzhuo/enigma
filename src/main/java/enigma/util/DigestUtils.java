/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *   ___ _ __ (_) __ _ _ __ ___   __ _
 *  / _ \ '_ \| |/ _` | '_ ` _ \ / _` |
 * |  __/ | | | | (_| | | | | | | (_| |
 *  \___|_| |_|_|\__, |_| |_| |_|\__,_|
 *              |___/      https://github.com/yingzhuo/enigma
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package enigma.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * (内部工具)
 *
 * @author 应卓
 * @since 1.0.1
 */
public final class DigestUtils {

    private DigestUtils() {
    }

    public static String md5(String string) {
        return digest(string, "MD5");
    }

    public static String sha256(String string) {
        return digest(string, "SHA-256");
    }

    private static String digest(String string, String alg) {
        final byte[] defaultBytes = string.getBytes();
        try {
            final MessageDigest digest = MessageDigest.getInstance(alg);
            digest.reset();
            digest.update(defaultBytes);
            final byte[] messageDigest = digest.digest();

            final StringBuilder hexString = new StringBuilder();
            for (final byte element : messageDigest) {
                final String hex = Integer.toHexString(0xFF & element);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            string = hexString + "";
        } catch (final NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }
        return string;
    }

}
