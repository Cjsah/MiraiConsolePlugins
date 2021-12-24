package net.cjsah.plugin.mcspider

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.runBlocking
import net.cjsah.console.Console
import net.cjsah.console.Util
import net.cjsah.console.plugin.Plugin
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception
import java.net.ConnectException
import java.net.Socket
import java.net.URL
import java.net.UnknownHostException

internal object Utils {
    @JvmStatic
    fun getVersion(): String {
        val version = Config.getConfig()["version"].asJsonObject
        return "当前最新正式版本: ${version["release"].asString}\n当前最新快照版本: ${version["snapshot"].asString}"
    }

    @JvmStatic
    fun checkVersion(plugin: Plugin) {
        // 获取最新版本
        val value = Util.GSON.fromJson(URL("https://launchermeta.mojang.com/mc/game/version_manifest.json").readText(), JsonObject::class.java)
        val json = value["latest"].asJsonObject
        // 获取保存的版本
        val config = Config.getConfig()
        val release = json["release"].asString
        val snapshot = json["snapshot"].asString
        // 判断是否最新版本
        if (release != config["version"].asJsonObject["release"].asString || snapshot != config["version"].asJsonObject["snapshot"].asString) {
            val newVersion = if (release != config["version"].asJsonObject["release"].asString) release else snapshot
            Config.change("version", json)
            runBlocking {
                Console.logger.info("发现新版本:${newVersion}")
                Console.permissions.getAllowGroup(plugin).forEach { it.sendMessage("发现新版本:${newVersion}") }
            }
        }
    }

    @JvmStatic
    fun getBugs(id: Int): String {
        try {
            val bugJson = Gson().fromJson(URL("https://bugs.mojang.com/rest/api/2/issue/MC-$id").readText(), JsonObject::class.java)
                .asJsonObject["fields"].asJsonObject
            val versions = bugJson["versions"].asJsonArray.joinToString(" ") {
                it.asJsonObject["name"].asString
                    .replace("Minecraft ", "")
                    .replace("Pre-Release ", "Pre-")
                    .replace("Release Candidate ", "RC-")
            }
            return "https://bugs.mojang.com/browse/MC-$id\n" +
                    "${bugJson["summary"]}\n" +
                    "类型: ${bugJson.getName("issuetype", "未知")}\n" +
                    "状态: ${bugJson.getName("status", "未知")}\n" +
                    "解决结果: ${bugJson.getName("resolution", "未解决")}\n" +
                    "报告人: ${bugJson.getName("reporter", "未知")}\n" +
                    "影响版本: ${versions}\n" +
                    bugJson["description"].asString.replace("\r\n\r\n", "\n")
        }catch (e: Exception) {
            e.printStackTrace()
            return "获取失败"
        }
    }

    @JvmStatic
    fun getServer(address: String): String {
        if (!address.matches("""([^/:]+)(:\d*)?""".toRegex())) return "地址格式错误"
        val list = address.split(":")
        return when(address.split(":").size) {
            1 -> getServer(address, 25565)
            2 -> getServer(list[0], list[1].toInt())
            else -> "地址格式错误"
        }
    }

    @JvmStatic
    fun getServer(ip: String, port: Int): String {
        val s: Socket
        val dout: OutputStream
        val din: InputStream

        val out =  packet(varInt(404) + string(ip) + unss(port) + varInt(1))

        try {
            // 连接获取输入输出流
            s = Socket(ip, port)
            din = s.getInputStream()
            dout = s.getOutputStream()
            s.soTimeout = 10000 // 超时时间

            // 发送数据流
            dout.write(out)
            dout.write(packet("".toByteArray()))
            dout.flush() // 刷新

            var byteMessage = ByteArray(0) // 全部内容
            var jsonLength = 0
            var index = 0
            while (true) {
                val inMessage = ByteArray(1) // 临时获取内容
                din.read(inMessage)
                jsonLength += ((inMessage[0].toLong() and 0b1111111) shl (7 * index)).toInt()
                index++
                if (inMessage[0].toLong() and 0b11111111 shr 7 <= 0) {
                    break
                }
            }

            while (jsonLength > 0) { //循环接收数据包
                val inMessage = ByteArray(jsonLength) // 临时获取内容
                jsonLength -= din.read(inMessage)
                byteMessage += inMessage
            }
            din.close()
            dout.close()
            s.close()

            byteMessage = deleteHead(byteMessage)
            byteMessage = deleteHead(byteMessage)

            val json = Gson().fromJson(byteMessage.decodeToString(), JsonObject::class.java)
            //获取公告
            var text = ""
            if (json["description"].asJsonObject["text"].asString == "") {
                json["description"].asJsonObject["extra"].asJsonArray.forEach {
                    text += it.asJsonObject["text"].asString
                }
            }else {
                text = json["description"].asJsonObject["text"].asString
            }
            text = text.replace("""\u00a7([a-zA-Z0-9])""".toRegex(), "")
            return "$ip:$port\n${json["version"].asJsonObject["name"].asString} 服务器\n在线人数: ${json["players"].asJsonObject["online"]}/${json["players"].asJsonObject["max"]}\n$text"
        }catch (e : UnknownHostException) {
            e.printStackTrace()
            return "无法解析该地址"
        }catch (e : ConnectException) {
            e.printStackTrace()
            return "连接失败"
        }catch (e : IOException) {
            e.printStackTrace()
            return "连接异常"
        }catch (e: JsonSyntaxException) {
            e.printStackTrace()
            return "json解析异常"
        }catch (e: Exception) {
            e.printStackTrace()
            return "未知异常"
        }
    }

    @JvmStatic
    private fun JsonObject.getName(key: String, default: String): String {
        return if (this.has(key)) this[key].asJsonObject["name"].asString else default
    }

    @JvmStatic
    private fun varInt(x : Int): ByteArray {
        var byte = ByteArray(0)

        var num = (x.toLong() and 4294967295).toInt()
        while (num and -128 > 0) {
            byte += (num and 127 or 128).toByte()
            num = num shr 7
        }
        byte += num.toByte()
        return byte
    }

    @JvmStatic
    private fun packet(data : ByteArray): ByteArray {
        val id = varInt(0) + data
        val l = varInt(id.size)
        return l + id
    }

    @JvmStatic
    private fun string(s : String): ByteArray {
        val strArr = s.toByteArray()
        return varInt(strArr.size) + strArr
    }

    @JvmStatic
    private fun unss(x : Int): ByteArray {
        var c = ByteArray(0)
        c += (x/256).toByte()
        c += (x%256).toByte()
        return c
    }

    @JvmStatic
    private fun deleteHead(array: ByteArray): ByteArray {
        var newArray = array
        while (true) {
            val index0 = newArray[0]
            newArray = newArray.copyOfRange(1,newArray.size)
            if (index0.toLong() and 0b11111111 shr 7 <= 0) {
                break
            }
        }
        return newArray
    }
}

