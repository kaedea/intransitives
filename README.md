# Intransitives
![version](https://jitpack.io/v/com.kaedea/intransitives.svg) 

Gradle plugin `intransitives` helps to make apis of transitive children dependencies invisible from user.

## Problems
When you add an dependency in `build.gradle`, Gradle will also compile transitive children dependencies by default. This behavior makes the dependencies manager much simpler but something we hope to hide the apis of children dependencies. For example, picasso depends on okhttp as the following

  APP -> Picasso -> OkHttp

If we compile picasso, we will also compile okhttp (that is how picasso works). In another word, the apis of okhttp is visible to us though we do not compile it explictly. If we do not announce compling okhttp explictly, we should not access its apis. Because the implementaion of picasso might changes so that it does not compile okhttp any more.

## Solution
If we want to avoid accessing of transitive apis, we should set `transitive = false` of complie while `transitive = true` of runtime (hide the apis in compile time and package the apis in runtime). Such as

```groovy
dependencies {
    implementation ('com.squareup.picasso:picasso:latest.release') {
        transitive = false
    }
    runtimeOnly ('com.squareup.picasso:picasso:latest.release') {
        transitive = false
    }
}
```

And this is how `intransitives` works.

## Getting Started
Just apply the plugin in the `build.gradle` file of your module.

```groovy
// 1. Add dependency
buildscript {
    repositories {
        //...
        maven { url 'https://jitpack.io' }
    }
    dependencies {
        classpath 'com.kaedea:intransitives:latest.release'
    }
}

// 2. Apply plugin
apply plugin: 'com.kaedea.intransitives'
intransitives {
    // 3. Move your dependencies here in need
    implementation 'com.squareup.picasso:picasso:latest.release'
}
```

Use task `:app:intransitives` to dump your intransitive dependencies confinguration.