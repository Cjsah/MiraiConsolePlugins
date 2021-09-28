package net.cjsah.plugin.mcspider

import com.github.salomonbrys.kotson.set
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.cjsah.bot.console.Util
import java.io.File

object Config {
    internal lateinit var main: McSpider
    internal lateinit var file: File

    fun getConfig(): JsonElement {
        return main.getJsonConfig("config.json") {
            it.add("group", JsonArray())
            val json = JsonObject()
            json.addProperty("release", "")
            json.addProperty("snapshot", "")
            it.add("version", json)
        }
    }

    fun save(title : String, json: JsonElement) {
        val config = getConfig()
        config[title] = json
        file.writeText(Util.GSON.toJson(config))
    }
}