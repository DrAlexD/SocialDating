package xelagurd.socialdating.server.utils

import kotlin.random.Random
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.mockk

object TestUtils {
    private val objectMapper = jacksonObjectMapper()

    fun readObjectFromJsonString(json: String): Map<String, Any> =
        objectMapper.readValue(json, object : TypeReference<Map<String, Any>>() {})

    fun readArrayFromJsonString(json: String): List<Map<String, Any>> =
        objectMapper.readValue(json, object : TypeReference<List<Map<String, Any>>>() {})

    fun List<Any>.toRequestParams() =
        this.toString().removeSurrounding("[", "]").replace(" ", "")

    inline fun <reified T : Any> mockkList(size: Int = 5, relaxed: Boolean = false): List<T> =
        List(size) { mockk(relaxed = relaxed) }

    fun Random.nextIntList() =
        List(5) { this.nextInt() }
}