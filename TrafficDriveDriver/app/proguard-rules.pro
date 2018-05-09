# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\user65\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}


-keep class org.apache.** {*;}
-keep class org.apache.http.** { *; }

-keepattributes SourceFile,LineNumberTable
-keep class com.parse.*{ *; }
-keepclasseswithmembernames class * {
    native <methods>;
}


-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.examples.android.model.** { *; }
-keepattributes Signature

-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

-keepattributes *Annotation*,EnclosingMethod

-keep public class mydatapackage.** {
  public void set*(***);
  public *** get*();
}

-keeppackagenames org.jsoup.nodes

-keep class org.jivesoftware.smack.initializer.VmArgInitializer { public *; }
-keep class org.jivesoftware.smack.ReconnectionManager { public *; }
-keep class com.quickblox.module.c.a.c { public *; }
-keep class com.quickblox.module.chat.QBChatService { public *; }
-keep class com.quickblox.module.chat.QBChatService.loginWithUser { public *; }
-keep class com.quickblox.module.chat.listeners.SessionCallback { public *; }
-keep class * extends org.jivesoftware.smack { public *; }
-keep class org.jivesoftware.smack.** { public *; }
-keep class org.jivesoftware.smackx.** { public *; }
-keep class com.quickblox.** { public *; }
-keep class * extends org.jivesoftware.smack { public *; }
-keep class * implements org.jivesoftware.smack.debugger.SmackDebugger { public *; }

-keep class com.daimajia.easing.** { *; }
-keep interface com.daimajia.easing.** { *; }
-keep class com.google.protobuf.** { *; }

-keep class com.nineoldandroids.animation.** { *; }
-keep interface com.nineoldandroids.animation.** { *; }
-keep class com.nineoldandroids.view.** { *; }
-keep interface com.nineoldandroids.view.** { *; }
-keepclassmembers class com.google.android.gms.maps.model.Marker { *; }
-keeppackagenames com.github.orangegangsters.lollipin



-dontwarn org.apache.http.**
-dontwarn android.net.**
-dontwarn com.parse.**
-dontwarn com.squareup.picasso.**
-dontwarn okio.**
-dontwarn org.**
-dontwarn com.squareup.**
-dontwarn com.nineoldandroids.animation.**

-dontwarn com.github.orangegangsters.lollipin

-dontwarn android.arch.util.paging.CountedDataSource
-dontwarn android.arch.persistence.room.paging.LimitOffsetDataSource


##---------------Begin: proguard configuration common for all Android apps ----------
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose
-dump class_files.txt
-printseeds seeds.txt
-printusage unused.txt
-printmapping mapping.txt
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
