package xelagurd.socialdating.server.utils

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object TestUtils {
    private val objectMapper = jacksonObjectMapper()

    fun convertObjectToJsonString(obj: Any): String {
        return objectMapper.writeValueAsString(obj)
    }

    fun readObjectFromJsonString(json: String): Map<String, Any> {
        return objectMapper.readValue(json, object : TypeReference<Map<String, Any>>() {})
    }

    fun readArrayFromJsonString(json: String): List<Map<String, Any>> {
        return objectMapper.readValue(json, object : TypeReference<List<Map<String, Any>>>() {})
    }

    fun List<Any>.toRequestParams(): String {
        return this.toString().removeSurrounding("[", "]").replace(" ", "")
    }
}