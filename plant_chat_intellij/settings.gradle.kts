pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "plant_chat_intellij"
include(":plant_chat")
findProject(":plant_chat")?.projectDir = file("../plant_chat")