plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.com.google.devtools.ksp)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.molecule)
    alias(libs.plugins.serialization)
}

android {
    namespace = "com.colibrez.xkcdreader.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.colibrez.xkcdreader.android"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
        unitTests.all {
            it.useJUnitPlatform()

        }
    }
}

dependencies {
    implementation(projects.shared)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.ui.util)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.material)
    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.paging3.extensions)
    implementation(libs.coil.compose)
    implementation(libs.androidx.material.icons.extended)

    implementation(libs.compose.destinations.core)
    ksp(libs.compose.destinations.ksp)

    implementation(libs.kotlinx.serialization)

    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.androidx.work.runtime.ktx)


    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.framework.datatest)
    testImplementation(libs.turbine)

    detektPlugins(libs.bundles.detekt.rules)


}

detekt {
    config.setFrom("../config/detekt/detekt.yml")

    // Android config
    ignoredBuildTypes = listOf("release")
    ignoredFlavors = listOf("production")
    ignoredVariants = listOf("productionRelease")

    basePath = projectDir.absolutePath

    autoCorrect = true
}