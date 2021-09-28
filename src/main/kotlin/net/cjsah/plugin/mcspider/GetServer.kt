package net.cjsah.plugin.mcspider

import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.get
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonSyntaxException
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception
import java.net.ConnectException
import java.net.Socket
import java.net.UnknownHostException

object GetServer {
    fun getServer(address: String): String {
        val ip : String?
        var port = 25565

        when(address.split(":").size) {
            1 -> ip = address
            2 -> {
                try {
                    ip = address.split(":")[0]
                    port = address.split(":")[1].toInt()
                }catch (e : Exception) {
                    return "地址格式错误"
                }
            }
            else -> return "地址格式错误"
        }

        val s: Socket?

        val dout: OutputStream?

        val din: InputStream?

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
//        println(jsonLength)

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

            val json : JsonElement = Gson().fromJson(byteMessage.decodeToString())
            //获取公告
            var text = ""
            if (json["description"]["text"].asString == "") {
                json["description"]["extra"].asJsonArray.forEach {
                    text += it["text"].asString
                }
            }else {
                text = json["description"]["text"].asString
            }
            text = text.replace("""\u00a7([a-zA-Z0-9])""".toRegex(), "")
            return "$address\n${json["version"]["name"].asString} 服务器\n在线人数: ${json["players"]["online"]}/${json["players"]["max"]}\n$text"
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

    private fun packet(data : ByteArray): ByteArray {
        val id = varInt(0) + data
        val l = varInt(id.size)
        return l + id
    }

    private fun string(s : String): ByteArray {
        val strArr = s.toByteArray()
        return varInt(strArr.size) + strArr
    }

    private fun unss(x : Int): ByteArray {
        var c = ByteArray(0)
        c += (x/256).toByte()
        c += (x%256).toByte()
        return c
    }
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
