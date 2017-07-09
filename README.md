## Support-Application

 - support-application is a library which can get the information about the app like application, applicationContext, classloader, appName, versionName, versionCode, isDebugAble without context.
 - support-application is a library which can open the activity like url.
 - support-application is a library which can control the app develop environment globally.
 - support-application is a library which can get the application lifecycle(foreground/background), and the activity lifecycle(onCreated/onStarted/onStopped/onDestroyed), the top activity in activity stack etc.
 - each class in support-application is independent. so you need init each of them in application method named onCreate if needed.
 
## Changelog

See details in [CHANGELOG](https://github.com/lizhangqu/support-application/blob/master/CHANGELOG.md).

## Examples

I have provided a sample.

See sample [here on Github](https://github.com/lizhangqu/support-application/tree/master/app).

To run the sample application, simply clone this repository and use android studio to compile, install it on a connected device.

## Usage

### dependency

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

### ApplicationCompat

Get the information about the app like application, applicationContext, classloader, appName, versionName, versionCode, isDebugAble without context.

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

### RouteCompat

Route the Activity Like open the Url.

**declare url information at intent-filter node in AndroidManifest.xml**

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

**open the activity in code like this**

```
RouteUri routeUri = RouteUri.scheme("https")
                        .host("support.android.com")
                        .path("support/application/second")
                        .param("key1", "value1")
                        .fragment("1");
RouteCompat.from(MainActivity.this).toUri(routeUri);
```

**get the url information in dest activity**

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


### EnvironmentCompat

Control the app's develop environment globally.

**init in application method named onCreate**

```
EnvironmentCompat.getInstance().onApplicationCreate(this, EnvironmentCompat.Env.RELEASE);
```

or use Env.RELEASE for default

```
 EnvironmentCompat.getInstance().onApplicationCreate(this);
```

**get the env in app anywhere if you need**

```
EnvironmentCompat.Env env = EnvironmentCompat.getInstance().getEnv();
```

**change the env if you need**

```
String[] environments = {
        EnvironmentCompat.Env.RELEASE.name(),
        EnvironmentCompat.Env.PRE.name(),
        EnvironmentCompat.Env.DEVELOP.name()
};
EnvironmentCompat.Env env = EnvironmentCompat.getInstance().getEnv();
int ordinal = env.ordinal();
AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
        .setTitle("Please Select Environment")
        .setCancelable(true)
        .setSingleChoiceItems(environments, ordinal, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EnvironmentCompat.Env env = EnvironmentCompat.Env.values()[which];
                EnvironmentCompat.getInstance().changeEnv(getApplicationContext(), env);
                dialog.dismiss();
            }
        })
        .create();
dialog.show();
```

## License

support-application is under the BSD license. See the [LICENSE](https://github.com/lizhangqu/support-application/blob/master/LICENSE) file for details.