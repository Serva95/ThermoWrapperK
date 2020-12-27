package it.srv.thermoWrapperK

import it.srv.thermoWrapperK.dao.InfoDAO
import it.srv.thermoWrapperK.exception.JarExecutorException
import it.srv.thermoWrapperK.model.Info
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Configuration
class Runner {
    private val logger = LoggerFactory.getLogger(ThermoWrapperKApplication::class.java)
    val versionManager = VersionManager()
    private val webExecutor: JarExecutor = JarExecutor()
    private val toolsExecutor: JarExecutor = JarExecutor()

    @Autowired
    val infoDAO = InfoDAO()

    @Bean
    fun executer() {
        val info = infoDAO.findLastTemporal()
        val search = runBlocking { versionManager.searchNewVersion() }
        if (info["lastupdate"] == null) {
            with(versionManager) {
                download(search["weburl"]!!.value!!, "ThermoSmartSpring${search["webversion"]?.value}.jar")
                download(search["toolsurl"]!!.value!!, "ThermoTools${search["toolsversion"]?.value}.jar")
            }
            search["lastupdate"] = Info("lastupdate", "lastupdate", LocalDateTime.now())
            infoDAO.saveHash(search)
        } else {
            versionManager.checkVersions(search, info, infoDAO, true)
        }
        WebRunner().start()
        ToolsRunner().start()
        scheduledRunner()
    }

    inner class WebRunner : Runnable {
        private var t: Thread? = null
        override fun run() {
            val version = infoDAO["webversion"].value
            try {
                webExecutor.executeJar("ThermoSmartSpring$version")
            } catch (e: JarExecutorException) {
                e.printStackTrace()
            }
        }
        fun start() {
            if (t == null) {
                t = Thread(this)
                t!!.start()
            }
        }
    }

    inner class ToolsRunner : Runnable {
        private var t: Thread? = null
        override fun run() {
            val version = infoDAO["toolsversion"].value
            try {
                toolsExecutor.executeJar("ThermoTools$version")
            } catch (e: JarExecutorException) {
                e.printStackTrace()
            }
        }
        fun start() {
            if (t == null) {
                t = Thread(this)
                t!!.start()
            }
        }
    }

    inner class VersionManagerRunner : Runnable {
        override fun run() {
            val newVersion =  runBlocking { versionManager.searchNewVersion() }
            val actualVersion = infoDAO.findLastTemporal()
            val installed = versionManager.checkVersions(newVersion, actualVersion, infoDAO, false)
            if (installed) {
                if (!newVersion["webversion"]?.value.equals(actualVersion["webversion"]?.value, true)) {
                    webExecutor.destroy()
                    try {
                        Thread.sleep(1500)
                    } catch (ignored: InterruptedException) { }
                    val old = File("ThermoSmartSpring${actualVersion["webversion"]?.value}.jar")
                    if (old.exists() && old.delete()) println("Thermo jar deleted") else println("Error in Thermo jar deletion")
                    val wr = WebRunner()
                    wr.start()
                }
                if (!newVersion["toolsversion"]?.value.equals(actualVersion["toolsversion"]?.value, true)) {
                    toolsExecutor.destroy()
                    try {
                        Thread.sleep(1500)
                    } catch (ignored: InterruptedException) { }
                    val old = File("ThermoTools${actualVersion["toolsversion"]?.value}.jar")
                    if (old.exists() && old.delete()) println("Tools jar deleted") else println("Error in Tools jar deletion")
                    val tr = ToolsRunner()
                    tr.start()
                }
            }
        }
    }

    fun scheduledRunner() {
        val scheduler = Executors.newSingleThreadScheduledExecutor()
        val delay:LocalTime
        val now = LocalTime.now()
        delay = if (now.isBefore(LocalTime.of(1, 0)))
            LocalTime.of(1, 0).minusSeconds(now.toSecondOfDay().toLong())
        else
            LocalTime.MAX.minusSeconds(now.toSecondOfDay().toLong()).plusSeconds(3600)
        //scheduler.scheduleAtFixedRate(VersionManagerRunner(), delay.toSecondOfDay().toLong(), TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS)
        scheduler.scheduleAtFixedRate(VersionManagerRunner(), 30, 45, TimeUnit.SECONDS)
    }

}