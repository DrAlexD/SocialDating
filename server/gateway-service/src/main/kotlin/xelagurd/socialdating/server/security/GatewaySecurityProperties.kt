package xelagurd.socialdating.server.security

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "security")
data class GatewaySecurityProperties(
    var whitelist: List<String> = emptyList()
)