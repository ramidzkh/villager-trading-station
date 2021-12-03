plugins {
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

architectury {
    platformSetupLoomIde()
    fabric()
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

    getByName("developmentFabric").extendsFrom(common.get())
}

dependencies {
    modImplementation("net.fabricmc", "fabric-loader", "0.12.8")
    modApi("net.fabricmc.fabric-api", "fabric-api", "0.43.1+1.18")
    modApi("dev.architectury", "architectury-fabric", "3.1.45")

    "common"(project(path = ":common", configuration = "namedElements")) {
        isTransitive = false
    }

    "shadowCommon"(project(path = ":common", configuration = "transformProductionFabric")) {
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
