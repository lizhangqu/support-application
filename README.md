### support-application

support-application is a library which can get the information about the application like applicationContext, appName, versionName, versionCode, isDebugAble without context.


#### latest_version

current latest_version is [ ![Download](https://api.bintray.com/packages/lizhangqu/maven/support-application/images/download.svg) ](https://bintray.com/lizhangqu/maven/support-application/_latestVersion)


#### gradle compile

```
dependencies {
    compile "com.android.support:support-application:${latest_version}"
}
```

#### maven compile

```
<dependency>
  <groupId>com.android.support</groupId>
  <artifactId>support-application</artifactId>
  <version>${latest_version}</version>
</dependency>
```

#### get application

```
Application application = ApplicationCompat.getApplication();                   
```

#### get application context

```
Context context = ApplicationCompat.getApplicationContext();                   
```

#### get the apk appName

```
String appName = ApplicationCompat.getAppName();
```

#### get the apk versionName

```
String versionName = ApplicationCompat.getVersionName();
```

#### get the apk versionCode

```
int versionCode = ApplicationCompat.getVersionCode();
```

#### judge whether the apk can debug or not

```
boolean isDebugAble = ApplicationCompat.isDebugAble();
```

### License

support-application is under the BSD license. See the [LICENSE](https://github.com/lizhangqu/support-application/blob/master/LICENSE) file for details.