package net.cjsah.plugin.mcspider

import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.get
import com.github.salomonbrys.kotson.set
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.cjsah.console.Util
import net.cjsah.plugin.mcspider.Config.getConfig
import net.mamoe.mirai.Bot
import java.net.URL

object GetVersion {

    fun getVersion(bot: Bot): String {
        val config = McSpider.config
        try {
            // 获取最新版本
            val json = getJson()
            val release = json["release"].asString
            val snapshot = json["snapshot"].asString
            // 判断是否最新版本
            if (release != config["version"]["release"].asString || snapshot != config["version"]["snapshot"].asString) {
                val newVersion = if (release != config["version"]["release"].asString) release else snapshot
                config["version"]["release"] = release
                config["version"]["snapshot"] = snapshot
                Config.save("version", json)
                config["group"].asJsonArray.forEach { bot.getGroup(it.asLong)?.sendMessage("发现新版本:${newVersion}") }
            }
            return "当前最新正式版本: $release\n当前最新快照版本: $snapshot"
        }catch (e: Exception) {
            e.printStackTrace()
            return "获取新版本出现错误"
        }
    }

    private fun getJson() : JsonObject {
        val json = Util.GSON.fromJson(URL("http://launchermeta.mojang.com/mc/game/version_manifest.json").readText(), JsonObject::class.java)
        return json["latest"].asJsonObject
    }
}
