package xelagurd.socialdating.server.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_NAME_LENGTH_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_NAME_LENGTH_MIN
import xelagurd.socialdating.server.model.DefaultDataProperties.ID_MIN
import xelagurd.socialdating.server.model.DefaultDataProperties.OPINION_LENGTH_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.OPINION_LENGTH_MIN
import xelagurd.socialdating.server.model.additional.DefiningThemeResponse
import xelagurd.socialdating.server.model.enums.AppLanguage
import xelagurd.socialdating.server.utils.LocalizationUtils.localize

@Entity(name = "defining_themes")
@Table(
    name = "defining_themes",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_name_en", columnNames = ["name_en"]),
        UniqueConstraint(name = "uk_name_ru", columnNames = ["name_ru"]),
        UniqueConstraint(
            name = "uk_category_id__number_in_category",
            columnNames = ["category_id", "number_in_category"]
        ),
        UniqueConstraint(
            name = "uk_category_id__order_number",
            columnNames = ["category_id", "order_number"]
        )
    ]
)
class DefiningTheme(
    @field:Id
    @field:GeneratedValue(GenerationType.IDENTITY)
    var id: Int? = null,

    @field:Column(
        nullable = false,
        columnDefinition = "varchar($DEFINING_THEME_NAME_LENGTH_MAX) " +
                "check (length(trim(name_en)) between $DEFINING_THEME_NAME_LENGTH_MIN " +
                "and $DEFINING_THEME_NAME_LENGTH_MAX)"
    )
    var nameEn: String,

    @field:Column(
        nullable = false,
        columnDefinition = "varchar($DEFINING_THEME_NAME_LENGTH_MAX) " +
                "check (length(trim(name_ru)) between $DEFINING_THEME_NAME_LENGTH_MIN " +
                "and $DEFINING_THEME_NAME_LENGTH_MAX)"
    )
    var nameRu: String,

    @field:Column(
        nullable = false,
        columnDefinition = "varchar($OPINION_LENGTH_MAX) " +
                "check (length(trim(from_opinion_en)) between $OPINION_LENGTH_MIN and $OPINION_LENGTH_MAX)"
    )
    var fromOpinionEn: String,

    @field:Column(
        nullable = false,
        columnDefinition = "varchar($OPINION_LENGTH_MAX) " +
                "check (length(trim(from_opinion_ru)) between $OPINION_LENGTH_MIN and $OPINION_LENGTH_MAX)"
    )
    var fromOpinionRu: String,

    @field:Column(
        nullable = false,
        columnDefinition = "varchar($OPINION_LENGTH_MAX) " +
                "check (length(trim(to_opinion_en)) between $OPINION_LENGTH_MIN and $OPINION_LENGTH_MAX)"
    )
    var toOpinionEn: String,

    @field:Column(
        nullable = false,
        columnDefinition = "varchar($OPINION_LENGTH_MAX) " +
                "check (length(trim(to_opinion_ru)) between $OPINION_LENGTH_MIN and $OPINION_LENGTH_MAX)"
    )
    var toOpinionRu: String,

    @field:Column(nullable = false, columnDefinition = "integer check (category_id >= $ID_MIN)")
    var categoryId: Int,

    @field:Column(nullable = false, columnDefinition = "integer check (number_in_category >= $ID_MIN)")
    var numberInCategory: Int,

    @field:Column(nullable = false, columnDefinition = "integer check (order_number >= $ID_MIN)")
    var orderNumber: Int = numberInCategory
) {

    fun toDefiningThemeResponse(language: AppLanguage = AppLanguage.current()) =
        DefiningThemeResponse(
            id = id!!,
            name = localize(nameEn, nameRu, language),
            fromOpinion = localize(fromOpinionEn, fromOpinionRu, language),
            toOpinion = localize(toOpinionEn, toOpinionRu, language),
            categoryId = categoryId,
            numberInCategory = numberInCategory,
            orderNumber = orderNumber
        )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DefiningTheme

        if (id != other.id) return false
        if (categoryId != other.categoryId) return false
        if (numberInCategory != other.numberInCategory) return false
        if (orderNumber != other.orderNumber) return false
        if (nameEn != other.nameEn) return false
        if (nameRu != other.nameRu) return false
        if (fromOpinionEn != other.fromOpinionEn) return false
        if (fromOpinionRu != other.fromOpinionRu) return false
        if (toOpinionEn != other.toOpinionEn) return false
        if (toOpinionRu != other.toOpinionRu) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + categoryId
        result = 31 * result + numberInCategory
        result = 31 * result + orderNumber
        result = 31 * result + nameEn.hashCode()
        result = 31 * result + nameRu.hashCode()
        result = 31 * result + fromOpinionEn.hashCode()
        result = 31 * result + fromOpinionRu.hashCode()
        result = 31 * result + toOpinionEn.hashCode()
        result = 31 * result + toOpinionRu.hashCode()
        return result
    }
}
