package it.srv.thermoWrapperK.dao

import it.srv.thermoWrapperK.model.Info
import it.srv.thermoWrapperK.repo.InfoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class InfoDAO {
    @Autowired
    val repo: InfoRepository? = null

    fun findLastTemporal(): HashMap<String, Info?> {
        val last = HashMap<String, Info?>()
        last["lastsearch"] = repo!!.findById("lastsearch").orElse(null)
        last["webversion"] = repo!!.findById("webversion").orElse(null)
        last["toolsversion"] = repo!!.findById("toolsversion").orElse(null)
        last["lastupdate"] = repo!!.findById("lastupdate").orElse(null)
        return last
    }

    fun saveHash(hash: HashMap<String, Info?>) {
        val toSave = ArrayList<Info>()
        hash["lastsearch"]?.let { toSave.add(it) }
        hash["webversion"]?.let { toSave.add(it) }
        hash["toolsversion"]?.let { toSave.add(it) }
        hash["weburl"]?.let { toSave.add(it) }
        hash["toolsurl"]?.let { toSave.add(it) }
        if (hash["lastupdate"] != null)
            hash["lastupdate"]?.let { toSave.add(it) }
        if (hash["webextra"] != null)
            hash["webextra"]?.let { toSave.add(it) }
        if (hash["toolsextra"] != null)
            hash["toolsextra"]?.let { toSave.add(it) }
        repo!!.saveAll(toSave)
    }

    operator fun get(id: String): Info { return repo!!.findById(id).orElse(null) }

    fun save(info: Info) { repo!!.save(info) }

}