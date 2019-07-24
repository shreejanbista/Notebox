### Notebox

Installation instructions:

1. Open Android Studio:
2. Click "Check out Project from Version Control" -> "Git"
3. Paste the forked git link.
4. Clone and Open it.

### Errors that might occur when you try to install:

Follow these:
- In Android Studio, Go to **File -> Project Structure -> SDK Location**

In Linux, Open Terminal:
```
$ cd ~/Android/Sdk/platform-tools
$ ./adb uninstall in.cipherhub.notebox
```
On MacOS, Open Terminal:
```
$ cd ~/Library/Android/sdk/platform-tools
$ ./adb uninstall in.cipherhub.notebox
```

After uninstalling, try disconnecting and installing again. If it **fails again**, **repeat the above steps again** and also do below things:

```
$ rm ~/.gradle/caches/
```

Go to Android Studio, you should see lots of errors. Click on Sync with Gradel Files and wait for 5 mins.
