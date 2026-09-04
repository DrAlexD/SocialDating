package xelagurd.socialdating.server.model

import org.hibernate.annotations.Check
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import xelagurd.socialdating.server.model.DefaultDataProperties.ID_MIN
import xelagurd.socialdating.server.model.DefaultDataProperties.STATEMENT_TEXT_LENGTH_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.STATEMENT_TEXT_LENGTH_MIN
import xelagurd.socialdating.server.model.additional.StatementWithDefiningThemes
import xelagurd.socialdating.server.model.enums.AppLanguage
import xelagurd.socialdating.server.utils.LocalizationUtils.localize

@Entity(name = "statements")
@Table(
    name = "statements",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_text_en", columnNames = ["text_en"]),
        UniqueConstraint(name = "uk_text_ru", columnNames = ["text_ru"])
    ]
)
@Check(name = "ck_text", constraints = "text_en is not null or text_ru is not null")
class Statement(
    @field:Id
    @field:GeneratedValue(GenerationType.IDENTITY)
    var id: Int? = null,

    @field:Column(
        columnDefinition = "varchar($STATEMENT_TEXT_LENGTH_MAX) " +
                "check (length(trim(text_en)) between $STATEMENT_TEXT_LENGTH_MIN and $STATEMENT_TEXT_LENGTH_MAX)"
    )
    var textEn: String? = null,

    @field:Column(
        columnDefinition = "varchar($STATEMENT_TEXT_LENGTH_MAX) " +
                "check (length(trim(text_ru)) between $STATEMENT_TEXT_LENGTH_MIN and $STATEMENT_TEXT_LENGTH_MAX)"
    )
    var textRu: String? = null,

    @field:Column(nullable = false, columnDefinition = "integer check (creator_user_id >= $ID_MIN)")
    var creatorUserId: Int
) {

    fun toStatementWithDefiningThemes(
        definingThemes: List<StatementDefiningTheme>,
        language: AppLanguage = AppLanguage.current()
    ) =
        StatementWithDefiningThemes(
            id = id!!,
            text = localize(textEn, textRu, language),
            creatorUserId = creatorUserId,
            definingThemes = definingThemes.map { it.toDefiningThemeReactionDetails() }
        )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Statement

        if (id != other.id) return false
        if (creatorUserId != other.creatorUserId) return false
        if (textEn != other.textEn) return false
        if (textRu != other.textRu) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + creatorUserId
        result = 31 * result + textEn.hashCode()
        result = 31 * result + textRu.hashCode()
        return result
    }
}
