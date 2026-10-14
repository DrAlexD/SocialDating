package xelagurd.socialdating.server.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import xelagurd.socialdating.server.model.details.UsersFilterDetails
import xelagurd.socialdating.server.security.BearerAuth
import xelagurd.socialdating.server.service.UsersService
import xelagurd.socialdating.server.utils.DataUtils.responseEntities
import xelagurd.socialdating.server.utils.DataUtils.responseEntity


@RestController
@RequestMapping(path = ["/users"], produces = ["application/json"])
class UsersController(
    private val usersService: UsersService
) {

    @BearerAuth
    @GetMapping("/{id}")
    fun getUser(@PathVariable("id") userId: Int) =
        responseEntity { usersService.getUser(userId) }

    @BearerAuth
    @GetMapping
    fun getUsers(@RequestParam userIds: List<Int>) =
        responseEntities { usersService.getUsers(userIds) }

    @BearerAuth
    @GetMapping("/ids")
    fun getUserIds(@Valid @ParameterObject usersFilterDetails: UsersFilterDetails) =
        responseEntities { usersService.getUserIds(usersFilterDetails) }
}