package io.codecrafters

import io.codecrafters.command.CommandHandler
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.util.Scanner

@Component
class ShellRunner(
  private val commandHandlerMap: Map<String, CommandHandler>,
  private val executableResolver: ExecutableResolver,
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
    val executableFile = executableResolver.resolve(commandName)
      ?: return println("$commandName: not found")

    val commandLine = buildList {
      add(executableFile.absolutePath)
      addAll(argumentValues)
    }

    val process = ProcessBuilder(commandLine).inheritIO().start()
    process.waitFor()
  }
}
