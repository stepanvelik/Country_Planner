package com.example.homework4

import com.example.homework4.data.model.Country

object TestCountries {

    val alpha = Country(
        code = "AL",
        name = "Alphaland",
        capital = "Alpha City",
        region = "Test",
        population = 1_000_000L,
        area = 900_000.0,
        flagUrl = "https://example.com/flag.png"
    )

    val beta = Country(
        code = "BE",
        name = "Betaland",
        capital = "Beta City",
        region = "Test",
        population = 2_000_000L,
        area = 300_000.0,
        flagUrl = "https://example.com/b.png"
    )

    val gamma = Country(
        code = "GA",
        name = "Gammaland",
        capital = "Gamma City",
        region = "Other",
        population = 500_000L,
        area = 2_000_000.0,
        flagUrl = "https://example.com/g.png"
    )
}
