/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *   ___ _ __ (_) __ _ _ __ ___   __ _
 *  / _ \ '_ \| |/ _` | '_ ` _ \ / _` |
 * |  __/ | | | | (_| | | | | | | (_| |
 *  \___|_| |_|_|\__, |_| |_| |_|\__,_|
 *              |___/      https://github.com/yingzhuo/enigma
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package enigma.util;

/**
 * (内部工具)
 *
 * @author 应卓
 * @since 1.0.2
 */
public final class StringUtils {

    private StringUtils() {
    }

    public static boolean isBlank(String string) {
        if (string == null) return true;
        return string.trim().equals("");
    }

    public static boolean isNotBlank(String string) {
        return !isBlank(string);
    }

    public static String blankToNull(String string) {
        return isBlank(string) ? null : string;
    }

    public static String join(String[] values) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            stringBuilder.append(values[i]);
            if (i != values.length - 1) {
                stringBuilder.append(',');
            }
        }
        return stringBuilder.toString();
    }

}
