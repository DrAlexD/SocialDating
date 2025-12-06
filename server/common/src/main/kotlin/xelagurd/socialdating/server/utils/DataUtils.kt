package xelagurd.socialdating.server.utils

import org.springframework.http.ResponseEntity

object DataUtils {

    fun <T : Any> responseEntities(block: () -> List<T>): ResponseEntity<List<T>> {
        val result = block()
        return when {
            result.isEmpty() -> ResponseEntity.noContent().build()
            else -> ResponseEntity.ok(result)
        }
    }

    fun <T : Any> responseEntity(block: () -> T?): ResponseEntity<T> {
        val result = block()
        return when {
            result == null -> ResponseEntity.noContent().build()
            else -> ResponseEntity.ok(result)
        }
    }
}