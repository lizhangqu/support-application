### Support-Application

 - support-application is a library which can get the information about the app like application, applicationContext, classloader, appName, versionName, versionCode, isDebugAble without context.
 - support-application is a library which can open the activity like url

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

**Debuggable Or Not**

```
boolean isDebuggable = ApplicationCompat.isDebuggable();
```

**Route Activity Like Url**

add intent-filter to AndroidManifest.xml

```
<activity android:name=".SecondActivity">
    <intent-filter android:priority="999">
        <action android:name="android.intent.action.VIEW"/>

        <category android:name="android.intent.category.DEFAULT"/>
        <category android:name="android.intent.category.BROWSABLE"/>

        <data android:scheme="http"/>
        <data android:scheme="https"/>
        <data android:host="support.android.com"/>
        <data android:path="/support/application/second"/>
    </intent-filter>
</activity>
```

open the activity in code

```
RouteUri routeUri = RouteUri.scheme("https")
                        .host("support.android.com")
                        .path("support/application/second")
                        .param("key1", "value1")
                        .fragment("1");
RouteCompat.from(MainActivity.this).toUri(routeUri);
```

get the url information in activity

```
Intent intent = getIntent();
if (intent != null) {
    Uri data = getIntent().getData();
    if (data != null) {
        String host = data.getHost();
        String path = data.getPath();
        String param = data.getQueryParameter("key1");
        String fragment = data.getFragment();
        Log.e("TAG", "host:" + host);
        Log.e("TAG", "path:" + path);
        Log.e("TAG", "param:" + param);
        Log.e("TAG", "fragment:" + fragment);
    }
}
```

see more api in [RouteCompat.java](https://github.com/lizhangqu/support-application/blob/master/support-application/src/main/java/com/android/support/application/RouteCompat.java)


### License

support-application is under the BSD license. See the [LICENSE](https://github.com/lizhangqu/support-application/blob/master/LICENSE) file for details.