### support-application

support-application is a library which can get the information about the application like applicationContext, appName, versionName, versionCode, isDebugAble without context.

#### latest_version

current latest_version is **0.0.1**

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