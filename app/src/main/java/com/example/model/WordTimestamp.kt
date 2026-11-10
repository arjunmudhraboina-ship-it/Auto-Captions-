package com.example.model

data class WordTimestamp(
    val id: String,
    val word: String,
    val startMs: Long,
    val endMs: Long
)
