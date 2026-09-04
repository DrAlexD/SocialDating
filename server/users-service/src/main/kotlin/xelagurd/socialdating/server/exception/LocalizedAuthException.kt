package xelagurd.socialdating.server.exception

// the exception message stays in English for the logs, the key is what reaches the client
interface LocalizedAuthException {
    val messageKey: String
}
