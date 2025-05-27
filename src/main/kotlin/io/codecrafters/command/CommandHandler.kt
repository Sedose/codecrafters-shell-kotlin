package io.codecrafters.command

import io.codecrafters.ExitExecutor
import org.springframework.stereotype.Component

interface CommandHandler {
  val commandName: String

  fun handle(arguments: String)
}

@Component
class ExitCommandHandler(
  private val exitExecutor: ExitExecutor,
) : CommandHandler {
  override val commandName = "exit"

  override fun handle(arguments: String) {
    exitExecutor.exit(arguments.toIntOrNull() ?: 0)
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
