package io.codecrafters.command

interface CommandHandler {
    val commandName: String

    fun handle(commandPayload: String)
}
