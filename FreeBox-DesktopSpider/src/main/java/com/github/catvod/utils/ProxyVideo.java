package com.github.catvod.utils;

import android.text.TextUtils;

import com.github.catvod.net.OkHttp;
import com.github.catvod.spider.Proxy;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.Response;

/**
 * 桌面端（FreeBox）视频代理工具。
 *
 * 与安卓版差异：
 * - 移除 android.os.SystemClock（改用 Thread.sleep）。
 * - 移除 SpiderDebug（改用标准输出）。
 * - 移除对 DownloadMT 多线程下载模块的依赖，proxyMultiThread 暂委托单线程 proxy 实现。
 */
public class ProxyVideo {

    private static final String GO_SERVER = "http://127.0.0.1:7777/";
    private static final int THREAD_NUM = Runtime.getRuntime().availableProcessors() * 2;
    private static Map<String, Object[]> infos = new HashMap<>();


    public static String buildCommonProxyUrl(String url, Map<String, String> headers) {
        return Proxy.getUrl() + "?do=proxy&url=" + Util.base64Encode(url.getBytes(Charset.defaultCharset())) + "&header=" + Util.base64Encode((new Gson().toJson(headers)).getBytes(Charset.defaultCharset()));
    }

    public static void go() {
        boolean close = OkHttp.string(GO_SERVER).isEmpty();
        if (close) OkHttp.string("http://127.0.0.1:" + Proxy.getPort() + "/go");
        if (close) while (OkHttp.string(GO_SERVER).isEmpty()) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException ignored) {
            }
        }
    }

    public static String goVer() {
        try {
            go();
            String result = OkHttp.string(GO_SERVER + "version");
            return new JSONObject(result).optString("version");
        } catch (Exception e) {
            return "";
        }
    }

    public static String url(String url, int thread) {
        if (!TextUtils.isEmpty(goVer()) && url.contains("/proxy?")) url += "&response=url";
        return String.format(Locale.getDefault(), "%s?url=%s&thread=%d", GO_SERVER, URLEncoder.encode(url), thread);
    }

    public static Object[] proxy(String url, Map<String, String> headers) throws Exception {
        System.out.println(" ++start proxy:");
        Response response = OkHttp.newCall(url, headers);
        String contentType = StringUtils.isAllBlank(response.headers().get("Content-Type")) ? response.headers().get("content-type") : response.headers().get("Content-Type");
        String contentDisposition = response.headers().get("Content-Disposition");
        if (contentDisposition != null && StringUtils.isAllBlank(contentType)) {
            contentType = getMimeType(contentDisposition);
        }
        Map<String, String> respHeaders = new HashMap<>();
        for (String key : response.headers().names()) {
            respHeaders.put(key, response.headers().get(key));
        }
        return new Object[]{response.code(), contentType, response.body().byteStream(), respHeaders};
    }


    public static Object[] proxyMultiThread(String url, Map<String, String> headers) {
        // 桌面端暂用单线程代理（未集成多线程下载 DownloadMT）
        try {
            return proxy(url, headers);
        } catch (Exception e) {
            return new Object[]{500, "text/plain", null, new HashMap<>()};
        }
    }


    public static Map<String, String> parseRange(String range) {
        if (StringUtils.isNoneBlank(range)) {
            String[] ranges = StringUtils.split(range.replace("bytes=", ""), "-");
            String start = ranges[0];
            String end = ranges.length > 1 ? ranges[1] : "";
            return Map.of("start", start, "end", end);
        }
        return null;
    }

    public static String getMimeType(String contentDisposition) {
        if (contentDisposition.endsWith(".mp4")) {
            return "video/mp4";
        } else if (contentDisposition.endsWith(".webm")) {
            return "video/webm";
        } else if (contentDisposition.endsWith(".avi")) {
            return "video/x-msvideo";
        } else if (contentDisposition.endsWith(".wmv")) {
            return "video/x-ms-wmv";
        } else if (contentDisposition.endsWith(".flv")) {
            return "video/x-flv";
        } else if (contentDisposition.endsWith(".mov")) {
            return "video/quicktime";
        } else if (contentDisposition.endsWith(".mkv")) {
            return "video/x-matroska";
        } else if (contentDisposition.endsWith(".mpeg")) {
            return "video/mpeg";
        } else if (contentDisposition.endsWith(".3gp")) {
            return "video/3gpp";
        } else if (contentDisposition.endsWith(".ts")) {
            return "video/MP2T";
        } else if (contentDisposition.endsWith(".mp3")) {
            return "audio/mp3";
        } else if (contentDisposition.endsWith(".wav")) {
            return "audio/wav";
        } else if (contentDisposition.endsWith(".aac")) {
            return "audio/aac";
        } else {
            return null;
        }
    }

}
