package it.srv.thermoWrapperK.model

import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "info")
class Info(id: String, value: String, time: LocalDateTime?) {
    @Id
    @Column(name = "id", nullable = false)
    var id: String? = null

    @Column(name = "value", nullable = false)
    var value: String? = null

    @Column(name = "time", nullable = true)
    var time: LocalDateTime? = null
}