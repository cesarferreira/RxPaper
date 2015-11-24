# RxPaper

RxPaper is a [RxJava](https://github.com/ReactiveX/RxJava) wrapper for the cool [Paper library](https://github.com/pilgr/Paper), a [fast](#benchmark-results) NoSQL data storage for Android that lets you save/restore Java objects by using efficient Kryo serialization and handling data structure changes automatically.

![Paper icon](https://raw.githubusercontent.com/pilgr/Paper/master/paper_icon.png)

#### Add dependency
```groovy
compile 'com.cesarferreira.rxpaper:rxpaper:0.1.0'
```


#### Save
Save data object. **Your custom classes must have no-arg constructor.**
Paper creates separate data file for each key.

```java
RxPaper.with(ctx)
        .write(key, value)
        .subscribeOn(Schedulers.io())               // optional
        .observeOn(AndroidSchedulers.mainThread())  // optional
        .subscribe(success -> /* all good */ );

```

#### Read
Read data objects. Paper instantiates exactly the classes which has been used in saved data. The limited backward and forward compatibility is supported. See [Handle data class changes](#handle-data-structure-changes).

Use default values if object doesn't exist in the storage.

```java

RxPaper.with(ctx)
        .read(key, defaultPersonValue)
        .subscribeOn(Schedulers.io())               // optional
        .observeOn(AndroidSchedulers.mainThread())  // optional
        .subscribe(person -> /* all good */ );

```


#### Delete
Delete data for one key.

```java

RxPaper.with(ctx)
        .delete("countries")
        .subscribe();
```

Completely destroys Paper storage.

```java
RxPaper.with(ctx)
        .destroy()
        .subscribe();
```

<!--
#### Use custom book
You can create custom Book with separate storage using

```java
Paper.book("custom-book")...;
```

Any changes in one book doesn't affect to others books.
-->
#### Handle data structure changes
Class fields which has been removed will be ignored on restore and new fields will have their default values. For example, if you have following data class saved in Paper storage:

```java
class Person {
    public String firstName; // Cesar
    public String middleName; // Costa
}
```

And then you realized you need to change the class like:

```java
class Person {
    public String firstName; // Cesar
    // public String middleName; removed field, who cares about middle names
    public String lastName; // New field
}
```

Then on restore the _middleName_ field will be ignored and new _lastName_ field will have its default value _null_.

#### Exclude fields
Use _transient_ keyword for fields which you want to exclude from saving process.

```java
public transient String tempId = "default"; // Won't be saved
```
#### Proguard config
* Keep data classes:

```
-keep class my.package.data.model.** { *; }
```

alternatively you can implement _Serializable_ in all your data classes and keep all of them using:

```
-keep class * implements java.io.Serializable { *; }
```

* Keep library classes and its dependencies

```
-keep class io.paperdb.** { *; }
-keep class com.esotericsoftware.** { *; }
-dontwarn com.esotericsoftware.**
-keep class de.javakaffee.kryoserializers.** { *; }
-dontwarn de.javakaffee.kryoserializers.**
```

#### How it works
Paper is based on the following assumptions:
- Saved data on mobile are relatively small;
- Random file access on flash storage is very fast.

So each data object is saved in separate file and write/read operations write/read whole file.

The [Kryo](https://github.com/EsotericSoftware/kryo) is used for object graph serialization and to provide data compatibility support.

#### Benchmark results
Running [Benchmark](https://github.com/pilgr/Paper/blob/master/paperdb/src/androidTest/java/io/paperdb/benchmark/Benchmark.java) on Nexus 4, in ms:

| Benchmark                 | Paper    | [Hawk](https://github.com/orhanobut/hawk) | [sqlite](http://developer.android.com/reference/android/database/sqlite/package-summary.html) |
|---------------------------|----------|----------|----------|
| Read/write 500 contacts   | 187      | 447      |          |
| Write 500 contacts        | 108      | 221      |          |
| Read 500 contacts         | 79       | 155      |          |