package it.srv.thermoWrapperK.dao

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import it.srv.thermoWrapperK.model.Info;
import it.srv.thermoWrapperK.repo.InfoRepository;

@Service
@Transactional
class InfoDAO {
    @Autowired
    val repo: InfoRepository? = null
    fun findFirst(): Info? {
        return repo!!.findFirstByOrderByIdDesc()
    }

    fun findLastTemporal(): Info? {
        return repo!!.findFirstByOrderByLastupdateDesc()
    }

    fun save(info: Info): Info {
        return repo!!.save(info)
    }

    operator fun get(id: Short): Info {
        return repo!!.findById(id).orElse(null)
    }

    fun delete(id: Short) {
        repo!!.deleteById(id)
    }
}