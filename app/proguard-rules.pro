# ProGuard rules for Material Expressive Gallery

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class com.example.gallery.**Hilt* { *; }

# Keep Room entities and DAOs
-keep class com.example.gallery.data.local.** { *; }
-keep class com.example.gallery.domain.model.** { *; }

# Keep Kotlin serialization
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}

# Keep Coil
-keep class coil3.** { *; }

# Keep ML Kit
-keep class com.google.mlkit.** { *; }

# Keep TensorFlow Lite
-keep class org.tensorflow.lite.** { *; }

# Keep ExoPlayer
-keep class com.google.android.exoplayer2.** { *; }

# Keep WorkManager
-keep class androidx.work.** { *; }

# Keep Paging
-keep class androidx.paging.** { *; }

# Keep Biometric
-keep class androidx.biometric.** { *; }

# Keep OkHttp
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

# Keep Gson
-keep class com.google.gson.** { *; }

# Keep Accompanist
-keep class com.google.accompanist.** { *; }

# Keep Coroutines
-keep class kotlinx.coroutines.** { *; }

# Keep Material3
-keep class androidx.compose.material3.** { *; }

# Keep Navigation
-keep class androidx.navigation.** { *; }

# Keep Lifecycle
-keep class androidx.lifecycle.** { *; }

# Keep Activity/Fragment
-keep class androidx.activity.** { *; }
-keep class androidx.fragment.** { *; }

# Keep Compose
-keep class androidx.compose.** { *; }

# Keep ViewBinding
-keep class com.example.gallery.databinding.** { *; }

# Optimize for Android Go - remove unused resources
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Keep native libraries
-keep class com.example.gallery.** { *; }

# Don't warn about missing classes
-dontwarn com.google.mlkit.**
-dontwarn org.tensorflow.lite.**
-dontwarn com.google.android.exoplayer2.**