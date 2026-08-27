package xelagurd.socialdating.server.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import xelagurd.socialdating.server.model.additional.UserData

@FeignClient(
    name = "users-service",
    url = "http://\${services.users.host}:\${services.users.port}"
)
interface UsersServiceClient {

    @GetMapping("/users")
    fun getUsers(@RequestParam userIds: List<Int>): List<UserData>
}