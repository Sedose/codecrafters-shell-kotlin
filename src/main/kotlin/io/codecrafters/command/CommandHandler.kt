package io.codecrafters.command

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import kotlin.system.exitProcess

interface CommandHandler {
  val commandName: String
  fun handle(arguments: String)
}

@Component
class ExitCommandHandler : CommandHandler {
  override val commandName = "exit"

  override fun handle(arguments: String) {
    val exitCode = arguments.toIntOrNull() ?: 0
    exitProcess(exitCode)
  }
}

@Component
class EchoCommandHandler : CommandHandler {
  override val commandName = "echo"

  override fun handle(arguments: String) {
    println(arguments)
  }
}

@Component
class TypeCommandHandler : CommandHandler {
  override val commandName = "type"

  override fun handle(arguments: String) {
    when (arguments) {
      "echo", "exit", "type" -> println("$arguments is a shell builtin")
      else -> println("$arguments: not found")
    }
  }
}

@Configuration
class CommandConfig {

  @Bean
  fun commandHandlerMap(commandHandlers: List<CommandHandler>): Map<String, CommandHandler> {
    return commandHandlers.associateBy { it.commandName }
  }
}
