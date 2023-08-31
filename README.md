# Pixiv-Downloader-Lib
___


- ## [简介]()
该库旨在提供一个接口，用于下载**下载[Pixiv](https://www.pixiv.net)艺术作品**。  
您可以轻松地调用该集成库，将特定的艺术作品下载到**本地文件夹**，以供AI训练等用途。
___
- ## [主要功能接口]()
      ### 通过pixiv.download().来访问下载接口
| 功能                | 方法                                                |
|-------------------|---------------------------------------------------|
  | *根据作者ID或名字下载艺术作品* | `.byAuthor(int artworkCount, String author)`      |
| *根据艺术作品下载相似艺术作品*  | `.byArtwork(int artworkCount, String artworkID)`  |
| *根据当日排行榜下载艺术作品*   | `.byRanking(int artworkCount)`                    |
| *根据已关注作者下载艺术作品*   | `.byAuthor(int artworkCount)`                     |
| *自定义下载目标*         | `.byCustom(List<String> idList, String fileName)` |

- ##  [下载前配置]()
---
- ### 任务配置接口(必须配置)

      通过pixiv.taskConfig().来访问配置接口
| 方法                                        | 描述                        |
|-------------------------------------------|---------------------------|
| `.setNormalArtworkPath(String filePath)`  | 你下载的**全年龄内容**将出现在该文件夹里    |
| `.setNormalArtworkPath(String filePath)`  | 你下载的**成人内容**将出现在该文件夹里     |
| `.setCookie(String cookie)`               | 设置**用于登陆pixiv的cookie**    |
| `.setProxy(String host, int port)`        | 设置**网络代理**(如需要)           |
| `.confirm()`                              | **保存**你的任务配置，配置会以文件路径形式返回 |


 - ### 过滤器配置接口 (可选配置)
       通过pixiv.filterConfig().来访问配置接口
| 方法                                        | # 描述                                 |
|-------------------------------------------|--------------------------------------|
| `.setExpectBookmarks(int count)`          | 屏蔽**收藏数**小于count的作品                  |
| `.setExpectLikes(int count)`              | 屏蔽**点赞数**小于count的作品                  |
| `.setExpectViews(int count)`              | 屏蔽**浏览数**小于count的作品                  |
| `.setDateAfter(String yyyy-MM-dd)`        | 屏蔽**发布时间在date之前**的作品                 |
| `.setDateBefore(String yyyy-MM-dd)`       | 屏蔽**发布时间在date之前**的作品                 |
| `.setWhitelistTag(String tags...)`        | 屏蔽**不含有tags**的作品                     |
| `.setBlacklistTag(String tags...)`        | 屏蔽**含有tags**的作品                      |
| `.setExpectLabel(Tag tag, Format format)` | 屏蔽非Tag(NOR, R18)&Format(IMG, GIF)的作品 |
| `.confirm()`                              | **保存**你的过滤器配置，配置会以文件路径形式返回           |

- ## [使用例]()
```java
import org.kyaru.pixiv.service.Pixiv;
import org.kyaru.pixiv.service.body.process.TaskID;

public class Test {
    public static void main(String[] args) {
        //通过文件路径加载或创建预设配置文件
        Pixiv pixiv = new Pixiv("path");

        //设置任务配置，并设为默认配置
        pixiv.taskConfig()
                .setDefault(cookie) //设置成人内容的储存路径
                .setR18ArtworkPath(path) //设置全年龄内容的储存路径
                .setNormalArtworkPath(path) //设置代理（可选）
                .setProxy("127.0.0.1", proxyPort)
                .confirm();
        
        //设置过滤器，仅符合过滤器标准的图片才会被下载
        pixiv.filterConfig()
                .setExpectViews(5000) //设置最低浏览量
                .setExpectLikes(5000) //设置最低点赞量
                .setExpectBookmarks(5000) //设置最低收藏量
                .setDateBefore("yyyy-MM-dd") //设置最晚发布时间
                .setDateAfter("yyyy-MM-dd")  //设置最早发布时间
                .setWhitelistTag("tag1", "tag2") //设置白名单标签
                .setBlacklistTag("tag3", "tag4") //设置黑名单标签
                .setExpectLabel(TaskID.Label.Tag.R18, TaskID.Label.Format.GIF) //设置仅下载成人动图
                .confirm();
        
        int limit = 100;//需要下载艺术作品集数量
        //选择下载方式 可选择多个
        pixiv.download()
                .byArtwork("作品ID", limit) //通过作品 ID 下载相似的作品
                .byAuthor("作者ID", limit)  //通过作者 ID 或名字 下载该作者的最新作品
                .byFollowing(limit) //下载已关注作者的最新作品
                .byRanking(limit)   //下载当日排行榜的作品
                .byCustom(List.of("artworkID..."), fileName); //自定义
    }
}
```
- ## [备注事项]()
### 1. 如何查看cookie？
> - **step 1:** 使用任意浏览器登陆[Pixiv](https://www.pixiv.net)并进入主页面  
> -  **step 2:** 在上方网址输入框输入`javascript:alert(document.cookie)`并确定 (*由于javascript:会被自动清除，请务必手动输入*)  
> -  **step 3:** 弹出的方框内容便是您的有效cookie，全选并复制粘贴至pixiv的setCookie方法中
### 2. Pixiv库的配置是如何工作的？文件需要提前配置吗？
> - Pixiv类的构造参数(Path settingFilePath)的目的在于预设配置文件位置，这意味着您**无需提前配置文件，甚至不需要提前创建文件**。   
> 创建与配置工作将分别在构造方法与filterConfig(), taskConfig()方法中完成，并储存于您预设的配置文件中。  
> - 若您需要修改已存在的配置文件，则需要**将配置文件的路径传入Pixiv的构造方法**中，并在该对象的filterConfig()与taskConfig()方法中修改你的配置。
> - 若您需要加载并使用已有配置，请**将预设配置文件的路径传入Pixiv的构造方法**中，并调用Pixiv的download()方法。

- ## [安装]()
1. 下载[jar](https://github.com/Ita-Ya/Pixiv/releases/download/lib/pixiv-1.0-SNAPSHOT.jar)包。
2. 将 jar 放入项目的 libs 文件夹 (如无则新建文件夹)
3. 在项目依赖中添加 jar  
   Maven
   ```xml
   <dependency>
       <groupId>org.kyaru</groupId>
       <artifactId>pixiv</artifactId>
       <version>1.0.0</version>
       <scope>system</scope>
       <systemPath>${project.basedir}/libs/library.jar</systemPath>
   </dependency>
   ```
   Gradle
   ```groovy
   dependencies {
   implementation files('pixiv-1.0-SNAPSHOT.jar')
   }
   ```
   



