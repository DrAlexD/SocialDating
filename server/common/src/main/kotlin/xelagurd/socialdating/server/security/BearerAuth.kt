package xelagurd.socialdating.server.security

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Operation(security = [SecurityRequirement("bearerAuth")])
annotation class BearerAuth