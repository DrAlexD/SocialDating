package xelagurd.socialdating.server.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import xelagurd.socialdating.server.model.DefaultDataProperties.CATEGORY_NAME_LENGTH_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.CATEGORY_NAME_LENGTH_MIN
import xelagurd.socialdating.server.model.DefaultDataProperties.ID_MIN
import xelagurd.socialdating.server.model.additional.CategoryResponse
import xelagurd.socialdating.server.model.enums.AppLanguage
import xelagurd.socialdating.server.utils.LocalizationUtils.localize

@Entity(name = "categories")
@Table(
    name = "categories",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_name_en", columnNames = ["name_en"]),
        UniqueConstraint(name = "uk_name_ru", columnNames = ["name_ru"]),
        UniqueConstraint(name = "uk_order_number", columnNames = ["order_number"])
    ]
)
class Category(
    @field:Id
    @field:GeneratedValue(GenerationType.IDENTITY)
    var id: Int? = null,

    @field:Column(
        nullable = false,
        columnDefinition = "varchar($CATEGORY_NAME_LENGTH_MAX) " +
                "check (length(trim(name_en)) between $CATEGORY_NAME_LENGTH_MIN and $CATEGORY_NAME_LENGTH_MAX)"
    )
    var nameEn: String,

    @field:Column(
        nullable = false,
        columnDefinition = "varchar($CATEGORY_NAME_LENGTH_MAX) " +
                "check (length(trim(name_ru)) between $CATEGORY_NAME_LENGTH_MIN and $CATEGORY_NAME_LENGTH_MAX)"
    )
    var nameRu: String,

    @field:Column(nullable = false, columnDefinition = "integer check (order_number >= $ID_MIN)")
    var orderNumber: Int
) {

    fun toCategoryResponse(language: AppLanguage = AppLanguage.current()) =
        CategoryResponse(
            id = id!!,
            name = localize(nameEn, nameRu, language),
            orderNumber = orderNumber
        )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Category

        if (id != other.id) return false
        if (orderNumber != other.orderNumber) return false
        if (nameEn != other.nameEn) return false
        if (nameRu != other.nameRu) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + orderNumber
        result = 31 * result + nameEn.hashCode()
        result = 31 * result + nameRu.hashCode()
        return result
    }
}
