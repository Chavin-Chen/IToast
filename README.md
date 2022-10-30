# What is this?

This is a compat library for android toast.

As you know android.widget.toast is dependence on OS NotificationManager.

After Android KitKat(API-19), users can close app Notification Permission, then android toast cannot show any message~

I use a Dialog on the TopActivity to show message which looks like toast.

## How to Use It?

First, you should add dependencies in your build.gradle

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'io.github.chavin-chen:util-toast:1.0.0'
}
```

Second, use `IToast` to show toast , just like use android.widget.Toast, eg:

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ChvToast.makeText(this, "Hello World", ChvToast.DURATION.SHORT).show()
    }
}
```

And also there is a no-repeat toast util which has more easily API.

But you should init the util first. for example in YourApplication#onCreate

```kotlin
class YourApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        ChvToastUtil.init(this, ChvToast.STRATEGY.ANDROID_FIRST) {
            // TODO: Here you need return the Top Activity
            return@init null
        }
    }
}
```

There are three arguments in `IToast.init(context, strategy, provider)`:

1. context, used for get application resource, you can pass a application context
2. strategy, tell IToast use custom toast first or android toast first
3. provider, when android toast can't show (IToast can know it in runtime), IToast need a top activity to show dialog

Of course, you can write a better util based on IToast, so IToast give you another way to init. And the arguments
of `IToast.setup(strategy, provider)` is the same as `IToast.init()`

 ```kotlin
// you can choose a time to setup
ChvToast.setup(ChvToast.STRATEGY.ANDROID_FIRST) {
    // TODO: Here you need return the Top Activity
    return@setup null
}
```

If you use IToastUtil to show toast, just like this:

```kotlin
ChvToastUtil.showShort("Hello I am Chavin :)")
```


## LICENSE

```plain

Copyright (c) 2018-present, IToast Contributors.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

```

the first version 25/08/2018 in guangzhou.
the latest version 10/30/2022 in changsha.



