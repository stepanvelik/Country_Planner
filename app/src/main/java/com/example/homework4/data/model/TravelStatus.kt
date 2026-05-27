package com.example.homework4.data.model

enum class TravelStatus {
    WANT_TO_VISIT,
    PLANNED,
    VISITED
}

fun TravelStatus.label(): String =
    when (this) {
        TravelStatus.WANT_TO_VISIT -> "Хочу посетить"
        TravelStatus.PLANNED -> "В плане"
        TravelStatus.VISITED -> "Посетил"
    }

