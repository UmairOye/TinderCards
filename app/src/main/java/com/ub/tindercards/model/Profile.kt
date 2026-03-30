package com.ub.tindercards.model

data class Profile(
    val id: Int,
    val name: String,
    val age: Int,
    val distance: String,
    val job: String,
    val bio: String,
    val imageRes: Int,
    val tags: List<String>
)
