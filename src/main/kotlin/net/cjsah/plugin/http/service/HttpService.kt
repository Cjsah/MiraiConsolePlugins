package net.cjsah.plugin.http.service

import net.cjsah.plugin.http.Connection

class HttpService : IHttpService{

    private val services: List<IHttpService> = listOf(Heart(), Report())
    private val connections: List<Connection> = listOf()

    override fun start() {
        services.forEach {
            it.start()
        }
    }

    override fun stop() {
        services.forEach {
            it.stop()
        }
    }

}