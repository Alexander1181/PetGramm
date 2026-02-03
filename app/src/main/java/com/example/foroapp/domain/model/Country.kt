package com.example.foroapp.domain.model

data class Country(
    val name: String,
    val flag: String,
    val prefix: String,
    val digits: Int
)

val supportedCountries = listOf(
    Country("Chile", "🇨🇱", "+56", 9),
    Country("Argentina", "🇦🇷", "+54", 10),
    Country("México", "🇲🇽", "+52", 10),
    Country("Perú", "🇵🇪", "+51", 9),
    Country("Colombia", "🇨🇴", "+57", 10),
    Country("España", "🇪🇸", "+34", 9),
    Country("EE.UU", "🇺🇸", "+1", 10)
)
