package io.codecrafters.command

import org.springframework.stereotype.Component
import java.nio.file.Paths

@Component
class PwdCommandHandler : CommandHandler {
    override val commandName: String = "pwd"

    override fun handle(commandPayload: String) {
        val currentDirectory = Paths.get("").toAbsolutePath().normalize()
        println(currentDirectory.toString())
    }
}
