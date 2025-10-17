package ru.wolfram.problems_client.domain.exception

class UnsupportedLanguageException : RuntimeException() {
    override val message = "Language is not supported!"
}