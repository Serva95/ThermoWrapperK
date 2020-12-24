package it.srv.thermoWrapperK

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import it.srv.thermoWrapperK.dao.InfoDAO
import it.srv.thermoWrapperK.model.Info
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.nio.channels.Channels
import java.time.LocalDateTime

class VersionManager {
    private val client = HttpClient(CIO)

    suspend fun searchNewVersion(): HashMap<String, Info> {
        val new = HashMap<String, Info>()
        try {
            val content: String = client.request("http://serva.altervista.org/prove/thermo.php")
            val json = JSONObject(content)
            new["lastsearch"] = Info("lastsearch", "lastsearch", LocalDateTime.now())
            new["webversion"] = Info("webversion", json.getString("webversion"), LocalDateTime.now())
            new["toolsversion"] = Info("toolsversion", json.getString("toolsversion"), LocalDateTime.now())
            new["weburl"] = Info("weburl", json.getString("weburl"), null)
            new["toolsurl"] = Info("toolsurl", json.getString("toolsurl"), null)
            if (!json.optString("webextra").equals("", true)){
                new["webextra"] = Info("webextra", json.getString("webextra"), null)
            }
            if (!json.optString("toolsextra").equals("", true)){
                new["toolsextra"] = Info("toolsextra", json.getString("toolsextra"), null)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return new
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

    fun checkVersions(newVersion: HashMap<String, Info>, actualVersion: HashMap<String, Info>, infoDAO: InfoDAO, startup: Boolean): Boolean {
        var newVersionInstalled = false
        if (!newVersion["webversion"]?.value.equals(actualVersion["webversion"]?.value, true)
                || !newVersion["toolsversion"]?.value.equals(actualVersion["toolsversion"]?.value, true)) {
            if (!newVersion["webversion"]?.value.equals(actualVersion["webversion"]?.value, true)) {
                download(newVersion["weburl"]?.value, "ThermoSmartSpring" + newVersion["webversion"]?.value + ".jar")
                if (startup) {
                    val old = File("ThermoSmartSpring${actualVersion["webversion"]?.value}.jar")
                    if (old.exists() && old.delete()) println("Thermo jar deleted") else println("Error in Thermo jar deletion")
                }
            }
            if (!newVersion["toolsversion"]?.value.equals(actualVersion["toolsversion"]?.value, true)) {
                download(newVersion["toolsurl"]?.value, "ThermoTools" + newVersion["toolsversion"]?.value + ".jar")
                if (startup) {
                    val old = File("ThermoTools${actualVersion["toolsversion"]?.value}.jar")
                    if (old.exists() && old.delete()) println("Tools jar deleted") else println("Error in Tools jar deletion")
                }
            }
            newVersion["lastupdate"] = Info("lastupdate", "lastupdate", LocalDateTime.now())
            infoDAO.saveHash(newVersion)
            newVersionInstalled = true
        } else {
            actualVersion["lastsearch"] = Info("lastsearch", "lastsearch", LocalDateTime.now())
            infoDAO.saveHash(actualVersion)
        }
        return newVersionInstalled
    }
}