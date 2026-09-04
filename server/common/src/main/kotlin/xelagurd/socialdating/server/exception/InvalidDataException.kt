package xelagurd.socialdating.server.exception

class InvalidDataException(
    val messageKey: String,
    vararg val messageArgs: Any
) : IllegalArgumentException(messageKey)
