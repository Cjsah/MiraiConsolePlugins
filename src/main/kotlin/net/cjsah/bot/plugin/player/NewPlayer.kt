package net.cjsah.bot.plugin.player

import kotlinx.coroutines.delay
import net.cjsah.bot.console.Plugin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.MemberJoinEvent
import net.mamoe.mirai.message.data.At

@Suppress("unused")
class NewPlayer : Plugin(
    "NewPlayer",
    "4.0",
    true,
    listOf("Cjsah")
) {

    override suspend fun onPluginStart() {
        val config = getYamlConfig("config.yml") { config ->
            run {
                config.set("群号", listOf(123))
                config.set("内容", "文本")
            }
        }

        GlobalEventChannel.subscribeAlways<MemberJoinEvent> {
            if (groupId in config.getLongList("群号")) {
                val message = config.getString("内容").split("[part]")
                for ((i,str) in message.withIndex()) {
                    delay(500)
                    if (i == 0) {
                        group.sendMessage(At(this.member) + str)
                    } else {
                        group.sendMessage(str)
                    }
                }
            }
        }
    }
}

