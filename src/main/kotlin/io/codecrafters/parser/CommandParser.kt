package io.codecrafters.parser

import io.codecrafters.dto.ParsedCommand
import org.springframework.stereotype.Component

@Component
class CommandParser {
    fun parse(line: String): ParsedCommand {
        val tokens = tokenize(line)
        val redirectIndex = tokens.indexOfFirst { it == ">" || it == "1>" }.takeUnless { it == -1 }
        val redirectTarget = tokens.getOrNull((redirectIndex ?: Int.MIN_VALUE) + 1)
        val cleanedTokens =
            redirectIndex?.let { index ->
                tokens.filterIndexed { i, _ -> i != index && i != index + 1 }
            } ?: tokens
        return ParsedCommand(
            commandName = cleanedTokens.firstOrNull().orEmpty(),
            arguments = cleanedTokens.drop(1),
            stdoutRedirect = redirectTarget,
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
