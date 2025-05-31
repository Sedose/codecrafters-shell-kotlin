package io.codecrafters.command.type

sealed interface ExecutableLookupResult

data class ExecutableFound(val absolutePath: String) : ExecutableLookupResult

object ExecutableNotFound : ExecutableLookupResult
