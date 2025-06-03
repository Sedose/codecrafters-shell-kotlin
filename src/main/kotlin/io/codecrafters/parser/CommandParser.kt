package io.codecrafters.parser

import org.springframework.stereotype.Component

data class ParsedCommand(
    val commandName: String,
    val arguments: List<String>,
)

private data class ParserState(
    val tokens: List<String>,
    val currentToken: String,
    val insideQuotes: Boolean,
)

@Component
class CommandParser {
    fun parse(input: String): ParsedCommand {
        val (tokens, lastToken, _) =
            input.fold(
                ParserState(
                    emptyList(),
                    "",
                    false,
                ),
            ) { (tokens, currentToken, insideQuotes), char ->
                when {
                    char == '\'' ->
                        ParserState(tokens, currentToken, !insideQuotes)

                    char.isWhitespace() && !insideQuotes ->
                        if (currentToken.isNotEmpty()) {
                            ParserState(tokens + currentToken, "", false)
                        } else {
                            ParserState(tokens, currentToken, false)
                        }

                    else ->
                        ParserState(tokens, currentToken + char, insideQuotes)
                }
            }
        val allTokens = if (lastToken.isNotEmpty()) tokens + lastToken else tokens
        val name = allTokens.firstOrNull() ?: ""
        val args = if (allTokens.size > 1) allTokens.drop(1) else emptyList()
        return ParsedCommand(name, args)
    }
}
