package icu.takeneko.appwebterminal.support.http

object HttpServerLifecycleSupport {
    private var serverInstance: HttpServer? = null

    fun launch(port: Int) {
        serverInstance?.interrupt()
        serverInstance = HttpServer(port)
        serverInstance!!.start()
    }

    fun stop() {
        serverInstance?.gracefullyStop()
        serverInstance = null
    }
}