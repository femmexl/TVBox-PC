package com.github.catvod.spider;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 桌面端（FreeBox）爬虫初始化入口。
 *
 * FreeBox 的 {@code SpiderJarLoader.invokeInit} 会通过反射调用本类的
 * <b>无参</b> {@code static void init()} 方法，因此必须提供该签名。
 *
 * 安卓版 Init 依赖 Handler/Looper/Application/Activity 等安卓运行时，
 * 桌面端不需要也不存在这些，故改为最小实现：仅保留线程池与执行入口。
 */
public class Init {

    private static final ExecutorService executor = Executors.newFixedThreadPool(5);

    /** FreeBox 调用此无参 init 完成爬虫包初始化（桌面端无需任何动作） */
    public static void init() {
        // no-op on desktop
    }

    public static void execute(Runnable runnable) {
        executor.execute(runnable);
    }

    public static void run(Runnable runnable) {
        // 桌面端直接在新线程执行，不依赖安卓 Handler/Looper
        new Thread(runnable).start();
    }
}
