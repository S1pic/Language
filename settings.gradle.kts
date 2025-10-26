pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "Language"
include(":app")

apply(from = "capacitor.settings.gradle")
