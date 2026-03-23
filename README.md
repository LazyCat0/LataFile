# LataFile
## What is this?
**LataFile is part of LazyCat portfolio, that was make for portfolio and using inside LazyCat projects, but also and you can us it!**

## How to add to project?
### Maven
### Gradle
### Gradle (kts)

## How to use
**Here example for Java (for example in C# check repos on this GitHub)**
```java
 try {
        lataFile.setValue("meta", "version", "1.0-snapshot");
        lataFile.setValue("example section", "example key", "example value");
        lataFile.saveToFile(file);
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