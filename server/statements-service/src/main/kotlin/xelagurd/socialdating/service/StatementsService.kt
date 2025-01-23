package xelagurd.socialdating.service

import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestBody
import xelagurd.socialdating.dto.Statement
import xelagurd.socialdating.dto.StatementDetails
import xelagurd.socialdating.repository.StatementsRepository

@Service
class StatementsService(
    private val statementsRepository: StatementsRepository
) {

    fun getStatements(definingThemeIds: List<Int>): Iterable<Statement> {
        return statementsRepository.findAllByDefiningThemeIdIn(definingThemeIds)
    }

    fun addStatement(@RequestBody statementDetails: StatementDetails): Statement {
        return statementsRepository.save(statementDetails.toStatement())
    }
}