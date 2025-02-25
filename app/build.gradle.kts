import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
}

val string = "String"
val keysPropertiesFile = rootProject.file("keys.properties")
val keysProperties = Properties()
keysProperties.load(FileInputStream(keysPropertiesFile))

android {
    namespace = "com.eaccid.musimpa"
    compileSdk = 35

    buildFeatures{
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.eaccid.musimpa"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }

    buildTypes.forEach {
        it.buildConfigField(
            string, "THE_MOVIE_DB_API_KEY", keysProperties.getProperty("apiKeyTMDb")
        )
        it.buildConfigField(
            string, "YOUTUBE_API_KEY", keysProperties.getProperty("apiKeyYouTube")
                )
        it.buildConfigField(
            string, "THE_MOVIE_DB_API_READ_ACCESS_TOKEN", keysProperties.getProperty("apiReadAccessToken")
                )
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.10.0")
    implementation(platform("androidx.compose:compose-bom:2025.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    //koin
    implementation("io.insert-koin:koin-core:3.5.2-RC1")
    implementation("io.insert-koin:koin-android:3.5.2-RC1")
    implementation("io.insert-koin:koin-androidx-compose:3.5.2-RC1")
    implementation(platform("androidx.compose:compose-bom:2025.02.00"))

    //api json
    implementation("com.squareup.moshi:moshi-kotlin:1.12.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")

    //coil
    implementation("io.coil-kt:coil-compose:2.6.0")

    //youtube
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2025.02.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    //koin test
    testImplementation("io.insert-koin:koin-test-junit4:3.5.2-RC1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2025.02.00"))

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

}