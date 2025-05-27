package io.codecrafters

import io.codecrafters.command.CommandHandler
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import java.util.Scanner

@SpringBootApplication
class Main

fun main(args: Array<String>) {
  runApplication<Main>(*args)
}

@Component
@ConditionalOnProperty(prefix = "shell", name = ["enabled"], havingValue = "true", matchIfMissing = true)
class ShellRunner(
  private val commandHandlerMap: Map<String, CommandHandler>,
) : CommandLineRunner {
  override fun run(vararg args: String) {
    val scanner = Scanner(System.`in`)
    while (scanner.hasNextLine()) {
      print("$ ")
      val inputLine = scanner.nextLine()
      val command = inputLine.substringBefore(delimiter = ' ')
      val arguments = inputLine.substringAfter(delimiter = ' ', missingDelimiterValue = "")
      commandHandlerMap[command]
        ?.handle(arguments)
        ?: println("$inputLine: command not found")
    }
  }
}
