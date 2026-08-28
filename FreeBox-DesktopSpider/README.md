# FreeBox-DesktopSpider（TVBox 安卓源 → 电脑端重编译工程）

> ⚠️ **个人备份 / 自用**。本工程把第三方 TVBox 安卓源（[lushunming/AndroidCatVodSpider](https://github.com/lushunming/AndroidCatVodSpider)）的蜘蛛 Java 源码**重编译**为可在 FreeBox（桌面 JVM）加载的 spider jar。
> 原作者：**lushunming**（MIT 协议）。**非本人作品，仅供自用，未授权对外分发**。

---

## 为什么需要这个工程

FreeBox 桌面端用 JVM 加载 spider jar，而安卓源的 `custom_spider.jar` 内部是 `classes.dex`（安卓字节码），桌面 JVM **无法加载**——这就是"安卓源在电脑上用不了"的根因。

本工程把安卓蜘蛛源码重新编译为纯 Java 的 `.class` jar，从而能在 FreeBox 上加载。

## 原理（已源码级验证）

- FreeBox 的 `SpiderInvokeUtil` 通过**反射**调用 spider 的 `homeContent / categoryContent / detailContent / searchContent / playerContent / init` 等方法，**不要求继承特定基类**，只需方法签名匹配。
- 安卓蜘蛛源码平台无关度较高：网络层 `net/OkHttp` 是 **0 安卓依赖**的纯 okhttp3；爬虫基类仅依赖 1 个 `android.content.Context`；典型站点（如 `YiSo`）仅依赖 1 个 `android.util.Base64`。
- 因此改造重点是替换少量安卓 API，而非重写逻辑。

### 已提供的兼容垫片（shim，无需你改）

| 安卓 API | 处理 |
|----------|------|
| `android.util.Base64` | ✅ `android/util/Base64.java` 兼容实现 |
| `android.text.TextUtils` | ✅ `android/text/TextUtils.java` 兼容实现 |
| `android.util.Log` | ✅ `android/util/Log.java` 兼容实现（输出到标准输出） |

这三个垫片覆盖源码中最高频的安卓依赖（约 50+ 文件用到 TextUtils）。

## 工程结构

```
FreeBox-DesktopSpider/
├── build.gradle              # Gradle 构建（产出 custom_spider_desktop.jar）
├── batch_patch_spider.py     # 批量把 init(Context) 改为 init()（一次性）
├── desktop-index.json        # 桌面版源配置（spider 指向本 jar，43 个站点）
├── src/main/java/
│   ├── android/util/Base64.java        # 兼容垫片
│   ├── android/util/Log.java           # 兼容垫片
│   ├── android/text/TextUtils.java     # 兼容垫片
│   └── com/github/catvod/...           # 安卓蜘蛛源码（改造后）
└── README.md
```

## 本机编译步骤（需 Windows + JDK 17+ + Gradle 8.x + 网络）

1. 安装 **JDK 17+** 与 **Gradle 8.x**，确保 `java -version`、`gradle -version` 可用。
2. 进入本目录，先运行一次性批量补丁（把仍带 `Context` 的 `init` 改为无参）：
   ```bash
   python batch_patch_spider.py
   ```
3. 编译并打包 jar：
   ```bash
   gradle build
   ```
   产物：`build/libs/custom_spider_desktop-1.0.0.jar`
4. 若编译报错，按错误信息修对应 spider 文件的安卓依赖（见下表），改完重跑 `gradle build`。

### 仍需手工处理的安卓依赖清单

垫片已覆盖 Base64 / TextUtils / Log。以下需按编译错误逐个处理：

| 安卓 API | 处理方式 |
|----------|----------|
| `android.content.Context`（方法体内仍使用） | 若 `init` 外的方法体用了 `context` 变量，手工改为桌面等价逻辑或移除 |
| `android.net.Uri` | 改用 `java.net.URI`：`Uri.parse(x).getScheme()/getHost()` → `URI.create(x).getScheme()/getHost()` |
| `android.os.Build` | 版本判断通常可删除 |
| `android.os.SystemClock` | `SystemClock.sleep` → `Thread.sleep` |
| `android.graphics.Bitmap` | 图片处理类，桌面端多半无需，stub 或删除该方法 |
| `android.webkit.WebView` | 依赖 WebView 渲染 JS 的站点（如 `JSDemo`）在桌面端可能无法使用，建议从 `desktop-index.json` 的 sites 中排除该 site |

## 使用

1. 把编译出的 `custom_spider_desktop-1.0.0.jar` 与 `desktop-index.json` 放在**同一目录**（或同一 http 服务器）。
2. 在 FreeBox 中：**设置 → 数据源 → 导入** `desktop-index.json`（填写其 http 地址，或本地 `file:///` 路径）。
3. 能编译进 jar 的 spider 站点即可使用；编译失败 / 未移植的站点在导入后该站点不可用（**不影响其它站点**）。

## ⚠️ 重要限制（务必阅读）

- **本工程在沙盒环境无法编译验证**（无 JDK/Gradle、大文件下载受限）。交付的是"**可本机构建的脚手架 + 方法论**"，需你在本机 `gradle build` 并可能按错误修少量站点——**不保证 60+ 站点全部开箱即用**。
- 依赖 **WebView / 系统 UI / 系统服务**的站点（如 `JSDemo` 用 JS 引擎、部分站点用安卓 WebView 解析）在桌面端**可能无法播放**，属平台架构差异，非代码可绕过。
- 请仅使用你**有权使用**的来源，遵守所在地法律法规；本工程不内置、不分发任何影视内容。

## 合规

- 原作者：**lushunming**（MIT 协议）。本仓库为个人备份 / 自用，**非本人作品，未授权对外分发**。
- 原始安卓源：<https://github.com/lushunming/AndroidCatVodSpider>
- 桌面播放器：FreeBox（<https://github.com/kknifer7/FreeBox>，GPL-3.0）
