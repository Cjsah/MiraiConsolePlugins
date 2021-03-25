package net.cjsah.bot.plugin.mcspider

import net.cjsah.bot.plugin.mcspider.GetServer.getServer
import net.cjsah.bot.plugin.mcspider.GetVersion.getVersion
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Group

object Command {

    suspend fun commandUtil(group: Group, msg: String, bot: Bot) {
        val help = "/help(?)       显示帮助\n" +
                "/mcv       获取最新游戏版本\n" +
                "/server [ip]   获取某ip的服务器信息"

        if (msg == "?" || msg == "help") {
            group.sendMessage(help)
        }else if (msg == "mcv") {
            group.sendMessage(getVersion(bot))
        }else if (msg.startsWith("server")) {
            val list = msg.split(" ")
            if (list.size == 2 && list[0] == "server") {
                group.sendMessage(getServer(list[1]))
            }else if (list.size == 1 && list[0] == "server") {
                group.sendMessage("请输入参数")
            }
        }
    }
}
