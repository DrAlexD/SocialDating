package xelagurd.socialdating.server.utils

import org.springframework.http.ResponseEntity
import xelagurd.socialdating.server.model.dto.PageDto

object DataUtils {

    fun <T> responseEntities(block: () -> List<T>): ResponseEntity<List<T>> {
        val result = block()
        return when {
            result.isEmpty() -> ResponseEntity.noContent().build()
            else -> ResponseEntity.ok(result)
        }
    }

    fun <T> responseEntity(block: () -> T?): ResponseEntity<T> {
        val result = block()
        return when {
            result == null -> ResponseEntity.noContent().build()
            else -> ResponseEntity.ok(result)
        }
    }

    fun <T> responsePage(block: () -> PageDto<T>): ResponseEntity<PageDto<T>> {
        val result = block()
        return when {
            result.content.isEmpty() -> ResponseEntity.noContent().build()
            else -> ResponseEntity.ok(result)
        }
    }
}
