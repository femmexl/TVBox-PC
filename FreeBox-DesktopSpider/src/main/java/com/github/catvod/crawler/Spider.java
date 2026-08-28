package com.github.catvod.crawler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Dns;
import okhttp3.OkHttpClient;

/**
 * 桌面端（FreeBox）爬虫基类。
 *
 * 与安卓版 {@code com.github.catvod.crawler.Spider} 的差异：
 * 1. 移除了 {@code android.content.Context} 依赖（方法签名由 init(Context) 改为 init() / init(String)），
 *    以匹配 FreeBox 的 SpiderInvokeUtil 反射调用约定（init() 无参、init(String extend)）。
 * 2. 其余标准方法签名（homeContent / categoryContent / detailContent / searchContent /
 *    playerContent / manualVideoCheck / isVideoFormat / proxyLocal / action / destroy / client）
 *    与 FreeBox 期望完全一致，保持原样。
 */
public abstract class Spider {

    public void init() throws Exception {
    }

    public void init(String extend) throws Exception {
        init();
    }

    public String homeContent(boolean filter) throws Exception {
        return "";
    }

    public String homeVideoContent() throws Exception {
        return "";
    }

    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {
        return "";
    }

    public String detailContent(List<String> ids) throws Exception {
        return "";
    }

    public String searchContent(String key, boolean quick) throws Exception {
        return "";
    }

    public String searchContent(String key, boolean quick, String pg) throws Exception {
        return "";
    }

    public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {
        return "";
    }

    public boolean manualVideoCheck() throws Exception {
        return false;
    }

    public boolean isVideoFormat(String url) throws Exception {
        return false;
    }

    public Object[] proxyLocal(Map<String, String> params) throws Exception {
        return null;
    }

    public String action(String action) {
        return null;
    }

    public void destroy() {
    }

    public static Dns safeDns() {
        return null;
    }

    public static OkHttpClient client() {
        return null;
    }
}
