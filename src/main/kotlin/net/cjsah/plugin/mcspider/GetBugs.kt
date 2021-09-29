package net.cjsah.plugin.mcspider

import com.google.gson.Gson
import com.google.gson.JsonObject
import java.lang.Exception
import java.net.URL

object GetBugs {
    fun getBugs(id: Int): String {
        try {
            var bugJson = Gson().fromJson(URL("https://bugs.mojang.com/rest/api/2/issue/MC-$id").readText(), JsonObject::class.java)
            bugJson = bugJson.asJsonObject["fields"].asJsonObject

            var versions = ""
            bugJson["versions"].asJsonArray.forEach {
                versions += it.asJsonObject["name"].asString
                    .replace("Minecraft ", "")
                    .replace("Pre-Release ", "Pre-")
                    .replace("Release Candidate ", "RC-") +
                        " "
            }

            return "https://bugs.mojang.com/browse/MC-$id\n" +
                    "${bugJson["summary"]}\n" +
                    "类型: ${getName(bugJson, "issuetype", "未知")}\n" +
                    "状态: ${getName(bugJson, "status", "未知")}\n" +
                    "解决结果: ${getName(bugJson, "resolution", "未解决")}\n" +
                    "报告人: ${getName(bugJson, "reporter", "未知")}\n" +
                    "影响版本: ${versions}\n" +
                    bugJson["description"].asString.replace("\r\n\r\n", "\n")
        }catch (e: Exception) {
            e.printStackTrace()
            return "获取失败"
        }

    }

    private fun getName(json: JsonObject, type: String, default: String): String {
        if (!json[type].isJsonNull)
            return json[type].asJsonObject["name"].asString
        return default

    }


}