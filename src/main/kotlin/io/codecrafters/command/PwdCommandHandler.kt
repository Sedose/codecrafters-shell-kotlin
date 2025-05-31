package io.codecrafters.command

import io.codecrafters.state.ShellState
import org.springframework.stereotype.Component

@Component
class PwdCommandHandler(
    private val shellState: ShellState,
) : CommandHandler {
    override val commandName = "pwd"

    override fun handle(commandPayload: String) {
        println(shellState.currentDirectory)
    }
}
