package io.codecrafters

import org.springframework.stereotype.Component
import java.io.File

@Component
class ExecutableResolver {
  fun resolve(commandName: String): File? {
    val pathEnvironmentVariable = System.getenv("PATH") ?: return null
    for (directoryPath in pathEnvironmentVariable.split(File.pathSeparator)) {
      if (directoryPath.isBlank()) continue
      val candidateFile = File(directoryPath, commandName)
      if (candidateFile.exists() && candidateFile.canExecute()) return candidateFile
    }
    return null
  }
}
