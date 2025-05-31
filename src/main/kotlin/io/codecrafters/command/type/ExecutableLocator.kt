package io.codecrafters.command.type

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

            val candidateExecutable = File(directory, executableName)
            if (candidateExecutable.exists() && candidateExecutable.canExecute()) {
                return ExecutableFound(candidateExecutable.absolutePath)
            }
        }
        return ExecutableNotFound
    }
}
