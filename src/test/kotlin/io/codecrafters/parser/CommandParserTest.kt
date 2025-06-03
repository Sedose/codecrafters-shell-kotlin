package io.codecrafters.parser

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CommandParserTest {
    private val parser = CommandParser()

    @Nested
    inner class BasicTokenization {
        @Test
        fun `parses command without arguments`() {
            val (commandName, arguments) = parser.parse("exit")
            assertEquals("exit", commandName)
            assertTrue(arguments.isEmpty())
        }

        @Test
        fun `parses command with multiple arguments`() {
            val result = parser.parse("echo hello world")
            assertEquals("echo", result.commandName)
            assertEquals(listOf("hello", "world"), result.arguments)
        }

        @Test
        fun `ignores leading and trailing whitespace`() {
            val result = parser.parse("   ls   -l    /tmp   ")
            assertEquals("ls", result.commandName)
            assertEquals(listOf("-l", "/tmp"), result.arguments)
        }
    }

    @Nested
    inner class SingleQuoting {
        @Test
        fun `treats everything inside single quotes as one token`() {
            val result = parser.parse("echo 'hello world'")
            assertEquals("echo", result.commandName)
            assertEquals(listOf("hello world"), result.arguments)
        }

        @Test
        fun `handles multiple quoted segments in one line`() {
            val result = parser.parse("echo 'hello world' test 'foo bar'")
            assertEquals("echo", result.commandName)
            assertEquals(listOf("hello world", "test", "foo bar"), result.arguments)
        }

        @Test
        fun `handles unmatched single quote at end of line`() {
            val result = parser.parse("echo 'foo bar")
            assertEquals("echo", result.commandName)
            assertEquals(listOf("foo bar"), result.arguments)
        }
    }

    @Nested
    inner class DoubleQuoting {
        @Test
        fun `treats everything inside single quotes as one token`() {
            val result = parser.parse("echo \"hello world\"")
            assertEquals("echo", result.commandName)
            assertEquals(listOf("hello world"), result.arguments)
        }

        @Test
        fun `handles multiple quoted segments in one line`() {
            val result = parser.parse("echo \"hello world\" test \"foo bar\"")
            assertEquals("echo", result.commandName)
            assertEquals(listOf("hello world", "test", "foo bar"), result.arguments)
        }

        @Test
        fun `handles unmatched single quote at end of line`() {
            val result = parser.parse("echo \"foo bar")
            assertEquals("echo", result.commandName)
            assertEquals(listOf("foo bar"), result.arguments)
        }
    }

    @Nested
    inner class EdgeCases {
        @Test
        fun `returns empty tokens for empty input`() {
            val result = parser.parse("")
            assertEquals("", result.commandName)
            assertTrue(result.arguments.isEmpty())
        }

        @Test
        fun `returns empty tokens for whitespace-only input`() {
            val result = parser.parse("        ")
            assertEquals("", result.commandName)
            assertTrue(result.arguments.isEmpty())
        }

        @Test
        fun `drops only the first token as command when more than one token present`() {
            val result = parser.parse("cmd arg1")
            assertEquals("cmd", result.commandName)
            assertEquals(listOf("arg1"), result.arguments)
        }
    }
}
