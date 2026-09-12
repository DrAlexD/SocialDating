package xelagurd.socialdating.server.utils

import xelagurd.socialdating.server.exception.InvalidDataException

object CursorUtils {
    private const val CURSOR_SEPARATOR = ":"

    fun encodeCursorParts(vararg parts: Any) =
        parts.joinToString(CURSOR_SEPARATOR)

    fun decodeCursorParts(cursor: String, partsNumber: Int): List<String> {
        val parts = cursor.split(CURSOR_SEPARATOR)

        if (parts.size != partsNumber) throw InvalidDataException("error.invalidData.wrongCursor")

        return parts
    }
}
