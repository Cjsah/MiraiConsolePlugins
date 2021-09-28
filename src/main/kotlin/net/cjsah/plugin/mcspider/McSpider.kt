package net.cjsah.plugin.mcspider

import com.google.common.collect.Lists
import com.google.gson.JsonArray
import com.google.gson.JsonObject
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
import net.cjsah.console.command.source.GroupCommandSource
import net.cjsah.console.plugin.Plugin
import net.cjsah.plugin.mcspider.GetBugs.getBugs
import net.cjsah.plugin.mcspider.GetServer.getServer
import net.cjsah.plugin.mcspider.GetVersion.getVersion
import java.io.File

@Suppress("unused")
class McSpider : Plugin() {

    lateinit var config: JsonObject

    override fun onPluginLoad() {
        config = getJsonConfig("config.json") {
            it.add("group", JsonArray())
            val json = JsonObject()
            json.addProperty("release", "")
            json.addProperty("snapshot", "")
            it.add("version", json)
        }.asJsonObject

    }

    @DelicateCoroutinesApi
    override fun onBotStarted() {
        CommandManager.register { dispatcher ->
            dispatcher.register(CommandManager.literal("mcv").requires{ source ->
                val groups = jsonArray2Long(config["group"].asJsonArray)
                source is GroupCommandSource && groups.contains(source.source.id)
            }.executes("获取最新mc版本") { context ->
                context.source.sendFeedBack(getVersion(Console.getBot()))
                Command.SUCCESSFUL
            })
        }

        CommandManager.register { dispatcher ->
            dispatcher.register(CommandManager.literal("server").then(CommandManager.argument("address", StringArgument.string()).requires{ source ->
                val groups = jsonArray2Long(config["group"].asJsonArray)
                source is GroupCommandSource && groups.contains(source.source.id)
            }.executes("获取某服务器的信息") { context ->
                context.source.sendFeedBack(getServer(StringArgument.getString(context, "address")))
                Command.SUCCESSFUL
            }))
        }

        CommandManager.register { dispatcher ->
            dispatcher.register(CommandManager.literal("mc").then(CommandManager.argument("value", IntArgument.integer(0)).requires{ source ->
                val groups = jsonArray2Long(config["group"].asJsonArray)
                source is GroupCommandSource && groups.contains(source.source.id)
            }.executes("获取 mc bugs") { context ->
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

    private fun jsonArray2Long(array: JsonArray): Collection<Long> {
        val list = Lists.newArrayList<Long>()
        array.forEach { json -> list.add(json.asLong) }
        return list
    }

    fun saveConfig() {
        File(pluginDir, "config.json").writeText(Util.GSON.toJson(config))
    }

}
