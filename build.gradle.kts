plugins {
    id("com.android.application") version "8.3.1" apply false // 升级Gradle插件
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false // 升级Kotlin（支持JVM 21）
    id("com.google.devtools.ksp") version "1.9.20-1.0.14" apply false // KSP匹配Kotlin版本
}