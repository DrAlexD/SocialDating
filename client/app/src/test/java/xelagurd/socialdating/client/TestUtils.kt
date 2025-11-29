package xelagurd.socialdating.client

import io.mockk.mockk

object TestUtils {

    inline fun <reified T : Any> mockkList(size: Int = 5, relaxed: Boolean = false): List<T> =
        List(size) { mockk(relaxed = relaxed) }
}