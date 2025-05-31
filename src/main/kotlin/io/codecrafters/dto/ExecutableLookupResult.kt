package io.codecrafters.dto

sealed interface ExecutableLookupResult

data class ExecutableFound(
    val absolutePath: String,
) : ExecutableLookupResult

object ExecutableNotFound : ExecutableLookupResult
