package xelagurd.socialdating.server.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.Valid
import xelagurd.socialdating.server.model.additional.StatementReactionDetails
import xelagurd.socialdating.server.security.BearerAuth
import xelagurd.socialdating.server.service.UserStatementsService

@RestController
@RequestMapping(path = ["/statements/users"], produces = ["application/json"])
class UserStatementsController(
    private val userStatementsService: UserStatementsService
) {

    @BearerAuth
    @PostMapping("/reaction")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun processStatementReaction(@RequestBody @Valid statementReactionDetails: StatementReactionDetails) =
        userStatementsService.processStatementReaction(statementReactionDetails)

}