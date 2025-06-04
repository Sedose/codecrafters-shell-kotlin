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

        var activeQuote: Char? = null          // either '\'' or '"' while inside quotes
        var escapingOutsideQuotes = false      // \  …when we’re **not** inside any quotes
        var i = 0

        fun flushCurrentToken() {
            if (buffer.isNotEmpty()) {
                tokens += buffer.toString()
                buffer.clear()
            }
        }

        while (i < input.length) {
            val ch = input[i]

            when {
                /* ------------------------------------------------------------
                 * 1. backslash handling outside any quotes
                 * ------------------------------------------------------------ */
                escapingOutsideQuotes -> {
                    buffer.append(ch)
                    escapingOutsideQuotes = false
                }

                activeQuote == null && ch == '\\' -> {
                    escapingOutsideQuotes = true
                }

                /* ------------------------------------------------------------
                 * 2. backslash handling **inside** double quotes
                 *    (preserve its special meaning only before \, $, " or \n)
                 * ------------------------------------------------------------ */
                activeQuote == '"' && ch == '\\' -> {
                    val next = input.getOrNull(i + 1)
                    if (next == '\\' || next == '"' || next == '$' || next == '\n') {
                        buffer.append(next)
                        i++                               // skip the escaped char
                    } else {
                        buffer.append(ch)                 // treat the \ literally
                    }
                }

                /* ------------------------------------------------------------
                 * 3. quote open / close
                 * ------------------------------------------------------------ */
                ch == '\'' || ch == '"' -> {
                    when (activeQuote) {
                        null -> activeQuote = ch          // opening quote
                        ch   -> activeQuote = null        // closing the same quote
                        else -> buffer.append(ch)         // quote char inside other quotes
                    }
                }

                /* ------------------------------------------------------------
                 * 4. token boundary on unquoted whitespace
                 * ------------------------------------------------------------ */
                ch.isWhitespace() && activeQuote == null -> flushCurrentToken()

                /* ------------------------------------------------------------
                 * 5. regular character
                 * ------------------------------------------------------------ */
                else -> buffer.append(ch)
            }

            i++
        }

        if (escapingOutsideQuotes) buffer.append('\\')    // dangling backslash at EOL
        flushCurrentToken()
        return tokens
    }
}
