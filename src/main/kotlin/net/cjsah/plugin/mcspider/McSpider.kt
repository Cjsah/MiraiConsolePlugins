package net.cjsah.plugin.mcspider

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.cjsah.console.Console
import net.cjsah.console.Util
import net.cjsah.console.command.Command
import net.cjsah.console.command.CommandManager
import net.cjsah.console.command.argument.IntArgument
import net.cjsah.console.command.argument.StringArgument
import net.cjsah.console.command.source.CommandSource
import net.cjsah.console.command.source.GroupCommandSource
import net.cjsah.console.plugin.Plugin
import net.cjsah.plugin.mcspider.GetBugs.getBugs
import net.cjsah.plugin.mcspider.GetServer.getServer
import net.cjsah.plugin.mcspider.GetVersion.getVersion

@Suppress("unused")
class McSpider : Plugin() {

    override fun onPluginLoad() {
        Config.init(this)
    }

    @DelicateCoroutinesApi
    override fun onBotStarted() {
        CommandManager.register { dispatcher ->
            dispatcher.register(CommandManager.literal("mcv").requires{ source -> canUse(source) }.executes("获取最新mc版本") { context ->
                context.source.sendFeedBack(getVersion(Console.getBot()))
                Command.SUCCESSFUL
            })
        }

        CommandManager.register { dispatcher ->
            dispatcher.register(CommandManager.literal("server").then(CommandManager.argument("address", StringArgument.string()).requires{ source -> canUse(source) }.executes("获取某服务器的信息") { context ->
                context.source.sendFeedBack(getServer(StringArgument.getString(context, "address")))
                Command.SUCCESSFUL
            }))
        }

        CommandManager.register { dispatcher ->
            dispatcher.register(CommandManager.literal("mc").then(CommandManager.argument("value", IntArgument.integer(0)).requires{ source -> canUse(source) }.executes("获取 mc bugs") { context ->
                context.source.sendFeedBack(getBugs(IntArgument.getInteger(context, "value")))
                Command.SUCCESSFUL
            }))
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
    }

    override fun onPluginUnload() {
    }

    private fun canUse(source: CommandSource<*>): Boolean {
        val groups = Util.jsonArray2LongList(Config.getConfig()["group"].asJsonArray)
        return source is GroupCommandSource && groups.contains(source.source.id)

    }

}
