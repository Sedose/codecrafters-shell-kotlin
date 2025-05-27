package io.codecrafters

import io.codecrafters.command.CommandHandler
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class ShellRunner(
  private val commandHandlerMap: Map<String, CommandHandler>,
) : CommandLineRunner {

  override fun run(vararg args: String) {
    while (true) {
      print("$ ")
      val inputLine = readLine() ?: break
      val trimmedInput = inputLine.trim()
      if (trimmedInput.isEmpty()) continue
      val tokens = trimmedInput.split(Regex("\\s+"))
      val commandName = tokens.first()
      val argumentList = tokens.drop(1)
      commandHandlerMap[commandName]
        ?.handle(argumentList.joinToString(" "))
        ?: executeExternalProgram(commandName, argumentList)
    }
  }

  private fun executeExternalProgram(commandName: String, argumentList: List<String>) {
    val fullCommand = buildList<String> {
      add(commandName)
      addAll(argumentList)
    }
    try {
      val process = ProcessBuilder(fullCommand)
        .inheritIO()
        .start()
      process.waitFor()
    } catch (_: IOException) {
      println("$commandName: not found")
    }
  }
}
