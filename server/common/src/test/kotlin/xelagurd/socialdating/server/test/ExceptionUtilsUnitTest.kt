package xelagurd.socialdating.server.test

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import xelagurd.socialdating.server.utils.ExceptionUtils
import xelagurd.socialdating.server.utils.ExceptionUtils.transformNotUniqueDataMessage

class ExceptionUtilsUnitTest {

    @Test
    fun getErrorPositionFromStackTrace_emptyStackTrace_returnsUnknownLocation() {
        assertEquals("Unknown location", ExceptionUtils.getErrorPositionFromStackTrace(emptyArray()))
    }

    @Test
    fun getErrorPositionFromStackTrace_withElement_returnsFormattedPosition() {
        val element = StackTraceElement("com.example.MyClass", "myMethod", "MyClass.kt", 42)

        assertEquals(
            "com.example.MyClass.myMethod(MyClass.kt:42)",
            ExceptionUtils.getErrorPositionFromStackTrace(arrayOf(element))
        )
    }

    @Test
    fun createWrongDataMessage_nullMessage_titlecasesSortsAndUsesDefault() {
        val result = ExceptionUtils.createWrongDataMessage(
            listOf(
                "zebra" to "must be positive",
                "apple" to null
            )
        )

        assertEquals("'Apple' has wrong value; 'Zebra' must be positive", result)
    }

    @Test
    fun createWrongDataMessage_emptyList_returnsEmptyString() {
        assertEquals("", ExceptionUtils.createWrongDataMessage(emptyList()))
    }

    @Test
    fun createWrongDataMessage_emptyFieldName_keepsEmptyName() {
        assertEquals("'' must not be blank", ExceptionUtils.createWrongDataMessage(listOf("" to "must not be blank")))
    }

    @Test
    fun transformNotUniqueDataMessage_pluralIesTable_returnsSingularYMessage() {
        val message =
            "Unique index violation: \"PUBLIC.UK_X ON PUBLIC.CATEGORIES(NAME NULLS FIRST) VALUES ( 1, 'Sport' )\""

        assertEquals("Category with 'Sport' name already exists", message.transformNotUniqueDataMessage())
    }

    @Test
    fun transformNotUniqueDataMessage_pluralSTable_returnsSingularMessage() {
        val message =
            "Unique index violation: \"PUBLIC.UK_X ON PUBLIC.USERS(USERNAME NULLS FIRST) VALUES ( 1, 'admin' )\""

        assertEquals("User with 'admin' username already exists", message.transformNotUniqueDataMessage())
    }

    @Test
    fun transformNotUniqueDataMessage_singularTable_keepsTableNameMessage() {
        val message =
            "Unique index violation: \"PUBLIC.UK_X ON PUBLIC.USER_DATA(LOGIN NULLS FIRST) VALUES ( 1, 'root' )\""

        assertEquals("User data with 'root' login already exists", message.transformNotUniqueDataMessage())
    }

    @Test
    fun transformNotUniqueDataMessage_singleLetterPluralTable_singularizesToEmptyName() {
        val message =
            "Unique index violation: \"PUBLIC.UK_X ON PUBLIC.S(F NULLS FIRST) VALUES ( 1, 'v' )\""

        assertEquals(" with 'v' f already exists", message.transformNotUniqueDataMessage())
    }

    @Test
    fun transformNotUniqueDataMessage_noTableMatch_returnsNull() {
        assertNull("totally unrelated error message".transformNotUniqueDataMessage())
    }

    @Test
    fun transformNotUniqueDataMessage_noValueMatch_returnsNull() {
        val message = "Unique index violation: \"PUBLIC.UK_X ON PUBLIC.CATEGORIES(NAME NULLS FIRST)\""

        assertNull(message.transformNotUniqueDataMessage())
    }
}
