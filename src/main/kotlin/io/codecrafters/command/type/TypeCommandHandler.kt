package io.codecrafters.command.type

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

        val executablePath = executableLocator.findExecutable(requestedCommand)
        if (executablePath != null) {
            println("$requestedCommand is $executablePath")
        } else {
            println("$requestedCommand: not found")
        }
    }

    companion object {
        private val BUILTIN_COMMANDS = setOf("echo", "exit", "type", "pwd")
    }
}
