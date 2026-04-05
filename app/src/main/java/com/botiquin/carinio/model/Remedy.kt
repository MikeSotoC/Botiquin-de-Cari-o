package com.botiquin.carinio.model

data class Remedy(
    val icon: String,
    val title: String,
    val subtitle: String,
    val badge: String,
    val detail: String,
    val color: Int,
    val hasBreathTool: Boolean,
    val hasAffirmations: Boolean,
    val hasGratitudeJar: Boolean
)
