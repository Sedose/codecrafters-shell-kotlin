package io.codecrafters.command

import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Paths

@Component
class CdCommandHandler : CommandHandler {

    override val commandName = "cd"

    override fun handle(commandPayload: String) {
        val requestedPath = commandPayload.trim()

        if (requestedPath.isEmpty() || !requestedPath.startsWith("/")) {
            println("cd: $requestedPath: No such file or directory")
            return
        }

        val target = Paths.get(requestedPath).normalize()

        if (Files.isDirectory(target)) {
            System.setProperty("user.dir", target.toString())
        } else {
            println("cd: $requestedPath: No such file or directory")
        }
    }
}
