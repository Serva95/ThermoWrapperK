package it.srv.thermoWrapperK.model

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "info")
data class Info(
    @Id
    @Column(name = "id", nullable = false)
    var id: String? = null,

    @Column(name = "value", nullable = false)
    var value: String? = null,

    @Column(name = "time", nullable = true)
    var time: LocalDateTime? = null
)
