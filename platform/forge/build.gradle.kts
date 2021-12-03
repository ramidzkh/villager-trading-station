plugins {
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

architectury {
    platformSetupLoomIde()
    forge()
}

val common by configurations.registering
val shadowCommon by configurations.registering

configurations {
    compileClasspath {
        extendsFrom(common.get())
    }

    runtimeClasspath {
        extendsFrom(common.get())
    }

    getByName("developmentForge").extendsFrom(common.get())
}

dependencies {
    forge("net.minecraftforge", "forge", "1.18-38.0.12")
    modApi("dev.architectury", "architectury-forge", "3.1.45")

    "common"(project(path = ":common", configuration = "dev")) {
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
        archiveClassifier.set(null as String?)
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

components.getByName<AdhocComponentWithVariants>("java") {
    withVariantsFromConfiguration(project.configurations.shadowRuntimeElements.get()) {
        skip()
    }
}
