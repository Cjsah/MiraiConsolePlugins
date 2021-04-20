package net.cjsah.bot.plugin.qq

import kotlinx.coroutines.*
import net.cjsah.bot.console.ConsoleCommand
import net.cjsah.bot.console.Plugin

@Suppress("unused")
class DelAllFriends : Plugin(
    "DelAllFriends",
    "1.0",
    false,
    listOf("Cjsah"),
) {

    override suspend fun onPluginStart() {

        ConsoleCommand.registerCommand("del confirm") {
            logger.log("正在删除好友...")
            runBlocking {
                val deferred = async {
                    bot.friends.forEach {
                        if (it.id != bot.id) {
                            logger.log("<${it.id}> [${it.nick}] 正在删除此好友")
                            it.delete()
                        }
                    }
                }
                deferred.await()
                logger.log("已成功删除所有好友!")
            }
        }
    }
}
