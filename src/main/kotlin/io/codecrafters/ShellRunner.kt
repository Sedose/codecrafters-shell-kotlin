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
            val commandName = trimmedInput.substringBefore(" ")
            val commandPayload = trimmedInput.substringAfter(" ")
            commandHandlerMap[commandName]
                ?.handle(commandPayload)
                ?: executeExternalProgram(commandName, commandPayload)
        }
    }

    private fun executeExternalProgram(
        commandName: String,
        argumentList: String,
    ) {
        val commandWithArguments = listOf(commandName) + argumentList.split(splitWordsRegex)
        try {
            val process =
                ProcessBuilder(commandWithArguments)
                    .redirectErrorStream(true)
                    .start()
            process.inputStream.bufferedReader().useLines { lines ->
                for (line in lines) {
                    println(line)
                }
            }
            process.waitFor()
        } catch (_: IOException) {
            println("$commandName: not found")
        }
    }
}
