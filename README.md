<blockquote>
  <details>
    <summary>
      <code>选择语言</code>
    </summary>
    <br/>
    <a href="中文readme的url">中文</a>
    <br/>
    <a href="英文readme的url">English</a>
    <br/>
    <a href="日語readme的url">日本語</a>
  </details>
</blockquote>

# Pixiv-Downloader-Lib
## 简介
该库旨在提供一个接口，用于下载**下载[Pixiv](https://www.pixiv.net)艺术作品**。  
您可以轻松地调用该集成库，将特定的艺术作品下载到**本地文件夹**，以供AI训练等用途。
## 安装库
1. 下载 [jar](https://github.com/Ita-Ya/Pixiv/releases/download/lib/pixiv-1.0-SNAPSHOT.jar) 包。
2. 在项目依赖中添加 jar
- ### Maven
   ```xml
   <dependency>
       <groupId>org.kyaru</groupId>
       <artifactId>pixiv</artifactId>
       <version>1.0.0</version>
       <scope>system</scope>
       <systemPath>${project.basedir}/libs/library.jar</systemPath>
   </dependency>
   ```
- ### Gradle
   ```groovy
   dependencies {
   implementation files('pixiv-1.0-SNAPSHOT.jar')
   }
   ```

## 下载前配置
`Pixiv pixiv = new Pixiv("预设配置文件路径")`  
###  [任务配置接口(必须配置)]()
在开始下载作品前，你必须提供**作品储存路径** 与 **有效Cookie**  
> 请通过`pixiv.taskConfig()`来访问任务配置接口

| 方法                                        |  描述                   |
|-------------------------------------------|-----------------------|
| `.setNormalArtworkPath(String filePath)`  | 你下载的全年龄内容将出现在该文件夹里    |
| `.setNormalArtworkPath(String filePath)`  | 你下载的成人内容将出现在该文件夹里     |
| `.setCookie(String cookie)`               | 设置可用于登陆pixiv的cookie   |
| `.setProxy(String host, int port)`        | 设置网络代理(如需要)           |
| `.confirm()`                              | 保存你的任务配置，并以配置文件路径形式返回 |

### [配置过滤器（可选配置)]()
你可以通过配置过滤器来设置作品标准，只有符合标准的作品会被下载至本地。
> 请通过 `pixiv.filterConfig().` 来访问过滤器配置接口
>
| 方法                                        | 描述                                                                                                                                                                        |
|-------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `.setExpectBookmarks(int count)`          | 跳过收藏数小于 count 的作品                                                                                                                                                         |
| `.setExpectLikes(int count)`              | 跳过点赞数小于 count 的作品                                                                                                                                                         |
| `.setExpectViews(int count)`              | 跳过浏览数小于count 的作品                                                                                                                                                          |
| `.setDateAfter(String date)`              | 跳过在 date 之前发布的作品，date 以 yyyy-MM-dd 格式表示                                                                                                                                   |
| `.setDateBefore(String date)`             | 跳过在 date 之后发布的作品，date 以 yyyy-MM-dd 格式表示                                                                                                                                   |
| `.setWhitelistTag(String tags...)`        | 设置白名单标签，跳过不含有 tags 的作品                                                                                                                                                    |
| `.setBlacklistTag(String tags...)`        | 设置黑名单标签，跳过含有 tags 的作品                                                                                                                                                     |
| `.setExpectLabel(Tag tag, Format format)` | 设置年龄限制（Tag）和格式（Format）过滤器，Tag 的选项包括：<br/>-`NOR`（全年龄）<br/>-`R18`（成人向）<br/>-`BOTH`（全部）<br/>Format 则包括：<br/>-`IMG`（静态图片）<br/>-`GIF`（动态图片）<br/>-`BOTH`（全部）<br/>当作品的其中一项不满足则跳过 |
| `.confirm()`                              | 保存你的过滤器配置，并以配置文件路径形式返回                                                                                                                                                    |

### 下载
完成配置后，您便可以开始下载作品了。  
> 请通过 `pixiv.download().` 来访问下载接口，调用方法后立即开始下载。
> 
| 功能               | 方法                                                |
|------------------|---------------------------------------------------|
| 根据作者ID或名字下载艺术作品  | `.byAuthor(int artworkCount, String author)`      |
| 根据艺术作品下载相似艺术作品   | `.byArtwork(int artworkCount, String artworkID)`  |
| 根据当日排行榜下载艺术作品    | `.byRanking(int artworkCount)`                    |
| 根据已关注作者下载艺术作品    | `.byAuthor(int artworkCount)`                     |
| 自定义下载目标          | `.byCustom(List<String> idList, String fileName)` |

## Example
```java
import org.kyaru.pixiv.service.Pixiv;
import org.kyaru.pixiv.service.body.process.TaskID;

public class Test {
    public static void main(String[] args) {
        //通过文件路径加载或创建预设配置文件
        Pixiv pixiv = new Pixiv("path");

        //设置任务配置，并设为默认配置
        pixiv.taskConfig()
                .setDefault(cookie) 
                .setR18ArtworkPath(path) //设置成人内容的储存路径
                .setNormalArtworkPath(path) //设置全年龄内容的储存路径
                .setProxy("127.0.0.1", proxyPort) //设置代理（可选）
                .confirm();
        
        //设置过滤器，仅符合过滤器标准的图片才会被下载
        pixiv.filterConfig()
                .setExpectViews(5000) //设置最低浏览量
                .setExpectLikes(5000) //设置最低点赞量
                .setExpectBookmarks(5000) //设置最低收藏量
                .setDateBefore("yyyy-MM-dd") //设置最迟发布时间
                .setDateAfter("yyyy-MM-dd")  //设置最早发布时间
                .setWhitelistTag("", "") //设置白名单标签
                .setBlacklistTag("", "") //设置黑名单标签
                .setExpectLabel(TaskID.Label.Tag.R18, TaskID.Label.Format.GIF) //设置仅下载成人动图
                .confirm();
        
        int limit = 100;//需要下载艺术作品集数量
        //选择下载方式，可选择多个
        pixiv.download()
                .byArtwork("作品ID", limit) //通过作品 ID 下载相似的作品
                .byAuthor("作者ID", limit)  //通过作者 ID 或用户名，下载该作者的最新作品
                .byFollowing(limit) //下载已关注作者的最新作品
                .byRanking(limit)   //下载当日排行榜的作品
                .byCustom(List.of("artworkID..."), fileName); //自定义
    }
}
```
## Q&A
___
### 1. 如何查看cookie？
> - **step 1** 使用浏览器登陆pixiv并进入主页面  
> - **step 2** 在上方网址输入框输入`javascript:alert(document.cookie)` (由于javascript:会被自动清除，请务必手动输入)  
> - **step 3** 全选方框的内容并复制，将其并传入pixiv的setCookie方法中

### 2. Pixiv库的配置是如何工作的？文件需要提前配置吗？
> 1. Pixiv类的构造参数(Path settingFilePath)的目的在于预设配置文件位置，这意味着您无需提前配置好文件，甚至不需要提前创建好文件。  
   创建与配置工作将分别在构造方法与filterConfig(), taskConfig()方法中完成，并储存于您预设的配置文件中。 
> 2. 若您需要修改已存在的配置文件，则需要将配置文件的路径作为构造参数传入并创建Pixiv对象，并在该对象的filterConfig()与taskConfig()方法中修改你的配置。  
> 3. 若您需要加载并使用已有配置，请将预设配置文件的路径传入Pixiv的构造方法中，并调用Pixiv的download()方法。