plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}

buildscript {
    dependencies {
        // Android Gradle Plugin의 버전을 8.3.0으로 설정합니다.
        classpath("com.google.gms:google-services:4.3.15")  // 기존 Google Play Services 플러그인
    }
}
