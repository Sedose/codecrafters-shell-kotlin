package io.codecrafters.command.type

import io.codecrafters.dto.ExecutableFound
import io.codecrafters.dto.ExecutableLookupResult
import io.codecrafters.dto.ExecutableNotFound
import org.springframework.stereotype.Component
import java.io.File

@Component
class ExecutableLocator {
    fun findExecutable(executableName: String): ExecutableLookupResult {
        return (System.getenv("PATH") ?: return ExecutableNotFound)
            .split(File.pathSeparator)
            .asSequence()
            .filter { it.isNotBlank() }
            .map { resolveCandidateExecutable(it, executableName) }
            .firstOrNull { it.exists() && it.canExecute() }
            ?.let { ExecutableFound(it.absolutePath) }
            ?: ExecutableNotFound
    }

    private fun resolveCandidateExecutable(
        directoryPath: String,
        executableName: String,
    ): File =
        if (File(executableName).isAbsolute) {
            File(executableName)
        } else {
            File(directoryPath, executableName)
        }
}
