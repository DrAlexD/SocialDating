package xelagurd.socialdating.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import xelagurd.socialdating.dto.User
import xelagurd.socialdating.service.UsersService


@RestController
@RequestMapping(path = ["/api/v1/users"], produces = ["application/json"])
class UsersController(
    private val usersService: UsersService
) {

    @GetMapping("/{id}")
    fun getUser(@PathVariable("id") userId: Int): User? {
        return usersService.getUser(userId)
    }
}