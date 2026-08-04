package it.srv.thermoWrapperK.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class RemoteVersionResponse(
    val webversion: String,
    val toolsversion: String,
    val weburl: String,
    val toolsurl: String,
    val webextra: String? = null,
    val toolsextra: String? = null
)
