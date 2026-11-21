# Changelog

## 1.0.0 — 2026-11-21

- 首个版本，面向 Minecraft 1.21.1 / NeoForge 21.1.229+。
- 在 Punchy! / Punchy! Moves 存在时，将 Real Camera 的 `RealCameraCore.isRendering()` 固定为 `false`：
  - 跳过 Real Camera 的第一人称可见身体渲染；
  - 恢复被 Real Camera 取消的原版/Punchy! 第一人称手部渲染；
  - 保留 Real Camera 的相机绑定。
- 增加 `hideRealCameraBody` 与 `onlyWhenPunchyInstalled` 两个客户端配置。
- Mixin 配置使用 `IMixinConfigPlugin`，Real Camera 缺席时不应用注入。
