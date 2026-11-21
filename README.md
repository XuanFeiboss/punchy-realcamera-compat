# Punchy! × Real Camera Compat

一个面向 **Minecraft 1.21.1 / NeoForge** 的客户端兼容补丁，用于解决 **Real Camera** 与 **Punchy! / Punchy! Moves** 在第一人称下的冲突。

## 它解决什么问题

Real Camera 在第一人称下会做两件事：

1. 把整具玩家模型重新画在第一人称视角里（`renderModel`）；
2. 为了不让原版手挡住模型，直接取消原版第一人称手部渲染（`DisableHelper.RENDER_HANDS = RealCameraCore::isRendering`）。

这会和 Punchy! 的第一人称手臂、以及 Punchy! Moves 施加在玩家模型上的身体姿势打架：

- Punchy! 的手被 Real Camera 一起取消，第一人称看不到动画手；
- Real Camera 画出的身体和 Punchy! 的手臂重叠；
- 多出来的身体渲染还会让 Moves 的姿势/渲染状态在同一帧被重复采样，可能放大抖动或穿模。

本补丁把 `RealCameraCore.isRendering()` 在 Punchy 系模组存在时固定为 `false`：

- Real Camera 的**可见身体渲染**被跳过；
- 原版手部取消逻辑随之失效，Punchy! 可以正常画自己的手；
- Real Camera 的**相机绑定仍然工作**（它只依赖 `isActive()`），并且绑定采样时 Moves 的 `LivingEntityRendererMixin` 仍会把动画后的头部姿势应用上去，所以相机继续跟随动画头部，而不是死绑原版头。

## 安装

1. Minecraft **1.21.1** + **NeoForge 21.1.229 或更高**；
2. 安装 **Real Camera 0.7.x**（1.21.1 NeoForge）；
3. 安装 **Punchy! Moves 1.0.x**，可选再装 **Punchy! 3.x**；
4. 把本模组的 `prccompat-1.0.0.jar` 放进 `mods`。

本模组是纯客户端模组，服务器不需要安装。

## 配置

首次启动后生成 `config/prccompat-client.toml`：

```toml
[compat]
    # Hide Real Camera's first-person player model while Punchy! or Punchy! Moves is installed.
    hideRealCameraBody = true
    # Only hide the body when the first-person Punchy! mod is present.
    onlyWhenPunchyInstalled = false
```

- `hideRealCameraBody = false`：恢复 Real Camera 原本的第一人称身体渲染（冲突也会回来，仅用于对照测试）。
- `onlyWhenPunchyInstalled = true`：只在装有 Punchy! 本体时隐藏；只装 Moves 时保留 Real Camera 的身体。

## 验证状态

- `javac --release 21` 全量编译通过，`build/libs/prccompat-1.0.0.jar` 由 `build-manual.sh` 生成。
- Mixin 插件检测已单测：不在类路径上放 Real Camera 时返回 `false`，放上 `realcamera-1.21.1-neoforge-0.7.8-beta.jar` 后返回 `true`。
- 构建机没有图形环境，未启动 Minecraft 客户端做实机验证；首次进游戏建议先看日志里的 `[Punchy x Real Camera]` 行是否出现。

## 兼容边界

- 不改变 Moves 的肩部相机，也不改变 Real Camera 的第一人称相机；两者本来就是不同视角层，不需要“同时驱动同一个相机”。
- 不会把 Moves 的第三人称身体动画带进第一人称；第一人称显示的是 Punchy! / 原版的手。
- 如果 Real Camera 以后改了 `isRendering()` 的签名，Mixin 会失效；构建时会直接报 Mixin 注入失败，不会静默通过。
- 仅针对 Real Camera `0.7.8-beta` + Punchy! Moves `1.0.9` + Punchy! `3.2b` 的 1.21.1 NeoForge 版本开发。

## 从源码构建

需要 JDK 21（NeoForge 1.21.1 要求 Java 21）。

```bash
export JAVA_HOME=/path/to/jdk-21
./gradlew build
```

产物：`build/libs/prccompat-1.0.0.jar`。

本仓库开发时使用了 Gradle 8.14.2 和 ModDevGradle 2.0.148；`neo_version` 在 `gradle.properties` 中，默认 `21.1.267`。

如果所在网络访问不了 `maven.neoforged.net`，Gradle 会在 `createMinecraftArtifacts` 阶段卡住。仓库另外附了不依赖 NeoFormRuntime 的直编脚本：

```bash
export JAVA_HOME=/path/to/jdk-21
./build-manual.sh
```

它只取 NeoForge universal、FancyModLoader、Mixin、ASM、slf4j 这几个编译期依赖，用 `javac --release 21` 直接出 jar；本仓库的 `prccompat-1.0.0.jar` 就是用它构建的。

## 代码结构

```
src/main/java/com/xuanfei/prccompat/
├── PunchyRealCameraCompat.java   # Mod 入口，注册客户端配置
├── ModPresence.java              # 检测 punchy / punchy_moves，缓存结果
├── config/CompatConfig.java      # hideRealCameraBody / onlyWhenPunchyInstalled
└── mixin/
    ├── RealCameraCoreMixin.java  # 关键注入：isRendering() -> false
    └── PrcMixinPlugin.java       # Real Camera 不存在时不应用 Mixin
```

## 设计说明

Real Camera 的 `isRendering()` 同时控制可见身体和原版手部取消，因此不需要分别去改 `MixinLevelRenderer` 和 `MixinItemInHandRenderer`：一个返回值就能同时关掉“重复身体”和“手被吃掉”两个问题，同时保留 `isActive()` 驱动的相机绑定。

没有采用“直接改 Real Camera 配置里的 `renderModel`”的原因：

- 配置值是持久化的，用户切回 Real Camera 单独使用时还得手动改回来；
- 改配置不会影响 `DisableHelper.RENDER_HANDS` 的判定链，手部取消仍然存在；
- Mixin 到 `isRendering()` 是运行时的、可逆的，且能精确表达“只在 Punchy 系模组存在时生效”。

## License

MIT
