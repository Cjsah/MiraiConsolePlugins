@file:Suppress("unused")

package net.cjsah.bot.plugin.recall

import com.github.salomonbrys.kotson.get
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import net.cjsah.bot.console.Permission
import net.cjsah.bot.console.Plugin
import net.cjsah.bot.console.Util
import net.cjsah.bot.console.command.CommandManager
import net.cjsah.bot.console.command.arguments.base.StringArgument
import net.cjsah.bot.console.events.CommandRegistration
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.MessageSource.Key.recall
import java.io.File

class Main : Plugin(
    "KeywordRecall",
    "1.0",
    true,
    listOf("Cjsah")
) {
    override suspend fun onPluginStart() {
        // 初始化配置文件
        val config = getJsonConfig("config.json") {
            it.add("group", JsonArray())
            it.add("keywords", JsonArray())
        }

        GlobalEventChannel.subscribeAlways<GroupMessageEvent> {
            val groupList = ArrayList<Long>()
            config["group"].asJsonArray.forEach { groupList.add(it.asLong) }
            if (group.id in groupList) {
                config["keywords"].asJsonArray.forEach {
                    if (message.contentToString().contains(it.asString)) {
                        message.recall()
                        group.sendMessage("消息违规, 已撤回!")
                        return@forEach
                    }
                }
            }
        }

        CommandRegistration.EVENT.register(CommandManager.literal("recall").requires{ source ->
            source.hasPermission(Permission.HELPER)
        }.then(CommandManager.literal("add").then(CommandManager.argument("word", StringArgument.string()).executes{ context ->
            val word = StringArgument.getString(context, "word")
            if (!contains(config["keywords"].asJsonArray, word)) {
                config["keywords"].asJsonArray.add(word)
                save(config)
                context.source.sendFeedBack("成功添加关键字")
            }else context.source.sendFeedBack("已有此关键字, 不需要再次添加")
        })).then(CommandManager.literal("remove").then(CommandManager.argument("word", StringArgument.string()).executes{ context ->
            val word = StringArgument.getString(context, "word")
            config["keywords"].asJsonArray.forEach {
                if (it.asString == word) {
                    config["keywords"].asJsonArray.remove(it)
                    save(config)
                    context.source.sendFeedBack("成功删除关键字")
                    return@executes
                }
            }
            context.source.sendFeedBack("没有此关键字, 不需要删除")
        })).then(CommandManager.literal("list").executes{ context ->
            var str = ""
            config["keywords"].asJsonArray.forEach {
                str += " - ${it.asString}\n"
            }
            context.source.sendFeedBack(str)
        }))
    }

    private fun contains(keywords: JsonArray, keyword: String): Boolean {
        keywords.forEach {
            if (it.asString == keyword) return true
        }
        return false
    }

    private fun save(config: JsonElement) {
        File(pluginDir, "config.json").writeText(Util.GSON.toJson(config))
    }
}