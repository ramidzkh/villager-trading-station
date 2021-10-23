plugins {
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

val shadowCommon by configurations.registering

architectury {
    platformSetupLoomIde()
    fabric()
}

repositories {
    maven {
        url = uri("https://storage.googleapis.com/devan-maven/")
    }
}

dependencies {
    modImplementation("net.fabricmc", "fabric-loader", "0.12.2")
    modApi("net.fabricmc.fabric-api", "fabric-api", "0.41.0+1.17")
    modApi("dev.architectury", "architectury-fabric", "2.5.32")

    include(modImplementation("io.github.astrarre", "astrarre-gui-v1-fabric", "1.12.6"))
    include("io.github.astrarre", "astrarre-access-v0-fabric", "1.8.2")
    include("io.github.astrarre", "astrarre-util-v0-fabric", "1.2.1")
    include("io.github.astrarre", "astrarre-itemview-v0-fabric", "1.2.2")
    include("io.github.astrarre", "astrarre-hash-v0-fabric", "1.2.2")

    implementation(project(":common", "dev")) {
        isTransitive = false
    }

    "developmentFabric"(project(":common", "dev")) {
        isTransitive = false
    }

    "shadowCommon"(project(":common", "transformProductionFabric")) {
        isTransitive = false
    }
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand("version" to project.version)
        }
    }

    shadowJar {
        configurations = listOf(shadowCommon.get())
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        dependsOn(shadowJar)
        input.set(shadowJar.get().archiveFile)
        archiveClassifier.set("fabric")
    }

    jar {
        archiveClassifier.set("dev")
    }

    sourcesJar {
        val commonSources = project(":common").tasks.sourcesJar
        dependsOn(commonSources)
        from(commonSources.get().archiveFile.map { zipTree(it) })
    }
}
