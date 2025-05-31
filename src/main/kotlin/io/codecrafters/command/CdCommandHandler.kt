package io.codecrafters.command

import io.codecrafters.state.ShellState
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Paths

@Component
class CdCommandHandler(
    private val shellState: ShellState,
) : CommandHandler {

    override val commandName: String = "cd"

    override fun handle(commandPayload: String) {
        val requested = commandPayload.trim()

        if (requested.isEmpty()) {
            println("cd: $requested: No such file or directory")
            return
        }

        val rawTarget = Paths.get(requested).let { p ->
            if (p.isAbsolute) p else shellState.currentDirectory.resolve(p)
        }

        val target = rawTarget.normalize()

        if (Files.isDirectory(target)) {
            shellState.currentDirectory = target
        } else {
            println("cd: $requested: No such file or directory")
        }
    }
}
