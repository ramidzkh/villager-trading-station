plugins {
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

val shadowCommon by configurations.registering

architectury {
    platformSetupLoomIde()
    forge()
}

dependencies {
    forge("net.minecraftforge", "forge", "1.17.1-37.0.103")
    modApi("dev.architectury", "architectury-forge", "2.5.32")

    implementation(project(":common", "dev")) {
        isTransitive = false
    }

    "developmentForge"(project(":common", "dev")) {
        isTransitive = false
    }

    "shadowCommon"(project(":common", "transformProductionForge")) {
        isTransitive = false
    }
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("META-INF/mods.toml") {
            expand("version" to project.version)
        }
    }

    shadowJar {
        exclude("fabric.mod.json")

        configurations = listOf(shadowCommon.get())
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        input.set(shadowJar.get().archiveFile)
        dependsOn(shadowJar)
        archiveClassifier.set("forge")
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
