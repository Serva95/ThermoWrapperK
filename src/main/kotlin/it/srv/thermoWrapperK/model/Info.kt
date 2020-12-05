package it.srv.thermoWrapperK.model

import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "info")
class Info : Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Short? = null

    @Column(name = "webversion", nullable = false)
    var webversion: String? = null

    @Column(name = "toolsversion", nullable = false)
    var toolsversion: String? = null

    @Column(name = "webextra")
    var webextra: String? = null

    @Column(name = "toolsextra")
    var toolsextra: String? = null

    @Column(name = "weburl", nullable = false)
    var weburl: String? = null

    @Column(name = "toolsurl", nullable = false)
    var toolsurl: String? = null

    @Column(name = "lastsearch", nullable = false)
    var lastsearch: LocalDateTime? = null

    @Column(name = "lastupdate", nullable = false)
    var lastupdate: LocalDateTime? = null

    companion object {
        private const val serialVersionUID = 1L
    }
}