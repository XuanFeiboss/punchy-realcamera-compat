# Changelog

## 1.1.0alpha — 2026-11-21

- 修复 Real Camera 自动绑定失败但可见身体已经成功隐藏的问题。
- 新增宽松纹理匹配：当皮肤纹理 ID 不匹配 Real Camera 的 `minecraft:textures/entity/player/` 或 `minecraft:skins/` 目标时，回退到已配置的绑定目标继续做 UV 匹配，不再直接放弃绑定。
- 新增 `looseTextureMatching` 客户端配置，默认开启。
- 新增 `logBindDiagnostics` 客户端配置，默认开启；首次遇到不匹配的皮肤纹理会记录实际纹理 ID。
- 绑定仍使用 Real Camera 捕获的真实头部顶点，保留 Punchy! Moves 的动画头部姿势。

## 1.0.0 — 2026-11-21

- 首个版本，面向 Minecraft 1.21.1 / NeoForge 21.1.229+。
- 在 Punchy! / Punchy! Moves 存在时，将 Real Camera 的 `RealCameraCore.isRendering()` 固定为 `false`：
  - 跳过 Real Camera 的第一人称可见身体渲染；
  - 恢复被 Real Camera 取消的原版/Punchy! 第一人称手部渲染；
  - 保留 Real Camera 的相机绑定。
- 增加 `hideRealCameraBody` 与 `onlyWhenPunchyInstalled` 两个客户端配置。
- Mixin 配置使用 `IMixinConfigPlugin`，Real Camera 缺席时不应用注入。
