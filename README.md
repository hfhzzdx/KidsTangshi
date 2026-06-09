# KidsTangshi - 幼儿学唐诗宋词

一款专为幼儿设计的唐诗宋词学习 Android 应用，温馨卡通风格，大字体大按钮，操作简单。

## ✨ 功能特性

- 📖 **精选诗词**: 50+ 首精选唐诗宋词，内置离线数据
- 🔊 **语音朗读**: 系统 TTS 自动朗读，支持语速调节
- 📅 **每日推荐**: 每天推荐一首新诗，培养学习习惯
- ❤️ **收藏功能**: 收藏喜欢的诗词，随时复习
- ✅ **学习打卡**: 记录学习进度，激励持续学习
- 🎨 **温馨主题**: 暖黄/浅蓝/淡粉卡通配色，护眼舒适
- 📱 **HyperOS 兼容**: 兼容小米 HyperOS 3 (Android 14)

## 📱 系统要求

- Android 7.0 (API 24) 及以上
- 兼容 Android 14 / 小米 HyperOS 3
- 需要 TTS 中文语音引擎（系统自带）

## 🛠️ 技术栈

- Kotlin + Jetpack Compose (Material 3)
- minSdk 24 / targetSdk 34
- Gradle 8.4 + AGP 8.2 + JDK 17
- 无第三方依赖，纯 Android SDK

## 🏗️ 构建

### 本地构建

```bash
# 1. 克隆项目
git clone https://github.com/你的用户名/KidsTangshi.git
cd KidsTangshi

# 2. 生成 Gradle Wrapper（如果没有 gradlew）
gradle wrapper --gradle-version 8.4

# 3. 构建 Debug APK
./gradlew assembleDebug

# APK 输出路径
# app/build/outputs/apk/debug/app-debug.apk
```

### GitHub Actions 自动构建

推送代码到 `main` 或 `master` 分支会自动触发构建，或在 Actions 页面手动触发 `workflow_dispatch`。

构建完成后在 Actions → Run → Artifacts 中下载 APK。

## 📁 项目结构

```
KidsTangshi/
├── .github/workflows/build.yml    # CI/CD 配置
├── app/
│   ├── src/main/
│   │   ├── java/com/kids/tangshi/
│   │   │   ├── MainActivity.kt
│   │   │   ├── data/              # 数据模型和仓库
│   │   │   ├── ui/                # Compose UI
│   │   │   │   ├── theme/         # 主题配色
│   │   │   │   ├── home/          # 首页
│   │   │   │   ├── detail/        # 详情页
│   │   │   │   ├── settings/      # 设置页
│   │   │   │   └── components/    # 公共组件
│   │   │   └── util/              # TTS 工具
│   │   ├── assets/poems/          # 诗词 JSON 数据
│   │   └── res/                   # Android 资源
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## 📄 数据来源

诗词数据精选自 [chinese-poetry](https://github.com/android9527/chinese-poetry) 开源项目。

## 📜 License

MIT License
