package it.srv.thermoWrapperK.model

import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "info")
class Info() {
    constructor(id: String?, value: String, time: LocalDateTime?) : this() {
        this.id = id
        this.value = value
        this.time = time
    }

    @Id
    @Column(name = "id", nullable = false)
    var id: String? = null

    @Column(name = "value", nullable = false)
    var value: String? = null

    @Column(name = "time", nullable = true)
    var time: LocalDateTime? = null
}