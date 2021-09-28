package net.cjsah.plugin.mcspider

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.cjsah.console.Console
import net.cjsah.console.command.CommandManager
import net.cjsah.console.plugin.Plugin
import net.cjsah.plugin.mcspider.GetVersion.getVersion
import net.cjsah.plugin.mcspider.Config.getConfig
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import java.io.File

@Suppress("unused")
class McSpider : Plugin() {

//    override suspend fun onPluginStart() {
//        // 初始化配置目录
//        Config.main = this
//        Config.file = File(pluginDir, "config.json")
//
//        // 协程
//        GlobalScope.launch(Dispatchers.Unconfined) {
//            delay(1)
//            while (true) {
//                getVersion(bot)
//                delay(60000)
//            }
//        }
//        // 指令
//        GlobalEventChannel.subscribeAlways<GroupMessageEvent> {
//            val groupList = ArrayList<Long>()
//            getConfig()["group"].asJsonArray.forEach { groupList.add(it.asLong) }
//            if (group.id in groupList) {
//                val msg = message.contentToString()
//                if (msg.startsWith("/")) Command(group, msg.substring(1, msg.length), bot).run()
//            }
//        }
//    }

    override fun onPluginLoad() {
        // 初始化配置目录
        Config.main = this
        Config.file = File(pluginDir, "config.json")
    }

    @DelicateCoroutinesApi
    override fun onBotStarted() {
        CommandManager.register { dispatcher ->
            dispatcher.register(CommandManager.literal("mcv"))
        }



        GlobalScope.launch(Dispatchers.Unconfined) {
            delay(1)
            while (true) {
                getVersion(Console.getBot())
                delay(60000)
            }
        }
    }

    override fun onBotStopped() {
        TODO("Not yet implemented")
    }

    override fun onPluginUnload() {
        TODO("Not yet implemented")
    }

}
