package net.cjsah.bot.plugin.private

import kotlinx.coroutines.runBlocking
import net.cjsah.console.Console
import net.cjsah.console.command.Command
import net.cjsah.console.command.CommandManager
import net.cjsah.console.command.argument.LongArgument
import net.cjsah.console.command.argument.StringArgument
import net.cjsah.console.plugin.Plugin

class PrivateMessage : Plugin() {

    override fun onPluginLoad() {
    }

    override fun onBotStarted() {
        val bot = Console.getBot()
        CommandManager.register{ dispatcher ->
            dispatcher.register(CommandManager.literal("send")
                .then(CommandManager.literal("group")
                    .then(CommandManager.argument("id", LongArgument.longArg(0))
                        .then(CommandManager.argument("msg", StringArgument.string())
                            .executes("对某群组发送消息") { context ->
                                val group = bot.getGroup(LongArgument.getLong(context, "id"))
                                if (group != null) runBlocking { group.sendMessage(StringArgument.getString(context, "msg")) }
                                else context.source.sendFeedBack("机器人没有加入此群")
                                Command.SUCCESSFUL
                            })))
                .then(CommandManager.literal("user")
                    .then(CommandManager.argument("id", LongArgument.longArg(0))
                        .then(CommandManager.argument("msg", StringArgument.string())
                            .executes("对某好友发送消息") { context ->
                                val friend = bot.getFriend(LongArgument.getLong(context, "id"))
                                if (friend != null) runBlocking { friend.sendMessage(StringArgument.getString(context, "msg")) }
                                else context.source.sendFeedBack("机器人没有此好友")
                                Command.SUCCESSFUL
                            })))
                .then(CommandManager.literal("allGroup")
                    .then(CommandManager.argument("msg", StringArgument.string())
                        .executes("对所有群组发送消息") { context ->
                            bot.groups.forEach { runBlocking { it.sendMessage(StringArgument.getString(context, "msg")) } }
                            Command.SUCCESSFUL
                        }))
                .then(CommandManager.literal("allUser")
                    .then(CommandManager.argument("msg", StringArgument.string())
                        .executes("对所有好友发送消息") { context ->
                            bot.friends.forEach { runBlocking { it.sendMessage(StringArgument.getString(context, "msg")) } }
                            Command.SUCCESSFUL
                        }))
                .then(CommandManager.literal("all")
                    .then(CommandManager.argument("msg", StringArgument.string())
                        .executes("对所有群组和好友发送消息") { context ->
                            val msg = StringArgument.getString(context, "msg")
                            bot.groups.forEach { runBlocking { it.sendMessage(msg) } }
                            bot.friends.forEach { runBlocking { it.sendMessage(msg) } }
                            Command.SUCCESSFUL
                        }
                    )
                )
            )
        }
    }

    override fun onBotStopped() {
    }

    override fun onPluginUnload() {
    }
}

