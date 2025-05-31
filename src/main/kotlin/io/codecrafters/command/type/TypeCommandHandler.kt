package io.codecrafters.command.type

import io.codecrafters.BUILTIN_COMMANDS
import io.codecrafters.command.CommandHandler
import org.springframework.stereotype.Component

@Component
class TypeCommandHandler(
    private val executableLocator: ExecutableLocator,
) : CommandHandler {
    override val commandName: String = "type"

    override fun handle(commandPayload: String) {
        val requestedCommand = commandPayload.trim()
        if (requestedCommand.isEmpty()) {
            println("type: missing operand")
            return
        }

        if (requestedCommand in BUILTIN_COMMANDS) {
            println("$requestedCommand is a shell builtin")
            return
        }

        val lookupResult = executableLocator.findExecutable(requestedCommand)

        when (lookupResult) {
            is ExecutableFound -> println("$requestedCommand is ${lookupResult.absolutePath}")
            ExecutableNotFound -> println("$requestedCommand: not found")
        }
    }
}
