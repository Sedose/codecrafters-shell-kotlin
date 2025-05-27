package io.codecrafters

import io.codecrafters.command.CommandHandler
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class ShellRunner(
  private val commandHandlerMap: Map<String, CommandHandler>,
) : CommandLineRunner {

  override fun run(vararg args: String) {
    while (true) {
      print("$ ")
      val inputLine = readLine() ?: break
      val trimmedLine = inputLine.trim()
      if (trimmedLine.isEmpty()) continue
      val tokens = trimmedLine.split(Regex("\\s+"))
      val commandName = tokens.first()
      val argumentValues = tokens.drop(1)
      commandHandlerMap[commandName]
        ?.handle(argumentValues.joinToString(" "))
        ?: executeExternalProgram(commandName, argumentValues)
    }
  }

  private fun executeExternalProgram(commandName: String, argumentValues: List<String>) {
    val commandLine =
      buildList {
        add(commandName)
        addAll(argumentValues)
      }
    ProcessBuilder(commandLine)
      .inheritIO()
      .start()
      .waitFor()
  }
}
