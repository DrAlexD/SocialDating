package xelagurd.socialdating.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import xelagurd.socialdating.dto.Statement
import xelagurd.socialdating.dto.StatementDetails
import xelagurd.socialdating.dto.StatementReactionDetails
import xelagurd.socialdating.service.StatementsService

@RestController
@RequestMapping(path = ["/api/v1/statements"], produces = ["application/json"])
class StatementsController(
    private val statementsService: StatementsService
) {

    @GetMapping
    fun getStatements(@RequestParam definingThemeIds: List<Int>): Iterable<Statement> {
        return statementsService.getStatements(definingThemeIds)
    }

    @PostMapping
    fun addStatement(@RequestBody statementDetails: StatementDetails): Statement {
        return statementsService.addStatement(statementDetails)
    }

    @PostMapping("/{id}/reaction")
    fun addStatementReaction(
        @PathVariable("id") statementId: Int,
        @RequestBody statementReactionDetails: StatementReactionDetails
    ) {
        statementsService.addStatementReaction(statementId, statementReactionDetails)
    }
}