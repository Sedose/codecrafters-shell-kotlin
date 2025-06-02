package io.codecrafters

import io.codecrafters.command.CommandHandler
import io.codecrafters.dto.ExternalProgramNotFound
import io.codecrafters.dto.ExternalProgramSuccess
import io.codecrafters.external.ExternalProgramExecutor
import io.codecrafters.parser.CommandParser
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class ShellRunner(
    private val commandHandlerMap: Map<String, CommandHandler>,
    private val externalProgramExecutor: ExternalProgramExecutor,
    private val commandParser: CommandParser,
) : CommandLineRunner {
    override fun run(vararg args: String) {
        while (true) {
            print("$ ")
            val inputLine = readLine() ?: break
            val trimmedInput = inputLine.trim()
            if (trimmedInput.isEmpty()) {
                continue
            }
            val (commandName, arguments) = commandParser.parse(trimmedInput)
            commandHandlerMap[commandName]
                ?.handle(arguments)
                ?: handleExternalCommand(commandName, arguments)
        }
    }

    private fun handleExternalCommand(
        commandName: String,
        arguments: List<String>,
    ) {
        when (externalProgramExecutor.execute(commandName, arguments)) {
            is ExternalProgramNotFound -> println("$commandName: not found")
            is ExternalProgramSuccess -> {
                // no-op
            }
        }
    }
}
