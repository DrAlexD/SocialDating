package xelagurd.socialdating.server.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.Valid
import xelagurd.socialdating.server.model.Statement
import xelagurd.socialdating.server.model.additional.StatementReactionDetails
import xelagurd.socialdating.server.model.details.StatementDetails
import xelagurd.socialdating.server.service.StatementsService

@RestController
@RequestMapping(path = ["/api/v1/statements"], produces = ["application/json"])
class StatementsController(
    private val statementsService: StatementsService
) {

    @GetMapping
    fun getStatements(@RequestParam definingThemeIds: List<Int>): List<Statement> {
        return statementsService.getStatements(definingThemeIds)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addStatement(@RequestBody @Valid statementDetails: StatementDetails): Statement {
        return statementsService.addStatement(statementDetails)
    }

    @PostMapping("/{id}/reaction")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun addStatementReaction(
        @PathVariable("id") statementId: Int,
        @RequestBody statementReactionDetails: StatementReactionDetails
    ) {
        statementsService.addStatementReaction(statementId, statementReactionDetails)
    }
}