package io.codecrafters.command.type

import io.codecrafters.command.CommandHandler
import io.codecrafters.dto.CommandNames
import org.springframework.stereotype.Component

@Component
class TypeCommandHandler(
    private val executableLocator: ExecutableLocator,
    private val commandNames: CommandNames,
) : CommandHandler {
    override val commandName: String = "type"

    override fun handle(commandPayload: String) {
        val requestedCommand = commandPayload.trim()
        if (requestedCommand.isEmpty()) {
            println("type: missing operand")
            return
        }

        if (requestedCommand in commandNames.commandNames) {
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
