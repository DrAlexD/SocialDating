package xelagurd.socialdating.server.utils

import org.springframework.http.ResponseEntity

object DataUtils {

    fun <T> responseEntities(block: () -> List<T>): ResponseEntity<List<T>> {
        val result = block()
        return if (result.isEmpty()) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(result)
        }
    }

    fun <T> responseEntity(block: () -> T?): ResponseEntity<T> {
        val result = block()
        return if (result == null) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(result)
        }
    }

}