package io.codecrafters.external

import io.codecrafters.dto.ExternalProgramExecutionResult
import io.codecrafters.dto.ExternalProgramNotFound
import io.codecrafters.dto.ExternalProgramSuccess
import io.codecrafters.shared_mutable_state.ShellState
import org.springframework.stereotype.Component
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

@Component
class ExternalProgramExecutor(
    private val shellState: ShellState,
) {
    fun execute(
        commandName: String,
        arguments: List<String>,
        stdoutRedirect: Path? = null,
    ): ExternalProgramExecutionResult {
        val commandWithArguments = listOf(commandName) + arguments
        val builder =
            ProcessBuilder(commandWithArguments)
                .directory(shellState.currentDirectory.toFile())

        if (stdoutRedirect == null) {
            builder.redirectErrorStream(true)
        } else {
            stdoutRedirect.parent?.let { Files.createDirectories(it) }
            builder.redirectOutput(stdoutRedirect.toFile())
            builder.redirectError(ProcessBuilder.Redirect.INHERIT)
        }

        return try {
            val process = builder.start()

            if (stdoutRedirect == null) {
                process.inputStream.bufferedReader().forEachLine { println(it) }
            }
            ExternalProgramSuccess(process.waitFor())
        } catch (_: IOException) {
            ExternalProgramNotFound(commandName)
        }
    }
}
