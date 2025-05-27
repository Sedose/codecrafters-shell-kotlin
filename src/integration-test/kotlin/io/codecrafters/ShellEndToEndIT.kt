package io.codecrafters

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.SpringApplication
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.PrintStream

class ShellEndToEndIT {
  @Test
  fun echoCommandPrintsSuppliedArguments() {
    val result = runSession(
      """
                echo Hello, Kotlin End-to-End Test
                exit
            """,
    )
    assertTrue(result.consoleOutput.contains("Hello, Kotlin End-to-End Test"))
  }

  @Test
  fun typeCommandRecognisesBuiltins() {
    val result = runSession(
      """
                type echo
                type exit
                type type
                exit
            """,
    )
    val expectedBuiltins = listOf(
      "echo is a shell builtin",
      "exit is a shell builtin",
      "type is a shell builtin",
    )
    for (builtinStatement in expectedBuiltins) {
      assertTrue(result.consoleOutput.contains(builtinStatement))
    }
  }

  @Test
  fun typeCommandReportsUnknownExecutable() {
    val result = runSession(
      """
                type foobar
                exit
            """,
    )
    assertTrue(result.consoleOutput.contains("foobar: not found"))
  }

  @Test
  fun exitCommandExitsWithExplicitStatusCode() {
    val result = runSession("exit 42")
    assertEquals(42, result.exitStatus)
  }

  fun runSession(script: String): SessionResult {
    val originalInputStream: InputStream = System.`in`
    val originalPrintStream: PrintStream = System.out
    val capturedOutput = ByteArrayOutputStream()

    System.setIn(ByteArrayInputStream((script.trimIndent() + "\n").toByteArray()))
    System.setOut(PrintStream(capturedOutput))

    val status = try {
      SpringApplication.run(arrayOf(Main::class.java, TestExitConfig::class.java), emptyArray<String>())
      0
    } catch (intercepted: ExitInterceptedException) {
      intercepted.status
    } finally {
      System.setIn(originalInputStream)
      System.setOut(originalPrintStream)
    }

    return SessionResult(capturedOutput.toString(), status)
  }
}

data class SessionResult(
  val consoleOutput: String,
  val exitStatus: Int,
)
