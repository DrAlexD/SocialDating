package xelagurd.socialdating.server.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.Valid
import xelagurd.socialdating.server.model.details.StatementDetails
import xelagurd.socialdating.server.security.BearerAuth
import xelagurd.socialdating.server.service.StatementsService
import xelagurd.socialdating.server.utils.DataUtils.responseEntities

@RestController
@RequestMapping(path = ["/statements"], produces = ["application/json"])
class StatementsController(
    private val statementsService: StatementsService
) {

    @BearerAuth
    @GetMapping
    fun getStatements(
        @RequestParam userId: Int,
        @RequestParam definingThemeIds: List<Int>
    ) =
        responseEntities { statementsService.getStatements(userId, definingThemeIds) }

    @BearerAuth
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addStatement(@RequestBody @Valid statementDetails: StatementDetails) =
        statementsService.addStatement(statementDetails)
}