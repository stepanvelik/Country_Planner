package com.example.homework4.list

enum class CountrySortMode {
    NAME,
    POPULATION,
    AREA
}

enum class SortDirection {
    ASCENDING,
    DESCENDING
}

fun CountrySortMode.label(): String =
    when (this) {
        CountrySortMode.NAME -> "По алфавиту"
        CountrySortMode.POPULATION -> "Население"
        CountrySortMode.AREA -> "Площадь"
    }

fun SortDirection.next(): SortDirection =
    when (this) {
        SortDirection.ASCENDING -> SortDirection.DESCENDING
        SortDirection.DESCENDING -> SortDirection.ASCENDING
    }

