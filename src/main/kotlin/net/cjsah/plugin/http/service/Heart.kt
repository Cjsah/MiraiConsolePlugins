package net.cjsah.plugin.http.service

import net.cjsah.plugin.http.Utils
import org.apache.logging.log4j.LogManager
import java.util.*
import kotlin.concurrent.timerTask

class Heart : IHttpService {

    private val timer = Timer("HeartBeat", false)

    override fun start() {
        timer.schedule(timerTask {
            pingAll()
        }, 1000, 15000)

        Utils.logger.info("心跳模块已启动")

    }

    override fun stop() {
        timer.cancel()
        timer.purge()

        Utils.logger.info("心跳模块已关闭")
    }

    private fun pingAll() {

    }

    private fun ping(port: String) {

    }
}