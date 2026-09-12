package xelagurd.socialdating.server.service

import org.springframework.data.domain.Limit
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import xelagurd.socialdating.server.exception.InvalidDataException
import xelagurd.socialdating.server.model.DefaultDataProperties.PAGE_SIZE_DEFAULT
import xelagurd.socialdating.server.model.DefaultDataProperties.PAGE_SIZE_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.PAGE_SIZE_MIN
import xelagurd.socialdating.server.model.StatementsCursor
import xelagurd.socialdating.server.model.details.StatementDetails
import xelagurd.socialdating.server.model.dto.PageDto
import xelagurd.socialdating.server.model.dto.StatementDto
import xelagurd.socialdating.server.model.enums.AppLanguage
import xelagurd.socialdating.server.repository.StatementDefiningThemesRepository
import xelagurd.socialdating.server.repository.StatementsRepository
import xelagurd.socialdating.server.utils.SecurityUtils.checkCurrentUserAuth

@Service
class StatementsService(
    private val statementsRepository: StatementsRepository,
    private val statementDefiningThemesRepository: StatementDefiningThemesRepository
) {

    fun getStatements(
        currentUserId: Int,
        definingThemeIds: List<Int>,
        cursor: String? = null,
        size: Int = PAGE_SIZE_DEFAULT
    ): PageDto<StatementDto> {
        checkCurrentUserAuth(currentUserId)

        val statementsCursor = StatementsCursor.decodeOrNew(cursor)
        val pageSize = size.coerceIn(PAGE_SIZE_MIN, PAGE_SIZE_MAX)

        val statements = statementsRepository.findUnreactedStatements(
            currentUserId,
            definingThemeIds,
            statementsCursor.seed,
            statementsCursor.lastOrderKey,
            Limit.of(pageSize)
        )

        if (statements.isEmpty()) return PageDto(listOf())

        val language = AppLanguage.current()

        val definingThemesByStatementId = statementDefiningThemesRepository
            .findAllByStatementIdIn(statements.map { it.id!! })
            .groupBy { it.statementId }

        return PageDto(
            content = statements.map { it.toStatementDto(definingThemesByStatementId[it.id] ?: listOf(), language) },
            nextCursor = when {
                statements.size < pageSize -> null
                else -> statementsCursor.next(statements.last().id!!).encode()
            }
        )
    }

    @Transactional
    fun addStatement(statementDetails: StatementDetails): StatementDto {
        val definingThemeIds = statementDetails.definingThemes.map { it.definingThemeId }

        if (definingThemeIds.size != definingThemeIds.toSet().size) {
            throw InvalidDataException("error.statement.duplicatedDefiningThemes")
        }

        val statement = statementsRepository.save(statementDetails.toStatement())
        val definingThemes = statementDefiningThemesRepository
            .saveAll(statementDetails.toStatementDefiningThemes(statement.id!!))

        return statement.toStatementDto(definingThemes)
    }
}
