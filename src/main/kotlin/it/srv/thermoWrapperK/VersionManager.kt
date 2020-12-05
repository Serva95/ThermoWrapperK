package it.srv.thermoWrapperK

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import it.srv.thermoWrapperK.dao.InfoDAO
import it.srv.thermoWrapperK.model.Info
import org.json.JSONException
import org.json.JSONObject
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.nio.channels.Channels
import java.time.LocalDateTime

class VersionManager {
    private val client = HttpClient(CIO)

    suspend fun searchNewVersion(): Info {
        val info = Info()
        try {
            val content: String = client.request("http://serva.altervista.org/prove/thermo.php")
            val json = JSONObject(content)
            info.lastsearch = LocalDateTime.now()
            info.webversion = json.getString("webversion")
            info.toolsversion = json.getString("toolsversion")
            info.weburl = json.getString("weburl")
            info.toolsurl = json.getString("toolsurl")
            info.webextra = if (json.optString("webextra").equals("", true)) null else json.optString("webextra")
            info.toolsextra = if (json.optString("toolsextra").equals("", true)) null else json.optString("toolsextra")
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return info
    }

    fun download(url: String?, fileName: String) {
        try {
            val website = URL(url)
            val rbc = Channels.newChannel(website.openStream())
            val fos = FileOutputStream(fileName)
            //134MB max download
            fos.channel.transferFrom(rbc, 0, (1 shl 27).toLong())
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException()
        }
    }

    fun checkVersions(newVersion: Info, actualVersion: Info, infoDAO: InfoDAO): Boolean {
        var newVersionInstalled = false
        if (!newVersion.webversion.equals(actualVersion.webversion, true)
                || !newVersion.toolsversion.equals(actualVersion.toolsversion, true)) {
            if (!newVersion.webversion.equals(actualVersion.webversion, true)) download(newVersion.weburl, "ThermoSmartSpring" + newVersion.webversion + ".jar")
            if (!newVersion.toolsversion.equals(actualVersion.toolsversion, true)) download(newVersion.toolsurl, "ThermoTools" + newVersion.toolsversion + ".jar")
            newVersion.lastupdate = LocalDateTime.now()
            infoDAO.save(newVersion)
            newVersionInstalled = true
        } else {
            actualVersion.lastsearch = LocalDateTime.now()
            infoDAO.save(actualVersion)
        }
        return newVersionInstalled
    }
}