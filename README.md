# LataFile [![](https://jitpack.io/v/LazyCat0/LataFile.svg)](https://jitpack.io/#LazyCat0/LataFile)
## What is this?
**LataFile is part of LazyCat portfolio, that was make for portfolio and using inside LazyCat projects, but also and you can us it!**

## How to add to project?
### Maven
* Repository
```xml
<repositories>
	<repository>
	    <id>jitpack.io</id>
	    <url>https://jitpack.io</url>
	</repository>
</repositories>
```
* Depend
```xml
<dependency>
    <groupId>com.github.LazyCat0.LataFile</groupId> <!--->LataScript WIP. I do not recommend to use LataScript cause it a little rawwwww<!-->
    <artifactId>LataCore</artifactId>
    <version>1.1-snapshot</version>
</dependency>
```
### Gradle
* Repository
```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}
```
* Depend
```groovy
dependencies {
    implementation 'com.github.LazyCat0.LataFile:LataFile:1.1-snapshot' // LataScript WIP. I do not recommend to use LataScript cause it a little rawwwww
}
```
### Gradle (kts)
* Repository
```kotlin
repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}
```
* Depend
```kotlin
dependencies {
    implementation("com.github.LazyCat0.LataFile:LataFile:1.1-snapshot") // LataScript WIP. I do not recommend to use LataScript cause it a little rawwwww
}
```

## How to use
**Here example for Java (for example in C# check repos on this GitHub)**
```java
 try {
        lataFile.setValue("meta", "version", "1.0-snapshot");
        lataFile.setValue("example section", "example key", "example value");
        lataFile.saveToFile(file);
        Logger.getLogger("bzbzbzbz").info(LataFile.get("example section", "example key").toString());
    } catch (IOException e) {
        e.printStackTrace();
    }
```
**This code will create file with name "Lata example.lata" inside your project and add NECESSARILY `[meta]` section and `[example section]` with example values.** 
```lata
[meta]
version = "1.0-snapshot"

[example section]
example key = "example value"
```