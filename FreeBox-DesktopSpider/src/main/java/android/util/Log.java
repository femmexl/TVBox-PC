package android.util;

/**
 * 桌面端兼容垫片（shim）：模拟安卓 {@code android.util.Log}，将日志输出到标准输出。
 */
public class Log {

    public static int d(String tag, String msg) {
        System.out.println("D/" + tag + ": " + msg);
        return 0;
    }

    public static int i(String tag, String msg) {
        System.out.println("I/" + tag + ": " + msg);
        return 0;
    }

    public static int w(String tag, String msg) {
        System.out.println("W/" + tag + ": " + msg);
        return 0;
    }

    public static int e(String tag, String msg) {
        System.out.println("E/" + tag + ": " + msg);
        return 0;
    }

    public static int v(String tag, String msg) {
        System.out.println("V/" + tag + ": " + msg);
        return 0;
    }

    public static int d(String tag, String msg, Throwable tr) {
        System.out.println("D/" + tag + ": " + msg);
        if (tr != null) tr.printStackTrace();
        return 0;
    }

    public static int i(String tag, String msg, Throwable tr) {
        System.out.println("I/" + tag + ": " + msg);
        if (tr != null) tr.printStackTrace();
        return 0;
    }

    public static int w(String tag, String msg, Throwable tr) {
        System.out.println("W/" + tag + ": " + msg);
        if (tr != null) tr.printStackTrace();
        return 0;
    }

    public static int e(String tag, String msg, Throwable tr) {
        System.out.println("E/" + tag + ": " + msg);
        if (tr != null) tr.printStackTrace();
        return 0;
    }
}
