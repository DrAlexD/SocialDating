package xelagurd.socialdating.server.model

import xelagurd.socialdating.server.exception.InvalidDataException
import xelagurd.socialdating.server.model.additional.SimilarUserData
import xelagurd.socialdating.server.utils.CursorUtils.decodeCursorParts
import xelagurd.socialdating.server.utils.CursorUtils.encodeCursorParts

data class SimilarUsersCursor(
    val lastDifferenceNumber: Int,
    val lastUserId: Int
) {

    fun isAfter(similarUser: SimilarUserData) =
        similarUser.differenceNumber < lastDifferenceNumber ||
                (similarUser.differenceNumber == lastDifferenceNumber && similarUser.id > lastUserId)

    fun encode() = encodeCursorParts(lastDifferenceNumber, lastUserId)

    companion object {
        private const val CURSOR_PARTS_NUMBER = 2

        fun decodeOrNull(cursor: String?): SimilarUsersCursor? {
            if (cursor == null) return null

            val (lastDifferenceNumber, lastUserId) = decodeCursorParts(cursor, CURSOR_PARTS_NUMBER)

            return SimilarUsersCursor(lastDifferenceNumber.toCursorPart(), lastUserId.toCursorPart())
        }

        fun of(similarUser: SimilarUserData) =
            SimilarUsersCursor(similarUser.differenceNumber, similarUser.id)

        private fun String.toCursorPart() =
            toIntOrNull() ?: throw InvalidDataException("error.invalidData.wrongCursor")
    }
}
