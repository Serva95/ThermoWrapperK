package it.srv.thermoWrapperK.repo

import it.srv.thermoWrapperK.model.Info
import org.springframework.data.repository.CrudRepository

interface InfoRepository: CrudRepository<Info, Short> {
    fun findFirstByOrderByIdDesc(): Info?

    fun findFirstByOrderByLastupdateDesc(): Info?

}