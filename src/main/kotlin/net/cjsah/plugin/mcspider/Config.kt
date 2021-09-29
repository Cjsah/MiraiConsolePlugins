package net.cjsah.plugin.mcspider

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.cjsah.console.Util
import net.cjsah.console.plugin.Plugin
import java.io.File

object Config {
    private lateinit var file: File
    private val default = JsonObject()

    fun init(plugin: Plugin) {
        file = File(plugin.pluginDir, "config.json")
        default.add("group", JsonArray())
        val version = JsonObject()
        version.addProperty("release", "")
        version.addProperty("snapshot", "")
        default.add("version", version)

        if (!file.exists()) file.writeText(Util.GSON.toJson(default))
    }

    fun getConfig(): JsonObject {
        return Util.GSON.fromJson(file.readText(), JsonObject::class.java)
    }

    fun change(title : String, json: JsonElement) {
        val config = getConfig()
        config.add(title, json)
        file.writeText(Util.GSON.toJson(config))
    }

}