# 花密 FlowerPassword

> 一个基于 FlowerPassword 算法的 Android 密码管理器

## 简介

花密（FlowerPassword）是一款帮助您只需记忆一个密码，就能为不同网站生成不同密码的工具。基于 HMAC-MD5 算法，相同输入永远得到相同的输出，无需联网即可生成密码。

## 功能特性

- **密码生成** - 基于记忆密码和区分代号生成 16 位安全密码
- **历史记录** - 本地保存使用过的区分代号，快速找回
- **一键复制** - 点击生成的密码即可复制到剪贴板
- **Material Design 3** - 采用现代化 Material You 设计风格
- **完全离线** - 所有计算在本地完成，无需网络请求
- **数据安全** - 使用 Room 数据库和 DataStore 安全存储

## 截图

<div align="center">
  <img src="screenshot.jpg" alt="主界面" width="280"/>
</div>

## 使用方法

1. 输入你的**记忆密码** - 这是你唯一需要记住的密码
2. 输入**区分代号** - 如网站的名称（google、facebook 等）
3. 自动生成 16 位密码，点击即可复制

## 技术栈

- **Kotlin** - 100% Kotlin 编写
- **Jetpack Compose** - 现代化 UI 框架
- **Material 3** - Material Design 设计语言
- **Room** - 本地数据库存储历史记录
- **DataStore** - 安全的偏好设置存储
- **MVVM 架构** - ViewModel + State 管理界面状态

## 系统要求

- Android 7.0 (API 24) 或更高版本

## 构建

```bash
# 克隆仓库
git clone https://github.com/lzs2000131/FlowerPassword-Android.git
cd FlowerPassword-Android

# 构建 APK
./gradlew assembleDebug

# 安装到设备
./gradlew installDebug
```

## 算法说明

FlowerPassword 算法基于 HMAC-MD5：

1. 使用区分代号作为密钥，对记忆密码进行 HMAC-MD5 运算
2. 分别用固定字符串 `snow` 和 `kise` 对第一步结果进行 HMAC-MD5
3. 根据第三步结果规则化第二步结果（部分字符转大写）
4. 取前 16 位，若首位为数字则替换为 `K`

算法开源透明，可在 `PasswordGenerator.kt` 中查看完整实现。

## 隐私说明

- 所有密码生成在本地设备完成
- 不收集任何用户数据
- 不需要网络权限
- 历史记录仅存储区分代号，不存储生成的密码

## 致谢

本项目移植自 [FlowerPassword](https://github.com/kenmick/FlowerPassword) 小程序版本。

## 许可证

[MIT License](LICENSE)
