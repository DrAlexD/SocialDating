package xelagurd.socialdating.server.utils

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.MismatchedInputException

object ExceptionUtils {

    private const val FIELDS_SEPARATOR = "__"
    private const val CHECK_CONSTRAINT_PREFIX = "ck_"
    private const val APP_PACKAGE_PREFIX = "xelagurd.socialdating"

    private const val WRONG_VALUE_KEY = "error.field.wrongValue"
    private const val MUST_NOT_BE_NULL_KEY = "error.field.mustNotBeNull"
    private const val ALREADY_EXISTS_KEY = "error.field.alreadyExists"
    private const val COMBINATION_ALREADY_EXISTS_KEY = "error.fields.combinationAlreadyExists"

    // only for error messages, database column names stay as is
    private val renamedColumns = mapOf("udt_value" to "value") +
        listOf("name", "text", "city", "from_opinion", "to_opinion")
            .flatMap { listOf("${it}_en" to it, "${it}_ru" to it) }

    fun getErrorPositionFromStackTrace(stackTrace: Array<StackTraceElement>) =
        (stackTrace.firstOrNull { it.className.startsWith(APP_PACKAGE_PREFIX) } ?: stackTrace.firstOrNull())
            ?.let { "${it.className}.${it.methodName}(${it.fileName}:${it.lineNumber})" }
            ?: "Unknown location"

    fun createWrongDataMessage(errorList: List<Pair<String, String?>>, messages: LocalizedMessages) =
        errorList
            .map { (fieldName, message) ->
                "${fieldName.toQuotedFieldName()} ${message ?: messages.get(WRONG_VALUE_KEY)}"
            }
            .sorted()
            .joinToString(separator = "; ")

    private fun String.toQuotedFieldName(): String {
        val camelCaseFieldName = (renamedColumns[this] ?: this)
            .split('_')
            .filter { it.isNotEmpty() }
            .mapIndexed { index, part -> if (index == 0) part else part.replaceFirstChar { it.titlecase() } }
            .joinToString(separator = "")

        return "'${camelCaseFieldName.replaceFirstChar { it.titlecase() }}'"
    }

    fun String.transformWrongDataMessage(messages: LocalizedMessages): String? {
        val checkRegex = Regex("relation \"(.*?)\" violates check constraint \"(.*?)\"")
        val checkMatchResult = checkRegex.find(this)

        if (checkMatchResult != null) {
            val tableName = checkMatchResult.groupValues[1]
            val fieldName = checkMatchResult.groupValues[2]
                .removePrefix("${tableName}_")
                .removePrefix(CHECK_CONSTRAINT_PREFIX)
                .removeSuffix("_check")

            return createWrongDataMessage(listOf(fieldName to null), messages)
        }

        val notNullRegex = Regex("null value in column \"(.*?)\"")
        val notNullMatchResult = notNullRegex.find(this) ?: return null

        return createWrongDataMessage(
            listOf(notNullMatchResult.groupValues[1] to messages.get(MUST_NOT_BE_NULL_KEY)),
            messages
        )
    }

    fun String.transformNotUniqueDataMessage(messages: LocalizedMessages): String? {
        val uniqueRegex = Regex("violates unique constraint \"uk_(.*?)\"")
        val uniqueMatchResult = uniqueRegex.find(this) ?: return null

        val fieldNames = uniqueMatchResult.groupValues[1].split(FIELDS_SEPARATOR)

        if (fieldNames.size == 1) {
            return createWrongDataMessage(
                listOf(fieldNames.first() to messages.get(ALREADY_EXISTS_KEY)),
                messages
            )
        }

        return messages.get(COMBINATION_ALREADY_EXISTS_KEY, fieldNames.joinToString { it.toQuotedFieldName() })
    }

    fun MismatchedInputException.transformWrongInputMessage(messages: LocalizedMessages): String? {
        val fieldName = path.lastOrNull { it.fieldName != null }?.fieldName ?: return null
        val message = if (this is InvalidFormatException) null else messages.get(MUST_NOT_BE_NULL_KEY)

        return createWrongDataMessage(listOf(fieldName to message), messages)
    }
}
