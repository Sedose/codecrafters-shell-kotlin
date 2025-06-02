package io.codecrafters.command.type

import io.codecrafters.dto.ExecutableFound
import io.codecrafters.dto.ExecutableLookupResult
import io.codecrafters.dto.ExecutableNotFound
import org.springframework.stereotype.Component
import java.io.File

@Component
class ExecutableLocator {
    fun findExecutable(executableName: String): ExecutableLookupResult {
        val environmentPath = System.getenv("PATH") ?: return ExecutableNotFound
        val pathDirectories = environmentPath.split(File.pathSeparator)

        for (directory in pathDirectories) {
            if (directory.isEmpty()) {
                continue
            }

            val candidateExecutable =
                if (File(executableName).isAbsolute) {
                    File(executableName)
                } else {
                    File(directory, executableName)
                }
            if (candidateExecutable.exists() && candidateExecutable.canExecute()) {
                return ExecutableFound(candidateExecutable.absolutePath)
            }
        }
        return ExecutableNotFound
    }
}
