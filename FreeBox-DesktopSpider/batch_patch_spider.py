#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
批量补丁：把安卓版 spider 的 init(Context) / init(Context, String) 改为
FreeBox 反射调用所需的无参 / 单 String 参数版本。

说明：
- FreeBox 的 SpiderInvokeUtil 通过反射调用 spider.init() 或 spider.init(String)，
  因此站点蜘蛛必须提供这两个签名（安卓版是 init(Context) / init(Context, String)）。
- 本脚本仅做方法签名层面的文本替换；若某个 spider 的 init 方法体内真正
  “使用了” context 变量（如读取 SharedPreferences），替换后会残留编译错误，
  需在该 spider 文件内手工处理（教程中有说明）。

可在本机（已装 JDK + Gradle）重编译前运行一次：
    python batch_patch_spider.py
"""
import os

SPIDER_DIR = os.path.join(os.path.dirname(__file__), "src", "main", "java",
                          "com", "github", "catvod", "spider")

REPLACEMENTS = [
    ("public void init(Context context, String extend) {", "public void init(String extend) {"),
    ("public void init(Context context,String extend) {", "public void init(String extend) {"),
    ("public void init(Context context) {", "public void init() {"),
    ("public void init(Context context)throws Exception {", "public void init() throws Exception {"),
    ("super.init(context, extend);", "super.init(extend);"),
    ("super.init(context);", "super.init();"),
    ("this.init(context);", "this.init();"),
]


def patch_file(path):
    with open(path, encoding="utf-8") as f:
        txt = f.read()
    orig = txt
    for old, new in REPLACEMENTS:
        txt = txt.replace(old, new)
    # 仅当文件中已不再出现 Context（除 import 行）时，移除 import
    stripped = txt.replace("import android.content.Context;\n", "")
    if "Context" not in stripped:
        txt = stripped
    if txt != orig:
        with open(path, "w", encoding="utf-8") as f:
            f.write(txt)
        return True
    return False


def main():
    if not os.path.isdir(SPIDER_DIR):
        print("spider dir not found:", SPIDER_DIR)
        return
    count = 0
    for fn in sorted(os.listdir(SPIDER_DIR)):
        if fn.endswith(".java"):
            if patch_file(os.path.join(SPIDER_DIR, fn)):
                count += 1
                print("patched:", fn)
    print(f"done. patched {count} file(s).")


if __name__ == "__main__":
    main()
