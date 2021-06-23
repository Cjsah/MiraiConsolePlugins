plugins {
    kotlin("jvm") version "1.4.31"
}

group = "net.cjsah.bot.plugin.recall"
version = "1.0"

repositories {
    maven { url = uri("https://jitpack.io") }
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    compileOnly("com.github.Cjsah:MiraiBotConsole:1.9")
    compileOnly("net.mamoe", "mirai-core-api", "2.6.4")
    implementation("com.github.salomonbrys.kotson:kotson:2.5.0")
    implementation("org.hydev:HyLogger:2.1.0.378")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

tasks.withType<Jar> {
    manifest {
        attributes(Pair("Plugin-Class", "net.cjsah.bot.plugin.recall.Main"))
    }
}
