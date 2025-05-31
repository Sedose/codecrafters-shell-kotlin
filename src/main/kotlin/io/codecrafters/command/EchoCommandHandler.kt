package io.codecrafters.command

import org.springframework.stereotype.Component

@Component
class EchoCommandHandler : CommandHandler {
    override val commandName = "echo"

    override fun handle(commandPayload: String) {
        println(commandPayload)
    }
}
