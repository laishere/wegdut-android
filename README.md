# WEGDUT 安卓项目
>WEGDUT是一款校园APP，作者是广东工业大学计科学生。  
>
>[官网 wegdut.com](https://wegdut.com/) 
>
>[联系作者：laishere@163.com](mailto:laishere@163.com)   

***仅供学习使用，请勿用于商业用途***  

### 项目概览
| - | |
|-------|-------|  
|语言 |`Kotlin`|  
|架构 |`MVP`|  
|主要库 |`zxing(二维码生成)` `bugly(崩溃报告)` `coroutines(kotlin协程，处理异步逻辑)` `dagger(依赖注入)` `glide(图片加载)` `palette(图片取色)` `matisse(知乎图片选取库)` `dexter(权限获取)` `retrofit(API实现)` `gson(json处理)` `okhttp(http通信)` `room(安卓数据库框架)` `jsoup(html解析)` `event bus(事件通信)` `阿里云OSS SDK`|
  
### 开发  
> 本项目是Android Studio项目  
- 克隆仓库 （可以直接在AS中克隆）  
- 实现 config/Config(.kt)，Config 应当继承 ConfigInterface，此类为APP配置类  
- 开发运行...  
  
*APP并不能直接运行，需要配套对应的后端才能正常运行，后端暂未开源，测试过程中，可以暂且把 Config.apiBaseUrl 设置为 `https://api.wegdut.com/`，功能会受到限制，比如，不能发送图片动态、评论。*