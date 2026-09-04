package xelagurd.socialdating.server.model.repository

import xelagurd.socialdating.server.model.enums.AppLanguage
import xelagurd.socialdating.server.utils.LocalizationUtils.localize

data class CategoryWithData(
    val id: Int,
    val nameEn: String,
    val nameRu: String,
    val maintained: Array<Long>?,
    val notMaintained: Array<Long>?
) {

    fun getLocalizedName(language: AppLanguage) = localize(nameEn, nameRu, language)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CategoryWithData

        if (id != other.id) return false
        if (nameEn != other.nameEn) return false
        if (nameRu != other.nameRu) return false
        if (!maintained.contentEquals(other.maintained)) return false
        if (!notMaintained.contentEquals(other.notMaintained)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + nameEn.hashCode()
        result = 31 * result + nameRu.hashCode()
        result = 31 * result + maintained.contentHashCode()
        result = 31 * result + notMaintained.contentHashCode()
        return result
    }
}
