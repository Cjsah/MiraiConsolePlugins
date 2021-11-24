package net.cjsah.plugin.http

import com.google.gson.JsonObject
import net.cjsah.console.plugin.Plugin
import java.io.File

object Config {
    private lateinit var PLUGIN_DIR: File
    private val CONFIG = JsonObject()

    fun init(plugin: Plugin) {
        PLUGIN_DIR = plugin.pluginDir
        CONFIG.addProperty("port", 8080)
    }

}