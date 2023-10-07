plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.serialization)

}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    targetHierarchy.default()

    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    jvm()
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                //put your multiplatform dependencies here
                implementation(libs.kotlinx.coroutines)
                implementation(libs.coroutines.extensions)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.cio)
                implementation(libs.ktor.client.resources)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }


        val jvmMain by getting {
            dependsOn(commonMain)

            dependencies {
                implementation(libs.sqlite.driver)
            }
        }
    }
}

android {
    namespace = "com.colibrez.xkcdreader"
    compileSdk = 34
    defaultConfig {
        minSdk = 29
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("com.colibrez.xkcdreader.data")
        }
    }
}
