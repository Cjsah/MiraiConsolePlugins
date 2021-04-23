package net.cjsah.bot.plugin.private

import net.cjsah.bot.console.Plugin
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.message.data.content
import java.lang.Exception
import java.lang.NullPointerException

class PrivateMessage : Plugin(
    "PrivateMessage",
    "4.1",
    false,
    listOf("Cjsah")
) {
    override suspend fun onPluginStart() {
        GlobalEventChannel.subscribeAlways<FriendMessageEvent> {
            val send = message.content
            if (friend.id == 2684117397L && send.startsWith("/")) {
                util(friend, send.substring(1, send.length))
            }
        }
    }


    private suspend fun util(friend: Friend, content: String) {
        var args = content.split(" ", limit = 2)
        when (args[0]) {
            "send" -> {
                args = args[1].split(" ", limit = 3)
                when (args[0]) {
                    "group" -> {
                        try {
                            bot.getGroup(args[1].toLong())!!.sendMessage(args[2])
                        }catch (e: NullPointerException) {
                            friend.sendMessage("机器人没有加入此群")
                        }catch (e: Exception) {
                            friend.sendMessage("发送失败")
                        }
                    }
                    "friend" -> {
                        try {
                            bot.getFriend(args[1].toLong())!!.sendMessage(args[2])
                        }catch (e: NullPointerException) {
                            e.printStackTrace()
                            friend.sendMessage("此机器人没有此好友")
                        }catch (e: Exception) {
                            friend.sendMessage("发送失败")
                        }
                    }
                }
            }
        }
    }
}

