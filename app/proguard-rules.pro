# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-dontwarn com.samsung.android.sdk.pass.**
-dontwarn au.com.bytecode.opencsv.bean.**
-dontwarn com.google.common.**
-dontwarn org.apache.commons.math3.**

######AuthSDK#####
-keep class com.rsa.mobile.android.authenticationsdk.db.**
-keep class com.rsa.mobile.android.authenticationsdk.db.BioAuthenticationEnrollmentDataSource.**
-keep class com.rsa.mobile.android.authenticationsdk.methods.**
-keep class org.apache.harmony.xnet.provider.jsse.NativeCrypto
#Check how to keep specific class

#####SAMSUNG SDK ######
-keep class com.samsung.** { *; }

####VOICE AGNITIO####
-keep class com.rsa.mobile.android.authenticationsdk.utils.CryptoUtils {*;}
-keep class com.rsa.mobile.android.authenticationsdk.methods.voice.agnitio.ResponseStatus {*;}
-keep class com.rsa.mobile.android.authenticationsdk.methods.voice.agnitio.QualityAndDiagnosis {*;}
-keep class com.rsa.mobile.android.authenticationsdk.methods.voice.agnitio.VerificationResult {*;}

#####Trs Signing#####
-keep class com.rsa.mobile.android.authenticationsdk.trxsgn.** {*;}
-keepnames class * implements android.os.Parcelable { *; }
-keepclassmembers class * implements android.os.Parcelable { *; }

