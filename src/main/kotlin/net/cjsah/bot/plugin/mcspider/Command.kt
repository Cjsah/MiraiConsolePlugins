package net.cjsah.bot.plugin.mcspider

import net.cjsah.bot.plugin.mcspider.GetServer.getServer
import net.cjsah.bot.plugin.mcspider.GetVersion.getVersion
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.message.data.PlainText

class Command(
    private val group: Group,
    private val msg: String,
    private val bot: Bot
    ) {

    suspend fun run() {
        val help = "/help(?)        显示帮助\n" +
                "/mcv            获取最新游戏版本\n" +
                "/server [ip]    获取某ip的服务器信息\n" +
                "/mc-[id]        获取mcbugs"

        if (msg == "?" || msg == "help") {
            sendMessage(help)
        }else if (msg == "mcv") {
            sendMessage(getVersion(bot))
        }else if (msg.matches("""server [a-zA-Z0-9.:]+""".toRegex())) {
            sendMessage(getServer(msg.split(" ")[1]))
        }else if (msg.matches("""[Mm][Cc]-[0-9]+""".toRegex())) {
            sendMessage(GetBugs.getBugs(msg.split("-")[1]))
        }
    }

    private suspend fun sendMessage(message: String) {
        group.sendMessage(PlainText(message))
    }
}
