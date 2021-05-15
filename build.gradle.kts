plugins {
    kotlin("jvm") version "1.4.31"
}

group = "net.cjsah.bot.plugin.player"
version = "4.2"

repositories {
    maven { url = uri("https://jitpack.io") }
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    compileOnly("com.github.Cjsah:MiraiBotConsole:1.7")
    compileOnly("net.mamoe", "mirai-core-api", "2.6.4")
    compileOnly("com.github.HyDevelop:HyConfigLib:3.1.52")
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
        attributes(Pair("Plugin-Class", "net.cjsah.bot.plugin.player.NewPlayer"))
    }
}