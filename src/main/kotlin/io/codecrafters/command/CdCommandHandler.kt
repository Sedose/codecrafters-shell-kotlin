package io.codecrafters.command

import io.codecrafters.state.ShellState
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Paths

@Component
class CdCommandHandler(
    private val shellState: ShellState,
) : CommandHandler {
    override val commandName = "cd"

    override fun handle(arguments: List<String>) {
        val requested = arguments.first().trim()

        val resolvedPath =
            if (requested == "~") {
                val homeDirectory =
                    System.getenv("HOME")
                        ?: return println("cd: HOME environment variable not set")
                Paths.get(homeDirectory)
            } else {
                val requestedPath = Paths.get(requested)
                if (requestedPath.isAbsolute) {
                    requestedPath
                } else {
                    shellState.currentDirectory.resolve(requestedPath)
                }
            }

        val normalizedTarget = resolvedPath.normalize()

        if (Files.isDirectory(normalizedTarget)) {
            shellState.currentDirectory = normalizedTarget
        } else {
            println("cd: $requested: No such file or directory")
        }
    }
}
