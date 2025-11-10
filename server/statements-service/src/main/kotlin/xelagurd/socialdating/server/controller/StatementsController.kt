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
import xelagurd.socialdating.server.model.Statement
import xelagurd.socialdating.server.model.details.StatementDetails
import xelagurd.socialdating.server.service.StatementsService

@RestController
@RequestMapping(path = ["/statements"], produces = ["application/json"])
class StatementsController(
    private val statementsService: StatementsService
) {

    @Operation(security = [SecurityRequirement("bearerAuth")])
    @GetMapping
    fun getStatements(
        @RequestParam userId: Int,
        @RequestParam definingThemeIds: List<Int>
    ): List<Statement> {
        return statementsService.getStatements(userId, definingThemeIds)
    }

    @Operation(security = [SecurityRequirement("bearerAuth")])
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addStatement(@RequestBody @Valid statementDetails: StatementDetails): Statement {
        return statementsService.addStatement(statementDetails)
    }
}