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
import xelagurd.socialdating.server.model.UserStatement
import xelagurd.socialdating.server.model.additional.StatementReactionDetails
import xelagurd.socialdating.server.model.details.UserStatementDetails
import xelagurd.socialdating.server.security.AdminAccess
import xelagurd.socialdating.server.service.UserStatementsService

@RestController
@RequestMapping(path = ["/api/v1/statements/users"], produces = ["application/json"])
class UserStatementsController(
    private val userStatementsService: UserStatementsService
) {

    @GetMapping
    fun getUserStatements(
        @RequestParam userId: Int,
        @RequestParam definingThemeIds: List<Int>
    ): List<UserStatement> {
        return userStatementsService.getUserStatements(userId, definingThemeIds)
    }

    @AdminAccess
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addUserStatement(@RequestBody @Valid userStatementDetails: UserStatementDetails): UserStatement {
        return userStatementsService.addUserStatement(userStatementDetails)
    }

    @PostMapping("/reaction")
    @ResponseStatus(HttpStatus.CREATED)
    fun addStatementReaction(@RequestBody statementReactionDetails: StatementReactionDetails): UserStatement {
        return userStatementsService.addStatementReaction(statementReactionDetails)
    }
}