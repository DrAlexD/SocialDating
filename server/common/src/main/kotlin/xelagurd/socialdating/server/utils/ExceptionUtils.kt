package xelagurd.socialdating.server.utils

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.MismatchedInputException

object ExceptionUtils {

    private const val FIELDS_SEPARATOR = "__"
    private const val APP_PACKAGE_PREFIX = "xelagurd.socialdating"

    // only for error messages, database column names stay as is
    private val renamedColumns = mapOf("udt_value" to "value")

    fun getErrorPositionFromStackTrace(stackTrace: Array<StackTraceElement>) =
        (stackTrace.firstOrNull { it.className.startsWith(APP_PACKAGE_PREFIX) } ?: stackTrace.firstOrNull())
            ?.let { "${it.className}.${it.methodName}(${it.fileName}:${it.lineNumber})" }
            ?: "Unknown location"

    fun createWrongDataMessage(errorList: List<Pair<String, String?>>) =
        errorList
            .map { (fieldName, message) -> "${fieldName.toQuotedFieldName()} ${message ?: "has wrong value"}" }
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

    fun String.transformWrongDataMessage(): String? {
        val checkRegex = Regex("relation \"(.*?)\" violates check constraint \"(.*?)\"")
        val checkMatchResult = checkRegex.find(this)

        if (checkMatchResult != null) {
            val tableName = checkMatchResult.groupValues[1]
            val fieldName = checkMatchResult.groupValues[2]
                .removePrefix("${tableName}_")
                .removeSuffix("_check")

            return createWrongDataMessage(listOf(fieldName to null))
        }

        val notNullRegex = Regex("null value in column \"(.*?)\"")
        val notNullMatchResult = notNullRegex.find(this) ?: return null

        return createWrongDataMessage(listOf(notNullMatchResult.groupValues[1] to "must not be null"))
    }

    fun String.transformNotUniqueDataMessage(): String? {
        val uniqueRegex = Regex("violates unique constraint \"uk_(.*?)\"")
        val uniqueMatchResult = uniqueRegex.find(this) ?: return null

        val fieldNames = uniqueMatchResult.groupValues[1].split(FIELDS_SEPARATOR)

        if (fieldNames.size == 1) return createWrongDataMessage(listOf(fieldNames.first() to "already exists"))

        return "Combination of ${fieldNames.joinToString { it.toQuotedFieldName() }} already exists"
    }

    fun MismatchedInputException.transformWrongInputMessage(): String? {
        val fieldName = path.lastOrNull { it.fieldName != null }?.fieldName ?: return null
        val message = if (this is InvalidFormatException) null else "must not be null"

        return createWrongDataMessage(listOf(fieldName to message))
    }
}
