package ru.wolfram.problems_client.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Solution(
    val language: String,
    val solution: String
)