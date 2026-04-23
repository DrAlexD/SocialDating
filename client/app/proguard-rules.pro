# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keepclasseswithmembers class * {
    @dagger.hilt.android.lifecycle.HiltViewModel <init>(...);
}

# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
# R8 full mode: keep Retrofit interfaces (otherwise replaced with null via Proxy)
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>
# R8 full mode: prevent stripping generic signatures from kept interfaces
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation,allowshrinking interface <1>
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Kotlinx Serialization
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class xelagurd.socialdating.client.**$$serializer { *; }
-keepclassmembers class xelagurd.socialdating.client.** {
    *** Companion;
}
-keepclasseswithmembers class xelagurd.socialdating.client.** {
    kotlinx.serialization.KSerializer serializer(...);
}