package io.codecrafters

import io.codecrafters.command.CommandHandler
import io.codecrafters.external.ExternalProgramExecutor
import io.codecrafters.external.ExternalProgramNotFound
import io.codecrafters.external.ExternalProgramSuccess
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.util.regex.Pattern

@Component
class ShellRunner(
    private val commandHandlerMap: Map<String, CommandHandler>,
    private val externalProgramExecutor: ExternalProgramExecutor,
) : CommandLineRunner {
    private val argumentSplitPattern: Pattern = Pattern.compile("\\s+")

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
