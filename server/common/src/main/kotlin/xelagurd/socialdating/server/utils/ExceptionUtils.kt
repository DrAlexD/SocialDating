package xelagurd.socialdating.server.utils

object ExceptionUtils {

    fun createWrongDataMessage(errorList: List<Pair<String, String?>>) =
        errorList
            .map {
                "'${it.first.replaceFirstChar { it.titlecase() }}' ${it.second ?: "has wrong value"}"
            }
            .sorted()
            .joinToString(separator = "; ")

    fun String.transformNotUniqueDataMessage(): String? {
        val tableRegex = Regex("ON PUBLIC\\.(.*?)\\((.*?) ")
        val tableMatchResult = tableRegex.find(this)

        val rawTableName = tableMatchResult?.groups?.get(1)?.value ?: return null
        val rawFieldName = tableMatchResult?.groups?.get(2)?.value ?: return null

        val tableName = singularizeTableName(rawTableName)
        val fieldName = rawFieldName.lowercase()

        val valueRegex = Regex("VALUES \\(.*?'(.*?)' \\)")
        val valueMatchResult = valueRegex.find(this)
        val violatingValue = valueMatchResult?.groups?.get(1)?.value ?: return null

        return "$tableName with '$violatingValue' $fieldName already exists"
    }

    private fun singularizeTableName(table: String): String {
        val lower = table.lowercase().replace('_', ' ')

        val singular = when {
            lower.endsWith("ies") -> lower.dropLast(3) + "y"
            lower.endsWith("s") -> lower.dropLast(1)
            else -> lower
        }

        return singular.replaceFirstChar { it.uppercase() }
    }
}