### Support-Application

support-application is a library which can get the information about the app like application, applicationContext, classloader, appName, versionName, versionCode, isDebugAble without context.


### Changelog

See details in [CHANGELOG](https://github.com/lizhangqu/support-application/blob/master/CHANGELOG.md).

### Examples

I have provided a sample.

See sample [here on Github](https://github.com/lizhangqu/support-application/tree/master/app).

To run the sample application, simply clone this repository and use android studio to compile, install it on a connected device.

### Usage

**Latest Version**

[ ![Download](https://api.bintray.com/packages/lizhangqu/maven/support-application/images/download.svg) ](https://bintray.com/lizhangqu/maven/support-application/_latestVersion)


**Gradle**

```
dependencies {
    //noinspection GradleCompatible
    compile "com.android.support:support-application:${latest_version}"
}
```

**Maven**

```
<dependencies>
    <dependency>
      <groupId>com.android.support</groupId>
      <artifactId>support-application</artifactId>
      <version>${latest_version}</version>
    </dependency>
</dependencies>
```

**Get Application**

```
Application application = ApplicationCompat.getApplication();                   
```

**Get Application Context**

```
Context context = ApplicationCompat.getApplicationContext();                   
```

**Get Classloader**

```
ClassLoader classLoader = ApplicationCompat.getClassLoader();              
```

**Get The Apk AppName**

```
String appName = ApplicationCompat.getAppName();
```

**Get The Apk VersionName**

```
String versionName = ApplicationCompat.getVersionName();
```

**Get The Apk VersionCode**

```
int versionCode = ApplicationCompat.getVersionCode();
```

**DebugAble Or Not**

```
boolean isDebugAble = ApplicationCompat.isDebugAble();
```

### License

support-application is under the BSD license. See the [LICENSE](https://github.com/lizhangqu/support-application/blob/master/LICENSE) file for details.