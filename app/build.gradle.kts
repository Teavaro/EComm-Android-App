plugins {
    id(BuildSystem.plugins.androidApplication)
    id(BuildSystem.plugins.kotlinAndroid)
    id("com.google.gms.google-services")
    id("kotlin-kapt")
    id("org.jetbrains.kotlin.android")
}

apply {
    from("$rootDir/tasks.gradle.kts")
}

android {

    namespace = "com.teavaro.ecommDemoApp"

    defaultConfig {
        applicationId = "com.teavaro.ecommDemoApp"
        minSdk = BuildSystem.versions.minSdk
        targetSdk = BuildSystem.versions.targetSdk
        compileSdk = BuildSystem.versions.compileSdk
        versionCode = BuildSystem.build.versionCode
        versionName = BuildSystem.build.versionName
    }

    buildTypes {
        getByName(BuildSystem.buildConfig.fieldName.debugBuildType) {
            isMinifyEnabled = false
            isShrinkResources = false
            isDefault = true
        }
        getByName(BuildSystem.buildConfig.fieldName.releaseBuildType) {
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    viewBinding {
        enable = true
    }

    buildFeatures {
        buildConfig = true
    }

    packaging {
        resources.excludes.addAll(listOf(
            "META-INF/DEPENDENCIES",
            "META-INF/LICENSE-notice.md",
            "META-INF/LICENSE.md",
            "META-INF/LICENSE",
            "META-INF/LICENSE.txt",
            "META-INF/license.txt",
            "META-INF/NOTICE",
            "META-INF/NOTICE.txt",
            "META-INF/notice.txt",
            "META-INF/ASL2.0",
            "META-INF/project.properties",
            "META-INF/MANIFEST.MF"))
    }

    applicationVariants.all {
        outputs.forEach { output ->
            if (output is com.android.build.gradle.internal.api.BaseVariantOutputImpl) {
                val apkName = "Ecomm DemoApp"
                val extension = output.outputFile.extension
                output.outputFileName = "${apkName}-v${versionName}(${versionCode})-${name}.${extension}"
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    //
    implementation(BuildSystem.libraries.material)
    implementation(BuildSystem.libraries.appCompat)
    implementation(BuildSystem.libraries.constraintLayout)
    implementation(BuildSystem.libraries.viewBinding)
    implementation(BuildSystem.libraries.onetrustSDK)
    implementation(BuildSystem.libraries.lifecycleCommon)
    /*
     * Added this library to solve this issue with Android API 32 and above :
     * java.lang.IllegalArgumentException: com.teavaro.funnelConnectSDK.androidSample.debug:
     * Targeting S+ (version 31 and above) requires that one of FLAG_IMMUTABLE or FLAG_MUTABLE
     * be specified when creating a PendingIntent.
     * Strongly consider using FLAG_IMMUTABLE, only use FLAG_MUTABLE if some functionality
     * depends on the PendingIntent being mutable, e.g. if it needs to be used with inline replies or bubbles.
     * Ref: https://stackoverflow.com/a/70870040/6927433
     */
    implementation(BuildSystem.libraries.androidXWorkRuntime)
    // Swrve
    implementation(BuildSystem.libraries.swrve)
    implementation(BuildSystem.libraries.swrveGeo)
    implementation(BuildSystem.libraries.googleServices)
    // FunnelConnect and UTIQ
    implementation("com.github.Teavaro.FunnelConnect-Mobile-SDK:funnelConnect:0.1.44") {
        exclude("com.github.Teavaro.FunnelConnect-Mobile-SDK", "core-android")
    }
    implementation("com.github.Utiq-tech.UTIQ-Mobile-SDK:utiq:0.1.59")
    val lifecycleVersion = "2.8.5"
    val navigationVersion = "2.8.0"
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.navigation:navigation-fragment-ktx:$navigationVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navigationVersion")
    implementation ("com.google.code.gson:gson:2.10.1")

    // Hyperion debugger
    //debugImplementation(BuildSystem.libraries.hyperionCore)
    //debugImplementation(BuildSystem.libraries.hyperionSharedPreferences)
    //releaseImplementation(BuildSystem.libraries.hyperionNoop)
    testImplementation("junit:junit:4.13.2")
    //Room
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
   }