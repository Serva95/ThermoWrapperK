package it.srv.thermoWrapperK

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import it.srv.thermoWrapperK.model.RemoteVersionResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class VersionManagerTest {

    private val objectMapper = ObjectMapper().registerKotlinModule()

    @Test
    fun `test deserialization of RemoteVersionResponse`() {
        val json = """
            {
                "webversion": "2.0.0",
                "toolsversion": "3.0.0",
                "weburl": "https://example.com/web.jar",
                "toolsurl": "https://example.com/tools.jar"
            }
        """.trimIndent()

        val response = objectMapper.readValue(json, RemoteVersionResponse::class.java)
        assertEquals("2.0.0", response.webversion)
        assertEquals("3.0.0", response.toolsversion)
        assertEquals("https://example.com/web.jar", response.weburl)
        assertEquals("https://example.com/tools.jar", response.toolsurl)
    }

    @Test
    fun `test searchNewVersion with successful response`() = runBlocking {
        val json = """
            {
                "webversion": "2.0.0",
                "toolsversion": "3.0.0",
                "weburl": "https://example.com/web.jar",
                "toolsurl": "https://example.com/tools.jar"
            }
        """.trimIndent()

        val mockEngine = MockEngine { _ ->
            respond(
                content = json,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val versionManager = VersionManager(HttpClient(mockEngine))

        val result = versionManager.searchNewVersion()

        assertEquals("2.0.0", result["webversion"]?.value)
        assertEquals("3.0.0", result["toolsversion"]?.value)
        assertEquals("https://example.com/web.jar", result["weburl"]?.value)
        assertEquals("https://example.com/tools.jar", result["toolsurl"]?.value)
    }

    @Test
    fun `test deserialization failure`() {
        val invalidJson = """{"webversion": "2.0.0", "broken": """
        assertFailsWith<Exception> {
            objectMapper.readValue(invalidJson, RemoteVersionResponse::class.java)
        }
    }
}
