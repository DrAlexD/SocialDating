package xelagurd.socialdating.server.test

import org.springframework.http.HttpStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import xelagurd.socialdating.server.utils.DataUtils

class DataUtilsUnitTest {

    @Test
    fun responseEntities_nonEmptyList_returnsOkWithBody() {
        val data = listOf(1, 2, 3)

        val response = DataUtils.responseEntities { data }

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(data, response.body)
    }

    @Test
    fun responseEntities_emptyList_returnsNoContent() {
        val response = DataUtils.responseEntities { emptyList<Int>() }

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        assertNull(response.body)
    }

    @Test
    fun responseEntity_nonNullValue_returnsOkWithBody() {
        val response = DataUtils.responseEntity { "value" }

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("value", response.body)
    }

    @Test
    fun responseEntity_nullValue_returnsNoContent() {
        val response = DataUtils.responseEntity<String> { null }

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        assertNull(response.body)
    }
}
