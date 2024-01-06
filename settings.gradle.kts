pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "XKCD_Reader"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":androidApp")
include(":shared")
include(":server")
