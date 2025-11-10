package xelagurd.socialdating.server.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import xelagurd.socialdating.server.model.UserStatement
import xelagurd.socialdating.server.model.additional.StatementReactionDetails
import xelagurd.socialdating.server.model.details.UserStatementDetails
import xelagurd.socialdating.server.security.AdminAccess
import xelagurd.socialdating.server.service.UserStatementsService

@RestController
@RequestMapping(path = ["/statements/users"], produces = ["application/json"])
class UserStatementsController(
    private val userStatementsService: UserStatementsService
) {

    @Operation(security = [SecurityRequirement("bearerAuth")])
    @GetMapping
    fun getUserStatements(
        @RequestParam userId: Int,
        @RequestParam definingThemeIds: List<Int>
    ): List<UserStatement> {
        return userStatementsService.getUserStatements(userId, definingThemeIds)
    }

    @Operation(security = [SecurityRequirement("bearerAuth")])
    @AdminAccess
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addUserStatement(@RequestBody @Valid userStatementDetails: UserStatementDetails): UserStatement {
        return userStatementsService.addUserStatement(userStatementDetails)
    }

    @Operation(security = [SecurityRequirement("bearerAuth")])
    @PostMapping("/reaction")
    @ResponseStatus(HttpStatus.CREATED)
    fun processStatementReaction(@RequestBody @Valid statementReactionDetails: StatementReactionDetails): UserStatement {
        return userStatementsService.processStatementReaction(statementReactionDetails)
    }
}