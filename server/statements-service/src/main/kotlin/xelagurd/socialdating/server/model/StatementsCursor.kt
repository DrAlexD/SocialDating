package xelagurd.socialdating.server.model

import java.security.MessageDigest
import xelagurd.socialdating.server.exception.InvalidDataException
import xelagurd.socialdating.server.utils.CursorUtils.decodeCursorParts
import xelagurd.socialdating.server.utils.CursorUtils.encodeCursorParts

data class StatementsCursor(
    val seed: String,
    val lastOrderKey: String = FIRST_PAGE_ORDER_KEY
) {

    fun next(lastStatementId: Int) = copy(lastOrderKey = orderKey(lastStatementId, seed))

    fun encode() = encodeCursorParts(seed, lastOrderKey)

    companion object {
        // any md5 hex is greater than the empty string, so it starts the order from the very beginning
        private const val FIRST_PAGE_ORDER_KEY = ""
        private const val CURSOR_PARTS_NUMBER = 2
        private const val SEED_LENGTH = 8
        private const val ORDER_KEY_LENGTH = 32

        private val seedSymbols = ('0'..'9') + ('a'..'f')

        fun decodeOrNew(cursor: String?): StatementsCursor {
            if (cursor == null) return StatementsCursor(seed = generateSeed())

            val (seed, lastOrderKey) = decodeCursorParts(cursor, CURSOR_PARTS_NUMBER)

            if (seed.length != SEED_LENGTH || lastOrderKey.length != ORDER_KEY_LENGTH) {
                throw InvalidDataException("error.invalidData.wrongCursor")
            }

            return StatementsCursor(seed, lastOrderKey)
        }

        // must stay equal to the order expression of StatementsRepository.findUnreactedStatements
        fun orderKey(statementId: Int, seed: String) =
            MessageDigest.getInstance("MD5")
                .digest("$statementId$seed".toByteArray())
                .joinToString("") { "%02x".format(it) }

        private fun generateSeed() = String(CharArray(SEED_LENGTH) { seedSymbols.random() })
    }
}
