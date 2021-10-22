architectury {
    common()
}

dependencies {
    modImplementation("net.fabricmc", "fabric-loader", "0.12.2")
    modApi("dev.architectury", "architectury", "2.5.32")
}

val dev by configurations.registering

artifacts {
    add(dev.name, tasks.jar)
}
