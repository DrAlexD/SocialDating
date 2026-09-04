package xelagurd.socialdating.server.exception

import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import xelagurd.socialdating.server.utils.GatewayLocalizedMessages

// unhandled exceptions never reach a controller advice in the gateway, so the default error body is localized here
@Component
class LocalizedErrorAttributes : DefaultErrorAttributes() {

    private val messages = GatewayLocalizedMessages()

    override fun getErrorAttributes(
        request: ServerRequest,
        options: ErrorAttributeOptions
    ): MutableMap<String, Any> {
        val attributes = super.getErrorAttributes(request, options)
        val status = attributes[STATUS_ATTRIBUTE] as? Int ?: return attributes

        messages.getOrNull("$STATUS_KEY_PREFIX$status", request.headers().asHttpHeaders())
            ?.let { attributes[ERROR_ATTRIBUTE] = it }

        return attributes
    }

    private companion object {
        const val STATUS_ATTRIBUTE = "status"
        const val ERROR_ATTRIBUTE = "error"
        const val STATUS_KEY_PREFIX = "error.status."
    }
}
