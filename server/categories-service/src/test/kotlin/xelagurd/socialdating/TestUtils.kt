package xelagurd.socialdating

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object TestUtils {
    private val objectMapper = jacksonObjectMapper()

    fun convertObjectToJsonString(obj: Any): String {
        return objectMapper.writeValueAsString(obj)
    }
}