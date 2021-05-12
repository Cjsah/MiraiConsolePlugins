package net.cjsah.bot.plugin.mcspider

import com.github.salomonbrys.kotson.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.cjsah.bot.console.Plugin
import net.cjsah.bot.plugin.mcspider.GetVersion.getVersion
import net.cjsah.bot.plugin.mcspider.Config.getConfig
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import java.io.File

@Suppress("unused")
class McSpider : Plugin(
    "McSpider",
    "2.1",
    true,
    listOf("Cjsah")
) {

    override suspend fun onPluginStart() {
        // 初始化配置目录
        Config.main = this
        Config.file = File(pluginDir, "config.json")

        // 协程
        GlobalScope.launch(Dispatchers.Unconfined) {
            delay(1)
            while (true) {
                getVersion(bot)
                delay(60000)
            }
        }
        // 指令
        GlobalEventChannel.subscribeAlways<GroupMessageEvent> {
            val groupList = ArrayList<Long>()
            getConfig()["group"].asJsonArray.forEach { groupList.add(it.asLong) }
            if (group.id in groupList) {
                val msg = message.contentToString()
                if (msg.startsWith("/")) Command(group, msg.substring(1, msg.length), bot).run()
            }
        }
    }

}
