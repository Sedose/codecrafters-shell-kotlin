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
        var quoteChar: Char? = null
        var escaping = false

        fun flush() {
            if (buffer.isNotEmpty()) {
                tokens += buffer.toString()
                buffer.clear()
            }
        }

        input.forEach { ch ->
            when {
                escaping -> {
                    buffer.append(ch)
                    escaping = false
                }

                ch == '\\' && quoteChar != '\'' -> {
                    escaping = true
                }

                ch == '\'' || ch == '\"' -> {
                    when (quoteChar) {
                        null -> quoteChar = ch
                        ch -> quoteChar = null
                        else -> buffer.append(ch)
                    }
                }

                ch.isWhitespace() && quoteChar == null -> {
                    flush()
                }

                else -> buffer.append(ch)
            }
        }

        if (escaping) buffer.append('\\')

        flush()
        return tokens
    }
}
