rootProject.name = "villager-trading-station"

pluginManagement {
    repositories {
        gradlePluginPortal()

        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }

        maven {
            name = "Architectury"
            url = uri("https://maven.architectury.dev/")
        }

        maven {
            name = "MinecraftForge"
            url = uri("https://maven.minecraftforge.net/")
        }

        maven {
            name = "Cotton"
            url = uri("https://server.bbkr.space/artifactory/libs-release/")
        }
    }
}

include("common", "fabric", "forge")

for (project in rootProject.children) {
    project.projectDir = file("platform/${project.name}")
}
