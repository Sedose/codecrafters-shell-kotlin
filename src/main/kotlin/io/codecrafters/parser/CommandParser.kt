package io.codecrafters.parser

import org.springframework.stereotype.Component

data class ParsedCommand(
    val commandName: String,
    val arguments: List<String>,
)

@Component
class CommandParser {
    fun parse(input: String): ParsedCommand {
        val tokens = mutableListOf<String>()
        val currentToken = StringBuilder()
        var insideQuotes = false
        for (character in input) {
            when {
                character == '\'' -> insideQuotes = !insideQuotes
                character.isWhitespace() && !insideQuotes -> {
                    if (currentToken.isNotEmpty()) {
                        tokens.add(currentToken.toString())
                        currentToken.clear()
                    }
                }
                else -> currentToken.append(character)
            }
        }
        if (currentToken.isNotEmpty()) tokens.add(currentToken.toString())
        val name = tokens.firstOrNull() ?: ""
        val args = if (tokens.size > 1) tokens.subList(1, tokens.size) else emptyList()
        return ParsedCommand(name, args)
    }
}
