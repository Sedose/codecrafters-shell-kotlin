package io.codecrafters.command

import io.codecrafters.ExitExecutor
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Paths

interface CommandHandler {
    val commandName: String

    fun handle(commandPayload: String)
}

@Component
class ExitCommandHandler(
    private val exitExecutor: ExitExecutor,
) : CommandHandler {
    override val commandName = "exit"

    override fun handle(commandPayload: String) {
        exitExecutor.exit(commandPayload.toIntOrNull() ?: 0)
    }
}

@Component
class EchoCommandHandler : CommandHandler {
    override val commandName = "echo"

    override fun handle(commandPayload: String) {
        println(commandPayload)
    }
}

@Component
class TypeCommandHandler : CommandHandler {
    override val commandName = "type"

    private val builtinCommands = setOf("echo", "exit", "type", "pwd")

    override fun handle(commandPayload: String) {
        val commandName = commandPayload
        if (commandName.isEmpty()) {
            println("type: missing operand")
            return
        }

        if (commandName in builtinCommands) {
            println("$commandName is a shell builtin")
            return
        }

        val pathVariable = System.getenv("PATH") ?: return println("$commandName: not found")
        val pathDirectories = pathVariable.split(File.pathSeparator)

        for (directoryPath in pathDirectories) {
            if (directoryPath.isEmpty()) continue
            val candidate = File(directoryPath, commandName)
            if (candidate.exists() && candidate.canExecute()) {
                println("$commandName is ${candidate.absolutePath}")
                return
            }
        }

        println("$commandName: not found")
    }
}

@Component
class PwdCommandHandler : CommandHandler {
    override val commandName: String = "pwd"

    override fun handle(commandPayload: String) {
        val currentDirectory = Paths.get("").toAbsolutePath().normalize()
        println(currentDirectory.toString())
    }
}
