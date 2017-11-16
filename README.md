# APK-HTML-Tool

This tool check whether it is a cordova application

## Usage
Run this jar file together with apktool.jar in the same directory.
```
java -jar checkCordova.jar <target.apk>
```

apktool will decompile the apk, and search for cordova.js file to determine whether it is a cordova application. If it is a cordova application, it would search for html files and determine whether they are encrypted or in incorrrect format.