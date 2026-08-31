package xelagurd.socialdating.server.test

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import xelagurd.socialdating.server.utils.ExceptionUtils
import xelagurd.socialdating.server.utils.ExceptionUtils.transformNotUniqueDataMessage
import xelagurd.socialdating.server.utils.ExceptionUtils.transformWrongDataMessage

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
    fun getErrorPositionFromStackTrace_frameworkAndAppFrames_returnsAppFrame() {
        val frameworkElement = StackTraceElement("org.springframework.web.Some", "resolve", "Some.java", 10)
        val appElement = StackTraceElement("xelagurd.socialdating.server.service.SomeService", "save", "SomeService.kt", 20)

        assertEquals(
            "xelagurd.socialdating.server.service.SomeService.save(SomeService.kt:20)",
            ExceptionUtils.getErrorPositionFromStackTrace(arrayOf(frameworkElement, appElement))
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
    fun createWrongDataMessage_snakeCaseField_convertsToCamelCase() {
        val result = ExceptionUtils.createWrongDataMessage(listOf("from_opinion" to "has wrong value"))

        assertEquals("'FromOpinion' has wrong value", result)
    }

    @Test
    fun createWrongDataMessage_renamedColumn_usesModelFieldName() {
        val result = ExceptionUtils.createWrongDataMessage(listOf("udt_value" to null))

        assertEquals("'Value' has wrong value", result)
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
    fun transformNotUniqueDataMessage_uniqueConstraint_returnsFieldMessage() {
        val message = "duplicate key value violates unique constraint \"uk_username\""

        assertEquals("'Username' already exists", message.transformNotUniqueDataMessage())
    }

    @Test
    fun transformNotUniqueDataMessage_compositeUniqueConstraint_returnsCombinationMessage() {
        val message = "duplicate key value violates unique constraint \"uk_category_id__user_id\""

        assertEquals("Combination of 'CategoryId', 'UserId' already exists", message.transformNotUniqueDataMessage())
    }

    @Test
    fun transformNotUniqueDataMessage_otherConstraint_returnsNull() {
        val message = "duplicate key value violates unique constraint \"idx_user_categories\""

        assertNull(message.transformNotUniqueDataMessage())
    }

    @Test
    fun transformNotUniqueDataMessage_noConstraintMatch_returnsNull() {
        assertNull("totally unrelated error message".transformNotUniqueDataMessage())
    }

    @Test
    fun transformWrongDataMessage_checkConstraint_returnsFieldMessage() {
        val message = "new row for relation \"users\" violates check constraint \"users_activity_check\""

        assertEquals("'Activity' has wrong value", message.transformWrongDataMessage())
    }

    @Test
    fun transformWrongDataMessage_notNullConstraint_returnsFieldMessage() {
        val message = "null value in column \"city\" of relation \"users\" violates not-null constraint"

        assertEquals("'City' must not be null", message.transformWrongDataMessage())
    }

    @Test
    fun transformWrongDataMessage_otherMessage_returnsNull() {
        assertNull("totally unrelated error message".transformWrongDataMessage())
    }
}
