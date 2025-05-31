package io.codecrafters.external

import io.codecrafters.dto.ExternalProgramExecutionResult
import io.codecrafters.dto.ExternalProgramNotFound
import io.codecrafters.dto.ExternalProgramSuccess
import io.codecrafters.state.ShellState
import org.springframework.stereotype.Component
import java.util.regex.Pattern
import java.io.IOException

@Component
 class ExternalProgramExecutor(
    private val shellState: ShellState,
    private val argumentSplitPattern: Pattern = Pattern.compile("\\s+"),
     ) {
    fun execute(
        commandName: String,
        argumentList: String,
    ): ExternalProgramExecutionResult {
        val commandWithArguments =
            listOf(commandName) + argumentSplitPattern.split(argumentList).filter { it.isNotEmpty() }

        return try {
            val process =
                ProcessBuilder(commandWithArguments)
                    .directory(shellState.currentDirectory.toFile())
                    .redirectErrorStream(true)
                    .start()

            process.inputStream.bufferedReader().useLines { outputLines ->
                for (outputLine in outputLines) {
                    println(outputLine)
                }
            }

            val exitCode = process.waitFor()
            ExternalProgramSuccess(exitCode)
        } catch (_: IOException) {
            ExternalProgramNotFound(commandName)
        }
    }
}
