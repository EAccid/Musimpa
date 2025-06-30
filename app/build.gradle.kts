import java.io.FileInputStream
import java.util.Properties

plugins {
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.10"
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
}

val string = "String"
val keysPropertiesFile = rootProject.file("keys.properties")
val keysProperties = Properties()
keysProperties.load(FileInputStream(keysPropertiesFile))

android {
    namespace = "com.eaccid.musimpa"
    compileSdk = 35

    buildFeatures {
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
            string,
            "THE_MOVIE_DB_API_READ_ACCESS_TOKEN",
            keysProperties.getProperty("apiReadAccessToken")
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
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    kotlin.sourceSets.forEach {
        it.languageSettings.enableLanguageFeature("DataObjects")
    }
    android {
        testOptions.unitTests.isIncludeAndroidResources = true
    }
}

composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    stabilityConfigurationFiles.addAll(rootProject.layout.projectDirectory.file("stability_config.conf"))
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2025.03.01"))

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:2.8.9")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("androidx.datastore:datastore-preferences:1.1.4")
    implementation("androidx.paging:paging-runtime-ktx:3.3.6")
    implementation("androidx.paging:paging-compose-android:3.3.6")
    implementation("androidx.paging:paging-compose:3.3.6")
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("com.google.accompanist:accompanist-pager:0.28.0")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.28.0")
    implementation(libs.androidx.junit.ktx)
    implementation("androidx.work:work-runtime-ktx:2.10.1")

//tests
//    implementation(libs.androidx.ui.test.junit4.android) todo add to libs.versions.TOML and not only here
    androidTestImplementation(platform("androidx.compose:compose-bom:2025.03.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    testImplementation("io.insert-koin:koin-test-junit4:3.5.2-RC1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    testImplementation("androidx.room:room-testing:2.7.1")
    androidTestImplementation("androidx.room:room-testing:2.7.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("androidx.paging:paging-common:3.3.6")
    androidTestImplementation("androidx.paging:paging-testing:3.3.6")
    testImplementation("com.google.truth:truth:1.1.5")
    testImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.sqlite:sqlite-framework:2.5.0")
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test:rules:1.5.0")

    testImplementation("io.insert-koin:koin-test:3.5.2-RC1")
    testImplementation("io.insert-koin:koin-test-junit4:3.5.2-RC1")

    testImplementation("io.mockk:mockk:1.13.10")


//koin
    implementation("io.insert-koin:koin-core:3.5.2-RC1")
    implementation("io.insert-koin:koin-android:3.5.2-RC1")
    implementation("io.insert-koin:koin-androidx-compose:3.5.2-RC1")
    implementation("io.insert-koin:koin-androidx-workmanager:3.5.3")

//api json
    implementation("com.squareup.moshi:moshi-kotlin:1.12.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")

//coil
    implementation("io.coil-kt:coil-compose:2.6.0")

//youtube
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")

//room
    implementation("androidx.room:room-runtime:2.7.1")
    implementation("androidx.room:room-ktx:2.7.1")

    ksp("androidx.room:room-compiler:2.7.1")
    implementation("androidx.room:room-paging:2.7.1")


}