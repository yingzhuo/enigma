/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *   ___ _ __ (_) __ _ _ __ ___   __ _
 *  / _ \ '_ \| |/ _` | '_ ` _ \ / _` |
 * |  __/ | | | | (_| | | | | | | (_| |
 *  \___|_| |_|_|\__, |_| |_| |_|\__,_|
 *              |___/      https://github.com/yingzhuo/enigma
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package enigma.core;

import enigma.Enigma;

import java.util.Optional;

/**
 * @author 应卓
 * @since 0.0.3
 */
public final class EnigmaContext {

    private EnigmaContext() {
    }

    private static final ThreadLocal<Enigma> HOLDER = ThreadLocal.withInitial(() -> null);

    static void set(Enigma enigma) {
        HOLDER.set(enigma);
    }

    static void remove() {
        HOLDER.remove();
    }

    public static Enigma get() {
        return HOLDER.get();
    }

    public static String getNonce() {
        try {
            return get().getNonce();
        } catch (Exception e) {
            return null;
        }
    }

    public static Optional<String> getNonceOptional() {
        return Optional.ofNullable(getNonce());
    }

    public static String getSign() {
        try {
            return get().getSign();
        } catch (Exception e) {
            return null;
        }
    }

    public static Optional<String> getSignOptional() {
        return Optional.ofNullable(getSign());
    }

    public static Long getTimestamp() {
        try {
            return get().getTimestamp();
        } catch (Exception e) {
            return null;
        }
    }

    public static Optional<Long> getTimestampOptional() {
        return Optional.ofNullable(getTimestamp());
    }

}
