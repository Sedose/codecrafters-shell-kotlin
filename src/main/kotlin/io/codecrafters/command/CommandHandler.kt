package io.codecrafters.command

import io.codecrafters.ExitExecutor
import org.springframework.stereotype.Component
import java.io.File

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

  private val builtinCommands = setOf("echo", "exit", "type")

  override fun handle(arguments: String) {
    val command = arguments.trim()
    if (command.isEmpty()) {
      println("type: missing operand")
      return
    }

    if (command in builtinCommands) {
      println("$command is a shell builtin")
      return
    }

    val pathVariable = System.getenv("PATH") ?: return println("$command: not found")
    val pathDirectories = pathVariable.split(File.pathSeparator)

    for (directoryPath in pathDirectories) {
      if (directoryPath.isEmpty()) continue
      val candidate = File(directoryPath, command)
      if (candidate.exists() && candidate.canExecute()) {
        println("$command is ${candidate.absolutePath}")
        return
      }
    }

    println("$command: not found")
  }
}
