plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    jvm {}

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.shared)
                implementation(libs.kotlinx.coroutines)
            }
        }
    }
}