package xelagurd.socialdating.server.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import xelagurd.socialdating.server.model.User
import xelagurd.socialdating.server.service.UsersService


@RestController
@RequestMapping(path = ["/users"], produces = ["application/json"])
class UsersController(
    private val usersService: UsersService
) {

    @Operation(security = [SecurityRequirement("bearerAuth")])
    @GetMapping("/{id}")
    fun getUser(@PathVariable("id") userId: Int): User {
        return usersService.getUser(userId)
    }
}