## Support-Application

 - support-application is a library which can get the information about the app like application, applicationContext, classloader, appName, versionName, versionCode, isDebugAble without context.
 - support-application is a library which can open the dest activity like url.
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

### Dependency

**latest Version**

[ ![Download](https://api.bintray.com/packages/lizhangqu/maven/support-application/images/download.svg) ](https://bintray.com/lizhangqu/maven/support-application/_latestVersion)


**gradle**

```
dependencies {
    //noinspection GradleCompatible
    compile "com.android.support:support-application:${latest_version}"
}
```

**maven**

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

**get application**

```
Application application = ApplicationCompat.getApplication();                   
```

**get application context**

```
Context context = ApplicationCompat.getApplicationContext();                   
```

**get classloader**

```
ClassLoader classLoader = ApplicationCompat.getClassLoader();              
```

**get the apk appName**

```
String appName = ApplicationCompat.getAppName();
```

**get the apk versionName**

```
String versionName = ApplicationCompat.getVersionName();
```

**Get The Apk VersionCode**

```
int versionCode = ApplicationCompat.getVersionCode();
```

**debuggable or not**

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

### LifecycleCompat

 - get the activity lifecycle changed callback(onCreated/onStarted/onStopped/onDestroyed)
 - get the application lifecycle changed broadcast(foreground/background)
 - get the activity list count changed broadcast
 - get the top activity in activity stack
 - get launchedActivity count
 - get launchedActivity list
 - show dest activity in activity stack
 
**init in application method named onCreate**

```
LifecycleCompat.getInstance().onApplicationCreate(this);
```

**get the top activity, launchedActivity count, launchedActivity list**

```
int launchedActivityCount = LifecycleCompat.getInstance().getLaunchedActivityCount();
Activity topActivity = LifecycleCompat.getInstance().getTopActivity();
List<WeakReference<Activity>> launchedActivityList = LifecycleCompat.getInstance().getLaunchedActivityList();

Log.e("TAG", "launchedActivityCount:" + launchedActivityCount);
Log.e("TAG", "topActivity:" + topActivity);

if (launchedActivityList != null) {
    for (WeakReference<Activity> activityWeakReference : launchedActivityList) {
        if (activityWeakReference != null && activityWeakReference.get() != null) {
            Log.e("TAG", "launchedActivity:" + activityWeakReference.get());
        }
    }
}
```

**show dest activity in activity stack**

```
LifecycleCompat.getInstance().showActivity(MainActivity.class);
```

or use the class name

```
LifecycleCompat.getInstance().showActivity("com.android.support.application.sample.MainActivity");
```


**register/unregister activity lifecycle changed callback**

```
LifecycleCompat.LifecycleCallback lifecycleCallback = new LifecycleCompat.LifecycleCallback() {
    @Override
    public void onCreated(Activity activity) {
        Log.e("TAG", "onCreated:" + activity);
    }

    @Override
    public void onDestroyed(Activity activity) {
        Log.e("TAG", "onDestroyed:" + activity);
    }

    @Override
    public void onStarted(Activity activity) {
        Log.e("TAG", "onStarted:" + activity);
    }

    @Override
    public void onStopped(Activity activity) {
        Log.e("TAG", "onStopped:" + activity);
    }
};
LifecycleCompat.getInstance().registerActivityLifecycleCallback(lifecycleCallback);
```

if you need to unregister it, you need to call the method named unregisterActivityLifecycleCallback.

```
LifecycleCompat.getInstance().unregisterActivityLifecycleCallback(lifecycleCallback);
```

**get the application lifecycle changed broadcast**

```
private class ApplicationLifecycleChangedBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isBackground = intent.getBooleanExtra(LifecycleCompat.EXTRA_LIFECYCLE_STATUS, false);
        String packageName = intent.getStringExtra(LifecycleCompat.EXTRA_PACKAGE_NAME);
        //judge packageName equal
        if (TextUtils.equals(packageName, getApplicationContext().getPackageName())) {
            if (isBackground) {
                Log.e("TAG", "app is background");
            } else {
                Log.e("TAG", "app is foreground");
            }
        }
    }
}
private ApplicationLifecycleChangedBroadcastReceiver mApplicationLifecycleChangedBroadcastReceiver = new ApplicationLifecycleChangedBroadcastReceiver();
  
//register broadcast receiver
registerReceiver(mApplicationLifecycleChangedBroadcastReceiver, new IntentFilter(LifecycleCompat.ACTION_APPLICATION_LIFECYCLE_CHANGED));
   
//unregister broadcast receiver
unregisterReceiver(mApplicationLifecycleChangedBroadcastReceiver);
```

**get the activity list count changed broadcast**

```
private class ActivityCountChangeBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String packageName = intent.getStringExtra(LifecycleCompat.EXTRA_PACKAGE_NAME);
        int activityCount = intent.getIntExtra(LifecycleCompat.EXTRA_ACTIVITY_COUNT, -1);
        //judge packageName equal
        if (TextUtils.equals(packageName, getApplicationContext().getPackageName())) {
            Log.e("TAG", "activityCountChanged:" + activityCount);
        }
    }
}

private ActivityCountChangeBroadcastReceiver mActivityCountChangeBroadcastReceiver = new ActivityCountChangeBroadcastReceiver();

//register broadcast receiver
registerReceiver(mActivityCountChangeBroadcastReceiver, new IntentFilter(LifecycleCompat.ACTION_ACTIVITY_COUNT_CHANGED));

//unregister broadcast receiver
unregisterReceiver(mActivityCountChangeBroadcastReceiver);
```

## License

support-application is under the BSD license. See the [LICENSE](https://github.com/lizhangqu/support-application/blob/master/LICENSE) file for details.