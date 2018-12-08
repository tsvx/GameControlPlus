# The Encoding problem of the GCP

The problem is that GCP (Game Control Devices) library contains a copy of the [jinput](https://github.com/jinput/jinput) library,
which is itself is a bit broken.

If you look at C-files in `jinput\plugins\windows\src\main\native\dx8`, you see invocations of `JNIEnv.NewStringUTF`,
which take an `TCHAR` arrays as the input, like `tszInstanceName`, `tszProductName` and `tszName`
(see the description of the corresponding structures `DIDEVICEINSTANCE`, `DIEFFECTINFO`, `DIDEVICEOBJECTINSTANCE`).
I believe these inputs to be just CHAR arrays as that code is unlikely compiled with Unicode support
(see `build.xml` one level up).
So it is in *CP1251* encoding for the russian locale which is an one-byte encoding.

But [`JNIEnv.NewStringUTF`](https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/functions.html#NewStringUTF)
takes *the pointer to a modified UTF-8 string* as the input. That mismatches with what the input is.
So it seems that that input is treated as *CP1252* by `JNIEnv` methods.

I don't know how to fix that in the C-files, so I decided to fix java wrappers to reencode those strings from *CP1252* to *CP1251*,
luckily there are those wrappers available, see commits.
 