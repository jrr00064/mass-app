# ProGuard rules for MASS app

# Keep Nothing SDK
-keep class com.nothing.ketchum.** { *; }
-dontwarn com.nothing.ketchum.**

# Keep coroutines
-keepnames class kotlinx.coroutines.**

# Keep Mass classes
-keep class com.nothing.mass.** { *; }
