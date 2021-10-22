plugins {
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

val shadowCommon by configurations.registering

architectury {
    platformSetupLoomIde()
    fabric()
}

dependencies {
    modImplementation("net.fabricmc", "fabric-loader", "0.12.2")
    modApi("net.fabricmc.fabric-api", "fabric-api", "0.41.0+1.17")
    modApi("dev.architectury", "architectury-fabric", "2.5.32")

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
