package android.util;

import java.util.Base64;

/**
 * 桌面端兼容垫片（shim）：模拟安卓 {@code android.util.Base64} 的 API，
 * 以便 TVBox 安卓爬虫源码在桌面 JVM（FreeBox）上无需逐文件改写即可编译运行。
 *
 * 注意：标准 Java 的 {@link java.util.Base64} 默认字母表为 {@code A-Za-z0-9+/}（标准），
 * 而安卓的 NO_WRAP 仅表示“不换行”，不影响解码逻辑；故此处直接委托标准实现。
 * 若某源使用了 URL_SAFE（-_）字母表，可在本类按 flags 切换到 {@code getUrlDecoder/Encoder}。
 */
public class Base64 {

    public static final int DEFAULT = 0;
    public static final int NO_PADDING = 1;
    public static final int NO_WRAP = 2;
    public static final int URL_SAFE = 8;
    public static final int NO_CLOSE = 16;

    public static byte[] decode(String s, int flags) {
        try {
            if ((flags & URL_SAFE) != 0) {
                return java.util.Base64.getUrlDecoder().decode(s);
            }
            return java.util.Base64.getDecoder().decode(s);
        } catch (IllegalArgumentException e) {
            // 部分安卓源用 URL_SAFE 字符但没标 flag，容错重试
            try {
                return java.util.Base64.getUrlDecoder().decode(s);
            } catch (IllegalArgumentException ignore) {
                return new byte[0];
            }
        }
    }

    public static byte[] decode(byte[] input, int flags) {
        return decode(new String(input, java.nio.charset.StandardCharsets.UTF_8), flags);
    }

    public static byte[] decode(String s) {
        return decode(s, DEFAULT);
    }

    public static byte[] decode(byte[] input) {
        return decode(input, DEFAULT);
    }

    public static String encodeToString(byte[] input, int flags) {
        if ((flags & URL_SAFE) != 0) {
            return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(input);
        }
        return java.util.Base64.getEncoder().withoutPadding().encodeToString(input);
    }

    public static String encodeToString(byte[] input) {
        return encodeToString(input, DEFAULT);
    }

    public static byte[] encode(byte[] input, int flags) {
        return encodeToString(input, flags).getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    public static byte[] encode(byte[] input) {
        return encode(input, DEFAULT);
    }
}
