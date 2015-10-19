# OkBuck
[ ![Download](https://img.shields.io/bintray/v/piasy/maven/OkBuck.svg) ](https://bintray.com/piasy/maven/OkBuck/_latestVersion)
[![Master branch build status](https://travis-ci.org/Piasy/OkBuck.svg?branch=master)](https://travis-ci.org/Piasy/OkBuck)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-OkBuck-green.svg?style=flat)](https://android-arsenal.com/details/1/2593)

**10**行配置从Android Studio + Gradle构建体系迁移到facebook的BUCK构建体系，且保持两者同时兼容使用，编码使用AS，享受安卓最强大IDE的功能，打包、安装、测试用BUCK，享受安卓最快构建系统的畅快淋漓，两者互不干扰。从此妈妈再也不用担心我在编译安卓工程时睡着了，而且真的只要**10**行！

如果在使用的过程中遇到了BUCK或者OkBuck的问题，欢迎加入**OkBuck使用问题交流群**：170102067。

[Wiki](https://github.com/Piasy/OkBuck/wiki)

## 为什么要有OkBuck？
Android Studio + Gradle已经是大部分安卓开发者的开发环境，为了体验BUCK超快的构建过程，从已有的工程进行迁移到BUCK环境是一个工作量较大、较繁琐、而且还不一定会的过程。OkBuck希望提供一个gradle plugin，通过对工程build.gradle简单地配置后，自动完成向BUCK的迁移。

此外，如果日后你的gradle脚本发生了变化（修改了依赖，加入了新的module），你同样可以通过OkBuck来更新你的buck配置，通过OkBuck，你甚至不用编写一行magic的buck脚本！

## OkBuck做了什么？
通过对已有基于gradle构建的安卓工程添加几行配置，OkBuck将自动为你编写BUCK配置文件，引入工程的第三方依赖。如果你已经安装了buck，那么配置完成之后直接`buck install app`就可以构建成功了。当然，前提是你得代码与buck兼容，关于兼容性问题后面将详细说明。

## 如何使用OkBuck
1. 工程根目录build.gradle的buildscript dependencies部分加入：`classpath "com.github.piasy:okbuck-gradle-plugin:${latest version}"`
    
2. 工程根目录build.gradle最外层加入apply语句：`apply plugin: 'com.github.piasy.okbuck-gradle-plugin'`

3. 工程根目录build.gradle最外层加入`okbuck`标签：
    
    ```gradle
    okbuck {
        target "android-23"
        keystore "debug.keystore"
        keystoreProperties "debug.keystore.properties"
        overwrite true
        resPackages = [
            'dummylibrary': 'com.github.piasy.okbuck.example.dummylibrary',
            'app': 'com.github.piasy.okbuck.example',
        ]
    }
    ```

    +  其中`android-23`相当于gradle指定`compileSdkVersion 23`；
    +  ~~`debug.keystore`和`debug.keystore.properties`分别代表的是签名文件和签名配置文件，需要放到application module的根目录下，用于指定签名文件；~~
    +  再也不用在OkBuck里指定签名配置了：
      +  只要你已经在build.gradle中设置了**刚好一个**签名配置
      +  但是你需要配置git，ignore你的签名秘钥和配置，把这一行加入到**工程根目录的.gitignore文件中**：`.okbuck/keystore`
      +  但是如果你在build.gradle中配置了多个签名配置，或者想要把OkBuck生成的签名配置放到另一个目录（**但必须是工程根目录的子目录**），你可以像下面这样配置，其中`keystoreDir`指定OkBuck生成的签名配置的路径（相对于工程根目录，不要前导的`/`哟），`signConfigName`指定多个签名配置中的一个。
        
        ```gradle
            okbuck {
                target "android-23"
                keystoreDir ".okbuck/keystore"
                signConfigName "release"
                overwrite true
                resPackages = [
                    'dummylibrary': 'com.github.piasy.okbuck.example.dummylibrary',
                    'app': 'com.github.piasy.okbuck.example',
                    'common': 'com.github.piasy.okbuck.example.common',
                ]
            }
        ```
        
        +  同样记得配置git，ignore签名配置
        +  完整的例子可以参考本repo的app module，[工程根目录build.gradle](build.gradle), [app module的build.gradle](app/build.gradle)
    +  `overwrite`指定是否覆盖已有的buck配置文件；
    +  `resPackages`用于指定每个Android library module和Android application module的R文件的包名，你需要在resPackages里面为每个module指定包名，将dummylibrary/app替换为你的module的名字，引号里面的内容通常都是对应module的AndroidManifest.xml中的包名。
    
4. 执行`./gradlew okbuck`命令，命令执行完毕后，将在工程目录下生成.buckconfig文件，.okbuck目录，以及每个module根目录下生成一个BUCK文件，此时在工程根目录执行`buck install app`即可开始使用buck构建安装了（假设你的application module叫app），开始体验buck构建的畅快淋漓吧 :)
    +  加入`apply plugin: 'com.github.piasy.okbuck-gradle-plugin'`后，OkBuck将为你的工程生成三个gradle task：`okbuck`，`okbuckDebug` `okbuckRelease` 和 `okbuckClean`
    +  执行`okbuckClean` 将**删除所有**OkBuck生成的文件
    +  `okbuckDebug`和`okbuckRelease`将使你在build.gradle中声明的`debugCompile`和`releaseCompile`依赖可以在buck的构建中正确引用，包括annotation processor哟！
    
5. 关于10行：第一步一行，第二步只有一行，第三步有八行，所以真的只有10行！

## 更多工作
当然上面所说的10行只是配置，如果你的代码和buck不兼容，另外如果之前的依赖声明比较混乱，则可能需要更多的工作 :)

+  处理依赖冲突

    执行`buck install app`的时候，可能遇到 `*** has already been defined` 或者
    
    ```java
    BUILD FAILED: //app:bin#dex_merge failed on step dx with an exception:
    Multiple dex files define***
    com.android.dex.DexException: Multiple dex files define ***
    ```
    
    这说明你遇到了依赖冲突，即多个module依赖了同一个第三方库（版本是否相同无所谓）。目前的解决办法是：把这个被多次依赖的第三方库（例如gson）指定给多个module中的一个（例如module A），其他的module（例如module B和module C）都依赖module A。对于安卓官方的support库同样如此。
    
    这个问题的解决OkBuck将会进行优化，不过对于工程本身来说，移除冲突依赖也是有必要的，即便在运行app的时候不会报错，运行espresso测试的时候也可能会报错。

+  以前本地jar包依赖可能会依赖失败

    现象：以前使用gradle时，moduleA以本地jar包依赖gson，moduleB依赖moduleA，在moduleB中可以正常引用gson，但是使用OkBuck之后，可能moduleB是无法引用gson的。
    
    临时解决方案：moduleA改为以远程方式（maven）依赖gson，使用OkBuck之后，此时moduleB就可以引用gson了。
    
    这个问题OkBuck接下来绝对是要解决的。

+  versionCode, versionName, targetSdkVersion, minSdkVersion的定义，需要放到AndroidManifest.xml文件中，而不是放在build.gradle文件里面，示例：

    ```xml
    <manifest xmlns:android="http://schemas.android.com/apk/res/android"
          package="com.github.piasy.okbuck.example"
          android:versionCode="1"
          android:versionName="1.0"
        >
    
        <uses-sdk
                android:targetSdkVersion="23"
                android:minSdkVersion="15"
                />
                ...
    </manifest>
    ```

+  `R`的引用问题

    buck构建生成的`R`中的定义，都不是final的，所以如果你使用了ButterKnife等这样的库，那这两者将不兼容；临时方案是：把ButterKnife的`@Bind`/`@InjectView`转换为`ButterKnife.findById`，`@OnClick`转换为手动设置`OnClickListener`。
    
    跨module的资源引用可能会遇到问题，例如module A中定义了一个string资源，名为`test_string`，module B依赖了module A，在module B的代码中直接引用`R.string.test_string`将会报编译错误，报找不到引用；可以通过在module B中定义一个相同的资源（但是名字不要一样，原因下面讲），module B的代码引用module B中定义的资源，或者在module B的代码中显式引用module A的R文件中的资源（即在R前面加上module A的包名）。
    
    另外多个module中声明同名的资源可能会引起问题，例如：每个module下都有一个AndroidManifest.xml文件，里面都有`Application`标签且设置了`android:label`属性，那么最终`buck install app`的时候，安装的APP的名字是什么将是未定义的。可以在每个module的AndroidManifest.xml中指定不同名字的string资源，这样将不会有资源与app的string资源冲突。

+  `javax.annotation`依赖，如果依赖了`javax.annotation`，请使用`compile` scope 而不是`provided` scope。

+  buck不能使用java 8编译，所以与retrolambda不兼容，暂时告别lambda了 :(

+  可能还有更多的工作需要进行，或者说更多的坑等着你踩 :) 。不过为了以后每次编译的畅快淋漓，值啊！

+  完整例子可以参考本repo对OkBuck的使用。

+  [更多需要的更改](https://github.com/Piasy/OkBuck/wiki/Known-caveats)

## 已知的“坑”
+  与`ButterKnife`不兼容 (buck)
+  与`RetroLambda`不兼容 (buck)
+  `javax.annotation`依赖请使用`compile` scope 而不是`provided` scope (OkBuck)
+  对`R`的跨module引用会有问题 (buck)，在java代码中跨module引用R的内容，需要显式指明该module的R类的包名，详见上文
+  无法引用design support库的string resource `appbar_scrolling_view_behavior` (buck)，其实是上一条的具体情形，因为资源的定义在design support库里面，跨module引用了，解决方案：
  +  在自己module的string.xml里面定义：`<string name="my_appbar_scrolling_view_behavior" translatable="false">android.support.design.widget.AppBarLayout$ScrollingViewBehavior</string>`，然后在layout中引用
  +  或者直接在layout中把内容硬编码进去：`app:layout_behavior="android.support.design.widget.AppBarLayout$ScrollingViewBehavior"`
+  (buck & OkBuck) 目前想要使BUCK打包出支持debug的（可调试，可查看log）apk并不容易，目前的暴力方法是在AndroidManifest.xml文件中加入`android:debuggable="true"`，OkBuck将尽快解决这个问题
+  (OkBuck) OkBuck支持一个project里面有多个application module，但是它们的签名配置必须一样，即各自build.gradle中signingConfigs标签的内容必须一致，如果保证这一点有问题，我建议你将工程一分为二，公共代码抽离为公用的库，这样也方便以后你有更多的project需要使用公用的代码
+  [更多](https://github.com/Piasy/OkBuck/wiki/Known-caveats)

## Troubleshooting
如果你在使用OkBuck的过程中遇到了什么问题（bug），请[提一个issue](https://github.com/Piasy/OkBuck/issues/new)，另外如果能把`./gradle okbuck --stacktrace`任务执行时的输出内容也提供上，那就是极好的了。

## Contribution
欢迎贡献！具体请见[详细TODO列表](https://github.com/Piasy/OkBuck/wiki/TODO-list)。

## 致谢
+  首先感谢Facebook开源的[buck](https://github.com/facebook/buck)构建系统
+  感谢[promeG](https://github.com/promeG/)在开发过程中的讨论与指导

## 谁用了OkBuck？
User | Repo
--- | ---
[Piasy](https://github.com/Piasy) | [AndroidTDDBootStrap](https://github.com/Piasy/AndroidTDDBootStrap)，[手把手应用教程](http://blog.piasy.com/AndroidTDDBootStrap-Use-OkBuck/)

如果你也在开源项目中使用了OkBuck，可以把repo的链接[发邮件给我](mailto:xz4215@gmail.com)，我会把你的repo加入到这个列表中。

## [Change log](README.md#change-log)