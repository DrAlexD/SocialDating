package xelagurd.socialdating.server.test

import kotlin.random.Random
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import xelagurd.socialdating.server.exception.InvalidDataException
import xelagurd.socialdating.server.model.StatementsCursor

class StatementsCursorUnitTest {

    private val statementId = Random.nextInt(1, Int.MAX_VALUE)

    @Test
    fun decodeOrNew_noCursor_createsNewSeedWithEmptyOrderKey() {
        val cursor = StatementsCursor.decodeOrNew(null)

        assertTrue(cursor.seed.isNotEmpty())
        assertEquals("", cursor.lastOrderKey)
    }

    @Test
    fun decodeOrNew_noCursor_createsDifferentSeedEveryTime() {
        val seeds = List(10) { StatementsCursor.decodeOrNew(null).seed }

        assertNotEquals(1, seeds.toSet().size)
    }

    @Test
    fun decodeOrNew_encodedCursor_restoresSeedAndOrderKey() {
        val cursor = StatementsCursor.decodeOrNew(null).next(statementId)

        val decodedCursor = StatementsCursor.decodeOrNew(cursor.encode())

        assertEquals(cursor, decodedCursor)
    }

    @Test
    fun decodeOrNew_wrongCursor_throwsInvalidData() {
        val seed = StatementsCursor.decodeOrNew(null).seed
        val orderKey = StatementsCursor.orderKey(statementId, seed)

        listOf(
            "",
            seed,
            "$seed:",
            ":$orderKey",
            "$seed:$orderKey:extra",
            "${seed}0:$orderKey",
            "$seed:${orderKey.dropLast(1)}"
        ).forEach {
            assertThrows<InvalidDataException> { StatementsCursor.decodeOrNew(it) }
        }
    }

    @Test
    fun orderKey_sameArguments_returnsSameMd5Hex() {
        val seed = StatementsCursor.decodeOrNew(null).seed

        val orderKey = StatementsCursor.orderKey(statementId, seed)

        assertEquals(StatementsCursor.orderKey(statementId, seed), orderKey)
        assertTrue(orderKey.matches(Regex("[0-9a-f]{32}")))
    }

    @Test
    fun orderKey_differentSeeds_returnsDifferentOrder() {
        val statementIds = (1..50).toList()
        val firstSeed = StatementsCursor.decodeOrNew(null).seed
        val secondSeed = StatementsCursor.decodeOrNew(null).seed

        val firstOrder = statementIds.sortedBy { StatementsCursor.orderKey(it, firstSeed) }
        val secondOrder = statementIds.sortedBy { StatementsCursor.orderKey(it, secondSeed) }

        assertNotEquals(statementIds, firstOrder)
        assertNotEquals(firstOrder, secondOrder)
    }
}
