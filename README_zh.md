# Applied Web Terminal

[English](README.md) | 简体中文

把你的AE终端搬到网页上！

<!-- TOC -->
* [Applied Web Terminal](#applied-web-terminal)
  * [依赖](#依赖)
  * [使用](#使用)
  * [图片](#图片)
    * [存储页面](#存储页面)
    * [合成状态页面](#合成状态页面)
    * [游戏内GUI](#游戏内gui)
  * [配置](#配置)
<!-- TOC -->

## 依赖

- [AE2](https://modrinth.com/mod/ae2)
- [Kotlin For Forge](https://modrinth.com/mod/kotlin-for-forge)
- [Configuration](https://modrinth.com/mod/configuration)

## 使用

- 在[Releases](https://github.com/ZhuRuoLing/AppliedWebTerminal/releases)下载本模组的最新版本 并安装依赖
- 若阁下在游玩单人世界
    - 进入您的单人世界
    - 运行 `/appwebterminal resources render` 命令以生成前端资源（仅需运行一次）
- 若阁下在游玩服务器
    - 新建一个单人世界
    - 运行 `/appwebterminal resources render` 命令以生成前端资源（仅需运行一次）
    - 将游戏根目录下的 `aeKeyResources` 文件夹上传至您的服务端
- 将 `ME 网络终端` 接入AE网络
- 右键打开gui，并配置名字及密码
- 打开网页浏览器，访问网络终端地址（默认端口`11451`），并输入密码登录
- 大功告成！

## 图片
<details>
<summary>
图片
</summary>

### 存储页面

<img src="/images/storage.png" style="width: 250px" alt="Storage Page">

### 合成状态页面
<img src="/images/crafting.png" style="width: 250px" alt="Crafting Page">

### 游戏内GUI
<img src="/images/ui.png" style="width: 250px" alt="In-Game ui">

</details>

## 配置

配置文件位于`.minecraft/config/appwebterminal.yaml`

<details>
<summary>
详细配置
</summary>

> ME Web Terminal 的 Http服务器端口

`httpPort`: `11451`
- - -
> 前端网页标题

`frontendTitle`: `Applied Web Terminal`
- - -
> 前端连接的 `Websocket url`
> 
> 填入 `~` 则前端自动判断（使用和网页相同的host）
> 
> 示例：`ws://example.com/`

`backendWebsocketEndpoint`: `~`
- - -
> 使用拼音搜索的语言

`needPinInLanguage`:

&emsp;&emsp;`- zh_cn`

&emsp;&emsp;`- zh_tw`

&emsp;&emsp;`- zh_hk`
- - -
</details>
