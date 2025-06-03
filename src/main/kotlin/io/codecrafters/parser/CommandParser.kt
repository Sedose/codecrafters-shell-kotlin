package io.codecrafters.parser

import org.springframework.stereotype.Component

data class ParsedCommand(
    val commandName: String,
    val arguments: List<String>,
)

@Component
class CommandParser {
    fun parse(line: String): ParsedCommand {
        val tokens = tokenize(line)
        return ParsedCommand(
            commandName = tokens.firstOrNull().orEmpty(),
            arguments = tokens.drop(1),
        )
    }

    private fun tokenize(input: String): List<String> {
        val tokens = mutableListOf<String>()
        val buffer = StringBuilder()
        var quoted = false

        fun flush() {
            if (buffer.isNotEmpty()) {
                tokens += buffer.toString()
                buffer.clear()
            }
        }

        input.forEach { ch ->
            when {
                ch == '\'' || ch == '\"' -> quoted = !quoted
                ch.isWhitespace() -> if (quoted) buffer.append(ch) else flush()
                else -> buffer.append(ch)
            }
        }

        flush()
        return tokens
    }
}
