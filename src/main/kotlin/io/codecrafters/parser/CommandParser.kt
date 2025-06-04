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
        data class State(
            val tokens: List<String>,
            val buffer: String,
            val activeQuote: Char?,
            val escapingOutsideQuotes: Boolean,
            val skipNext: Boolean,
        )

        fun State.addBufferedToken(): State =
            if (buffer.isNotEmpty()) copy(tokens = tokens + buffer, buffer = "") else this

        val initialState = State(
            tokens = emptyList(),
            buffer = "",
            activeQuote = null,
            escapingOutsideQuotes = false,
            skipNext = false,
        )

        val finishedState = input.foldIndexed(initialState) { index, state, currentChar ->
            when {
                state.skipNext ->
                    state.copy(skipNext = false)

                state.escapingOutsideQuotes ->
                    state.copy(buffer = state.buffer + currentChar, escapingOutsideQuotes = false)

                state.activeQuote == null && currentChar == '\\' ->
                    state.copy(escapingOutsideQuotes = true)

                state.activeQuote == '"' && currentChar == '\\' -> {
                    val next = input.getOrNull(index + 1)
                    if (next == '\\' || next == '"' || next == '$' || next == '\n') {
                        state.copy(buffer = state.buffer + next, skipNext = true)
                    } else {
                        state.copy(buffer = state.buffer + currentChar)
                    }
                }

                currentChar == '\'' || currentChar == '"' -> when (state.activeQuote) {
                    null -> state.copy(activeQuote = currentChar)
                    currentChar -> state.copy(activeQuote = null)
                    else -> state.copy(buffer = state.buffer + currentChar)
                }

                currentChar.isWhitespace() && state.activeQuote == null ->
                    state.addBufferedToken()

                else ->
                    state.copy(buffer = state.buffer + currentChar)
            }
        }.let { endState ->
            val withDanglingBackslash =
                if (endState.escapingOutsideQuotes) endState.copy(buffer = endState.buffer + '\\')
                else endState
            withDanglingBackslash.addBufferedToken()
        }

        return finishedState.tokens
    }
}
