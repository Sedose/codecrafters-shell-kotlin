package io.codecrafters

import io.codecrafters.command.CommandHandler
import io.codecrafters.dto.ExternalProgramNotFound
import io.codecrafters.dto.ExternalProgramSuccess
import io.codecrafters.external.ExternalProgramExecutor
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class ShellRunner(
    private val commandHandlerMap: Map<String, CommandHandler>,
    private val externalProgramExecutor: ExternalProgramExecutor,
) : CommandLineRunner {

    override fun run(vararg args: String) {
        while (true) {
            print("$ ")
            val inputLine = readLine() ?: break
            val trimmedInput = inputLine.trim()
            if (trimmedInput.isEmpty()) {
                continue
            }
            val commandName = trimmedInput.substringBefore(" ")
            val commandPayload = trimmedInput.substringAfter(" ")
            commandHandlerMap[commandName]
                ?.handle(commandPayload)
                ?: handleExternalCommand(commandName, commandPayload)
        }
    }

    private fun handleExternalCommand(
        commandName: String,
        commandPayload: String,
    ) {
        when (externalProgramExecutor.execute(commandName, commandPayload)) {
            is ExternalProgramNotFound -> println("$commandName: not found")
            is ExternalProgramSuccess -> {
                // no-op
            }
        }
    }
}
