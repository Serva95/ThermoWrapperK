package it.srv.thermoWrapperK

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import it.srv.thermoWrapperK.dao.InfoDAO
import it.srv.thermoWrapperK.model.Info
import it.srv.thermoWrapperK.model.RemoteVersionResponse
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.net.URL

class VersionManager(private val client: HttpClient = HttpClient(CIO)) {
    private val log = LoggerFactory.getLogger(VersionManager::class.java)
    private val searchUrl = "https://serva.click/prove/thermo.php"
    private val objectMapper = ObjectMapper().findAndRegisterModules()

    suspend fun searchNewVersion(): HashMap<String, Info?> {
        val result = HashMap<String, Info?>()
        try {
            val response: HttpResponse = client.get(searchUrl)
            val responseBody = response.body<String>()
            val remoteData = objectMapper.readValue<RemoteVersionResponse>(responseBody)
            
            result["webversion"] = Info("webversion", remoteData.webversion, LocalDateTime.now())
            result["weburl"] = Info("weburl", remoteData.weburl, LocalDateTime.now())
            result["toolsversion"] = Info("toolsversion", remoteData.toolsversion, LocalDateTime.now())
            result["toolsurl"] = Info("toolsurl", remoteData.toolsurl, LocalDateTime.now())
        } catch (e: Exception) {
            log.error("Error searching for new version: ${e.message}", e)
        }
        return result
    }

    fun download(url: String, fileName: String) {
        try {
            log.info("Downloading $url to $fileName")
            URL(url).openStream().use { input ->
                FileOutputStream(File(fileName)).use { output ->
                    input.copyTo(output)
                }
            }
            log.info("Download complete: $fileName")
        } catch (e: Exception) {
            log.error("Failed to download $url: ${e.message}")
            throw e
        }
    }

    fun checkVersions(new: HashMap<String, Info?>, current: HashMap<String, Info?>, dao: InfoDAO, update: Boolean): Boolean {
        var changed = false
        val keysToCheck = listOf("webversion" to ("weburl" to "ThermoSmartSpring"), "toolsversion" to ("toolsurl" to "ThermoTools"))

        for ((verKey, urlAndPrefix) in keysToCheck) {
            val (urlKey, prefix) = urlAndPrefix
            val newVer = new[verKey]?.value
            val curVer = current[verKey]?.value
            if (newVer != null && newVer != curVer) {
                log.info("Version change detected for $verKey: $curVer -> $newVer")
                changed = true
                download(new[urlKey]!!.value!!, "$prefix$newVer.jar")
            }
        }

        if (changed) {
            dao.saveHash(new)
        }
        return changed
    }
}