# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# ============================================================================
# GENERAL ANDROID RULES
# ============================================================================

# Preserve line number information for debugging stack traces
-keepattributes SourceFile,LineNumberTable

# Hide the original source file name in stack traces
-renamesourcefileattribute SourceFile

# Keep annotations
-keepattributes *Annotation*

# Keep signatures for generic types (needed for Gson, Retrofit)
-keepattributes Signature

# Keep exception information
-keepattributes Exceptions

# ============================================================================
# APPLICATION DATA MODELS
# ============================================================================

# Keep all data model classes used for serialization/deserialization
-keep class hr.algebra.moviedb.model.** { *; }
-keep class hr.algebra.moviedb.api.MovieItem { *; }
-keep class hr.algebra.moviedb.api.TmdbResponse { *; }
-keep class hr.algebra.moviedb.api.Record { *; }

# Keep Serializable classes
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ============================================================================
# ANDROID COMPONENTS
# ============================================================================

# Keep ContentProvider
-keep class hr.algebra.moviedb.MovieProvider { *; }

# Keep BroadcastReceivers
-keep class hr.algebra.moviedb.MovieReciever { *; }
-keep class hr.algebra.moviedb.receiver.RefreshAlarmReceiver { *; }

# Keep WorkManager Worker classes
-keep class hr.algebra.moviedb.api.MovieWorker { *; }

# Keep Activities
-keep class hr.algebra.moviedb.SplashScreenActivity { *; }
-keep class hr.algebra.moviedb.HostActivity { *; }
-keep class hr.algebra.moviedb.MovieDetailActivity { *; }

# ============================================================================
# RETROFIT / OKHTTP
# ============================================================================

# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Keep Retrofit interfaces
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and). If you are using R8 full mode, you must keep interfaces.
-keep interface hr.algebra.moviedb.api.MovieApi { *; }

# OkHttp platform used only on JVM and when Conscrypt and other security providers are available.
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# ============================================================================
# GSON
# ============================================================================

# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class hr.algebra.moviedb.api.** { <fields>; }

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ============================================================================
# PICASSO
# ============================================================================

-dontwarn com.squareup.okhttp.**
-dontwarn okio.**

# ============================================================================
# ANDROIDX / JETPACK
# ============================================================================

# Preferences
-keep class androidx.preference.** { *; }

# Navigation
-keep class androidx.navigation.** { *; }

# WorkManager
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context,androidx.work.WorkerParameters);
}

# ViewBinding
-keep class * implements androidx.viewbinding.ViewBinding {
    public static ** inflate(android.view.LayoutInflater);
    public static ** inflate(android.view.LayoutInflater, android.view.ViewGroup, boolean);
}

# ============================================================================
# KOTLIN
# ============================================================================

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# ============================================================================
# MISCELLANEOUS
# ============================================================================

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
}

# Keep R classes
-keepclassmembers class **.R$* {
    public static <fields>;
}

# Keep BuildConfig
-keep class hr.algebra.moviedb.BuildConfig { *; }
