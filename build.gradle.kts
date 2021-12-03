import juuxel.loomquiltflower.api.QuiltflowerExtension
import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("dev.architectury.loom") version "0.10.0-SNAPSHOT" apply false
    id("io.github.juuxel.loom-quiltflower") version "1.4.0" apply false
}

architectury {
    minecraft = "1.18"
}

allprojects {
    group = "me.ramidzkh"
    version = "1.0.0-SNAPSHOT"
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "dev.architectury.loom")
    apply(plugin = "maven-publish")
    // apply(plugin = "io.github.juuxel.loom-quiltflower")

    val loom = extensions.getByType(LoomGradleExtensionAPI::class)
    loom.silentMojangMappingsLicense()

    dependencies {
        "minecraft"("net.minecraft", "minecraft", "1.18")
        "mappings"(loom.officialMojangMappings())
    }

    extensions.getByType(JavaPluginExtension::class).apply {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17

        withSourcesJar()
    }

    // extensions.getByType(QuiltflowerExtension::class).apply {
    //     quiltflowerVersion.set("1.6.1")
    // }

    tasks {
        withType<JavaCompile> {
            options.encoding = "UTF-8"
            options.release.set(17)
        }
    }

    extensions.getByType(PublishingExtension::class).apply {
        repositories {
            mavenLocal()

            maven {
                val releasesRepoUrl = uri("${rootProject.buildDir}/repos/releases")
                val snapshotsRepoUrl = uri("${rootProject.buildDir}/repos/snapshots")
                name = "Project"
                url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            }
        }

        publications {
            create<MavenPublication>("maven${project.name}") {
                artifactId = rootProject.name + "-" + project.name
                from(components["java"])
            }
        }
    }
}

allprojects {
    apply(plugin = "architectury-plugin")
}
