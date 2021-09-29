package net.cjsah.plugin.mcspider

import com.google.gson.JsonObject
import kotlinx.coroutines.runBlocking
import net.cjsah.console.Util
import net.cjsah.console.plugin.Plugin
import net.cjsah.plugin.mcspider.Config.getConfig
import net.mamoe.mirai.Bot
import java.net.URL

object GetVersion {

    fun getVersion(bot: Bot): String {
        val config = getConfig()
        try {
            // 获取最新版本
            val json = getJson()
            val release = json["release"].asString
            val snapshot = json["snapshot"].asString
            // 判断是否最新版本
            if (release != config["version"].asJsonObject["release"].asString || snapshot != config["version"].asJsonObject["snapshot"].asString) {
                val newVersion = if (release != config["version"].asJsonObject["release"].asString) release else snapshot
                Config.change("version", json)
                runBlocking {
                    config["group"].asJsonArray.forEach { bot.getGroup(it.asLong)?.sendMessage("发现新版本:${newVersion}") }
                }
            }
            return "当前最新正式版本: $release\n当前最新快照版本: $snapshot"
        }catch (e: Exception) {
            e.printStackTrace()
            return "获取新版本出现错误"
        }
    }

    private fun getJson() : JsonObject {
        val json = Util.GSON.fromJson(URL("https://launchermeta.mojang.com/mc/game/version_manifest.json").readText(), JsonObject::class.java)
        return json["latest"].asJsonObject
    }
}
