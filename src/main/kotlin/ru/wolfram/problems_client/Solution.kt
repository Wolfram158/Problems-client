package ru.wolfram.problems_client

import kotlinx.serialization.Serializable

@Serializable
data class Solution(
    val language: String,
    val solution: String
)
