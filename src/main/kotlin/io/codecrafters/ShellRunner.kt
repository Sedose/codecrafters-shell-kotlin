package io.codecrafters

import io.codecrafters.command.CommandHandler
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class ShellRunner(
    private val commandHandlerMap: Map<String, CommandHandler>,
) : CommandLineRunner {
  private val splitWordsRegex = Regex("\\s+")

    override fun run(vararg args: String) {
        while (true) {
            print("$ ")
            val inputLine = readLine() ?: break
            val trimmedInput = inputLine.trim()
            if (trimmedInput.isEmpty()) continue
            val tokens = trimmedInput.split(splitWordsRegex)
            val commandName = tokens.first()
            val argumentList = tokens.drop(1)
            commandHandlerMap[commandName]
                ?.handle(argumentList)
                ?: executeExternalProgram(commandName, argumentList)
        }
    }

    private fun executeExternalProgram(
        commandName: String,
        argumentList: List<String>,
    ) {
      val commandWithArguments = listOf(commandName) + argumentList
        try {
            ProcessBuilder(commandWithArguments)
                .inheritIO()
                .start()
                .waitFor()
        } catch (_: IOException) {
            println("$commandName: not found")
        }
    }
}
