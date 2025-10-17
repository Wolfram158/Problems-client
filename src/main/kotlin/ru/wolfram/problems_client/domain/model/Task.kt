package ru.wolfram.problems_client.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val name: String,
    val descriptionMarkdown: String
)