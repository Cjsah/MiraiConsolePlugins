package net.cjsah.plugin.player

import cc.moecraft.yaml.HyConfig
import com.google.gson.JsonArray
import kotlinx.coroutines.delay
import net.cjsah.console.Util
import net.cjsah.console.plugin.Plugin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.MemberJoinEvent
import net.mamoe.mirai.message.data.At
import java.io.File

class NewPlayer : Plugin() {

    override fun onBotStarted() {
        GlobalEventChannel.subscribeAlways<MemberJoinEvent> {
            val messages = getConfig()[groupId]
            if (messages != null) {
                for ((i,str) in messages.withIndex()) {
                    if (i == 0) {
                        group.sendMessage(At(this.member) + str)
                    } else {
                        group.sendMessage(str)
                    }
                    delay(500)
                }
            }
        }

    }

    private fun getConfig(): Map<Long, List<String>> {
        val map = HashMap<Long, List<String>>()
        val config = Util.getJson(File(pluginDir, "config.json")) { JsonArray() }
        config.asJsonArray.forEach { json -> map[json.asJsonObject["id"].asLong] = json.asJsonObject["message"].asJsonArray.map { it.asString } }
        return map
    }

    override fun onPluginLoad() {
    }

    override fun onBotStopped() {
    }

    override fun onPluginUnload() {
    }
}

