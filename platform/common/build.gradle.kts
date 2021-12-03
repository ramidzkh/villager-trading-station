architectury {
    common()
}

dependencies {
    modImplementation("net.fabricmc", "fabric-loader", "0.12.7")
    modApi("dev.architectury", "architectury", "3.1.45")
}

val dev by configurations.registering

artifacts {
    add(dev.name, tasks.jar)
}
